package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.service.ClientCreditMemoService;
import com.mikealbert.accounting.processor.service.ClientInvoiceDepositService;
import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;


@SpringBootTest
@DisplayName("Given a client's invoice group update")
class ClientTransactionGroupQueueProcessorTest extends BaseTest {
	@Resource ClientTransactionGroupQueueProcessor clientTransactionGroupQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientCreditMemoService clientCreditMemoService;
	@MockBean ClientInvoiceDepositService clientInvoiceDepositService;
	@MockBean ClientInvoiceService clientInvoiceService;
	@MockBean ClientTransactionGroupService clientTransactionGroupService;
	@MockBean ClientService clientService;
	@MockBean MessageLogService messageLogService;

	static final String EXPECTED_DERIVED_ACCOUNT_CODE = "nnnnnnnn";

	String jsonPayload;
	ClientTransactionGroupVO mockClientTransactionGroupVO;

	@BeforeEach
	void init() throws Exception {
		mockClientTransactionGroupVO = generateMockClientTransactionGroupVO();
		jsonPayload = new ObjectMapper().writeValueAsString(mockClientTransactionGroupVO);
	}
	
	@Test
	@DisplayName("when new invoice group, then delegate processing of the invoice group details")
	void testProcess() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(clientService.parseAccountCodeFromExternalId(any())).thenReturn(EXPECTED_DERIVED_ACCOUNT_CODE);
		when(clientCreditMemoService.findByTransactionGroup(any())).thenReturn(new ArrayList<>(0));
		when(clientInvoiceService.findByTransactionGroup(any())).thenReturn(new ArrayList<>(0));
	    when(clientInvoiceDepositService.deposits(any())).thenReturn(new ArrayList<>(0));
		when(clientTransactionGroupService.formatMessageId(any())).thenReturn("");
		when(messageLogService.find(any(), anyString())).thenReturn(null);

		doNothing().when(clientCreditMemoService).process(anyList());
		doNothing().when(clientInvoiceDepositService).process(anyList());
		doNothing().when(clientTransactionGroupService).process(any());
		doNothing().when(messageLogService).end(any(), any());

		clientTransactionGroupQueueProcessor.process(ex);

		verify(clientService, times(1)).parseAccountCodeFromExternalId(eq(mockClientTransactionGroupVO.getClientExternalId()));
		verify(clientCreditMemoService, times(1)).findByTransactionGroup(eq(mockClientTransactionGroupVO));
		verify(clientCreditMemoService, times(1)).process(anyList());
		verify(clientInvoiceService, times(1)).findByTransactionGroup(any());
		verify(clientInvoiceDepositService, times(1)).deposits(anyList());
		verify(clientInvoiceDepositService, times(1)).process(anyList());
		verify(clientTransactionGroupService, times(1)).process(eq(mockClientTransactionGroupVO));
		verify(clientTransactionGroupService, times(2)).formatMessageId(eq(mockClientTransactionGroupVO));
		verify(messageLogService, times(1)).end(eq(EventEnum.GROUP_TRANSACTION), eq(""));

	}

	@Test
	@DisplayName("when transaction group has previously been processed, then ignore the invoice group, i.e. do nothing")
	void testProcessOfPreviouslyProccessedTransactionGroup() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
	
		MessageLog mockMessageLog = new MessageLog(EventEnum.GROUP_TRANSACTION.name(), "0|0|0");
		mockMessageLog.setEndDate(new Date());

		when(clientService.parseAccountCodeFromExternalId(any())).thenReturn(EXPECTED_DERIVED_ACCOUNT_CODE);
		when(clientCreditMemoService.findByTransactionGroup(any())).thenReturn(new ArrayList<>(0));
		when(clientInvoiceService.findByTransactionGroup(any())).thenReturn(new ArrayList<>(0));
	    when(clientInvoiceDepositService.deposits(any())).thenReturn(new ArrayList<>(0));
		when(clientTransactionGroupService.formatMessageId(any())).thenReturn("");
		when(messageLogService.find(any(), anyString())).thenReturn(mockMessageLog);

		doNothing().when(clientCreditMemoService).process(anyList());
		doNothing().when(clientInvoiceDepositService).process(anyList());
		doNothing().when(clientTransactionGroupService).process(any());
		doNothing().when(messageLogService).end(any(), any());

		clientTransactionGroupQueueProcessor.process(ex);

		verify(clientService, times(1)).parseAccountCodeFromExternalId(eq(mockClientTransactionGroupVO.getClientExternalId()));
		verify(clientCreditMemoService, times(0)).findByTransactionGroup(eq(mockClientTransactionGroupVO));
		verify(clientCreditMemoService, times(0)).process(anyList());
		verify(clientInvoiceService, times(0)).findByTransactionGroup(any());
		verify(clientInvoiceDepositService, times(0)).deposits(anyList());
		verify(clientInvoiceDepositService, times(0)).process(anyList());
		verify(clientTransactionGroupService, times(0)).process(eq(mockClientTransactionGroupVO));
		verify(clientTransactionGroupService, times(1)).formatMessageId(eq(mockClientTransactionGroupVO));
		verify(messageLogService, times(0)).end(eq(EventEnum.GROUP_TRANSACTION), eq(""));

	}

	private ClientTransactionGroupVO generateMockClientTransactionGroupVO() throws Exception {
		return new ClientTransactionGroupVO()
				.setAccountingPeriodId("0")
				.setAccountingPeriodDate(new Date())
				.setClientInternalId("-1")
				.setClientExternalId("1Cnnnnnnnn")
				.setGroupNumber("nnnn")
				.setClientAccountCode(EXPECTED_DERIVED_ACCOUNT_CODE);
	}

}

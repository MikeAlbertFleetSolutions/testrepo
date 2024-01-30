package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.util.data.DateUtil;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("Given zero to many client invoice group updates")
class ClientTransactionGroupPollingProcessorTest extends BaseTest {
	@Resource ClientTransactionGroupPollingProcessor clientTransactionGroupPollingProcessor;
	@Resource CamelContext context;	
	
	@MockBean AppLogService appLogService;
	@MockBean ClientTransactionGroupService clientTransactionGroupService;
	@MockBean ClientService clientService;
	@MockBean MessageLogService messageLogService;

	static final String EXPECTED_DERIVED_ACCOUNT_CODE = "nnnnnnnn";

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("accounting_period_id", "0");
		EXPECTED_PAYLOAD.put("ending", DateUtil.convertToDateTimeString(new Date()) );
		EXPECTED_PAYLOAD.put("customer_internal_id", "-1");
		EXPECTED_PAYLOAD.put("customer_external_id", "1Cnnnnnnnn");						
		EXPECTED_PAYLOAD.put("group_number", "nnnn");						
	}

	String jsonPayload;
	String jsonEmptyPayload;	

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
		jsonEmptyPayload = "[]";
	}
	
	@DisplayName("when update(s) exist, then the update(s) are processed")
	@Test
	void testProcess() throws Exception {										
		final Date EXPECTED_DATE = new Date();
		final List<ClientTransactionGroupVO> EXPECTED_CLIENT_INVOICE_GROUP_VOS = Arrays.asList(new ClientTransactionGroupVO());
		final List<String> EXPECTED_MESSAGE_IDS = Arrays.asList("nnn-xxxx-nnnnn");

		Exchange ex = new ExchangeBuilder(context)
		        .withHeader(CustomHeader.POLLER_NAME, PollerEnum.CLIENT_TRANSACTION_GROUPS.getName())
				.withBody(jsonPayload)
				.build();
				
		when(appLogService.getStartDate(any())).thenReturn(EXPECTED_DATE);
		when(appLogService.getEndDate()).thenReturn(EXPECTED_DATE);
		when(clientTransactionGroupService.getUpdates(any(), any())).thenReturn(EXPECTED_CLIENT_INVOICE_GROUP_VOS);
		when(clientTransactionGroupService.findAllClientTransactiongGroupsByAccountingPeriod(any())).thenReturn(EXPECTED_CLIENT_INVOICE_GROUP_VOS);
		when(clientTransactionGroupService.formatMessageIds(any())).thenReturn(EXPECTED_MESSAGE_IDS);
		doNothing().when(messageLogService).start(any(), anyList());

		clientTransactionGroupPollingProcessor.process(ex);

		verify(appLogService, times(1)).getStartDate(eq(PollerEnum.CLIENT_TRANSACTION_GROUPS.getName()));
		verify(appLogService, times(1)).getEndDate();
		verify(clientTransactionGroupService, times(1)).getUpdates(eq(EXPECTED_DATE), eq(EXPECTED_DATE));
		verify(clientTransactionGroupService, times(1)).findAllClientTransactiongGroupsByAccountingPeriod(any());
		verify(clientTransactionGroupService, times(1)).formatMessageIds(eq(EXPECTED_CLIENT_INVOICE_GROUP_VOS));
		verify(messageLogService, times(1)).start(eq(EventEnum.GROUP_TRANSACTION), eq(EXPECTED_MESSAGE_IDS));
	}

	@DisplayName("when no updates, then the processor does not blow up")
	@Test
	void testProcessWithNoUpdates() throws Exception {										
		final Date EXPECTED_DATE = new Date();

		Exchange ex = new ExchangeBuilder(context)
		        .withHeader(CustomHeader.POLLER_NAME, PollerEnum.CLIENT_TRANSACTION_GROUPS.getName())
				.withBody(jsonEmptyPayload)
				.build();
				
		when(appLogService.getStartDate(any())).thenReturn(EXPECTED_DATE);
		when(appLogService.getEndDate()).thenReturn(EXPECTED_DATE);
		when(clientTransactionGroupService.getUpdates(any(), any())).thenReturn(new ArrayList<>(0));
		when(clientTransactionGroupService.formatMessageIds(any())).thenReturn(new ArrayList<>(0));
		doNothing().when(messageLogService).start(any(), anyList());

		clientTransactionGroupPollingProcessor.process(ex);

		verify(appLogService, times(1)).getStartDate(eq(PollerEnum.CLIENT_TRANSACTION_GROUPS.getName()));
		verify(appLogService, times(1)).getEndDate();
		verify(clientTransactionGroupService, times(1)).getUpdates(eq(EXPECTED_DATE), eq(EXPECTED_DATE));
		verify(clientTransactionGroupService, times(1)).formatMessageIds(eq(new ArrayList<>(0)));
		verify(messageLogService, times(1)).start(eq(EventEnum.GROUP_TRANSACTION), eq(new ArrayList<>(0)));
	}
	
}

package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.CreditMemoSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CreditMemoSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a request")
public class ClientCreditMemoServiceTest extends BaseTest{
	@Resource ClientCreditMemoService clientCreditMemoService;

	@MockBean CreditMemoSuiteTalkService creditMemoSuiteTalkService;
	@MockBean CreditMemoSuiteAnalyticsService creditMemoSuiteAnalyticsService;
	@MockBean ClientTransactionDAO clientTransactionDAO;

	@BeforeEach
	void up() throws Exception {}

	@Test
	@DisplayName("when multiple grouped credit memos are update, then all grouped credit memos are sent to the DB for further processing")
	public void testProcessMultiplePayments() throws Exception {
		List<ClientCreditMemoVO> mockClientCreditMemoVOs = new ArrayList<>(0);
		mockClientCreditMemoVOs.add(generatedMockClientCreditMemoVO().setGroupNumber("xxxx"));
		mockClientCreditMemoVOs.add(generatedMockClientCreditMemoVO().setGroupNumber("xxxx"));	
		
		when(creditMemoSuiteTalkService.get(anyString(), anyString())).thenReturn(mockClientCreditMemoVOs.get(0));
		doNothing().when(clientTransactionDAO).processCreditMemo(any());

		clientCreditMemoService.process(mockClientCreditMemoVOs);

		verify(creditMemoSuiteTalkService, times(2)).get(anyString(), anyString());
		verify(clientTransactionDAO, times(2)).processCreditMemo(any());			
	}

	@Test
	@DisplayName("when a not to be grouped credit memo is updated, then the updated credit memo is sent to the DB for further processing")	
	public void testProcessNewPayment() throws Exception {
		ClientCreditMemoVO clientCreditMemoVOMock = generatedMockClientCreditMemoVO()
				.setGrouped(false)
				.setGroupNumber(null);

		when(creditMemoSuiteTalkService.get(anyString(), anyString())).thenReturn(clientCreditMemoVOMock);
		doNothing().when(clientTransactionDAO).processCreditMemo(any());

		clientCreditMemoService.process(clientCreditMemoVOMock);

		verify(creditMemoSuiteTalkService, times(1)).get(eq(clientCreditMemoVOMock.getInternalId()), eq(clientCreditMemoVOMock.getExternalId()));
		verify(clientTransactionDAO, times(1)).processCreditMemo(eq(clientCreditMemoVOMock));

		assertEquals("xxx", clientCreditMemoVOMock.getTranId());
	}	

	@Test
	@DisplayName("when a to be grouped credit memo is updated with a group no, then the updated credit memo is sent to the DB for further processing")	
	public void testProcessNewPaymentWithGroupNo() throws Exception {
		ClientCreditMemoVO clientCreditMemoVOMock = generatedMockClientCreditMemoVO()
				.setGroupNumber("xxxx");

		when(creditMemoSuiteTalkService.get(anyString(), anyString())).thenReturn(clientCreditMemoVOMock);
		doNothing().when(clientTransactionDAO).processCreditMemo(any());

		clientCreditMemoService.process(clientCreditMemoVOMock);

		verify(creditMemoSuiteTalkService, times(1)).get(eq(clientCreditMemoVOMock.getInternalId()), eq(clientCreditMemoVOMock.getExternalId()));
		verify(clientTransactionDAO, times(1)).processCreditMemo(eq(clientCreditMemoVOMock));

		assertEquals("xxxx", clientCreditMemoVOMock.getGroupNumber());
	}
	
	@Test
	@DisplayName("when to be grouped credit memo w/o a group no is updated, then the credit memo is NOT sent to the DB for further processing")	
	public void testProcessNewPaymentWithIsGroupedAndNoGroupNo() throws Exception {
		ClientCreditMemoVO clientCreditMemoVOMock = generatedMockClientCreditMemoVO();

		when(creditMemoSuiteTalkService.get(anyString(), anyString())).thenReturn(clientCreditMemoVOMock);
		doNothing().when(clientTransactionDAO).processCreditMemo(any());

		clientCreditMemoService.process(clientCreditMemoVOMock);

		verify(creditMemoSuiteTalkService, times(1)).get(eq(clientCreditMemoVOMock.getInternalId()), eq(clientCreditMemoVOMock.getExternalId()));
		verify(clientTransactionDAO, times(0)).processCreditMemo(eq(clientCreditMemoVOMock));

	}
	
	@Test@
	DisplayName("when find by an invoice group, then the call is made with the appriopriate parameters to get the respective credit memos")
	public void testFindByGroupedInvoice() throws Exception {
		final String CLIENT_INTERNAL_ID = "0";
		final String ACCOUNTING_PERIOD_ID = "-1";
		final String GROUP_INVOICE_LINK_ID = "-2";

		ClientTransactionGroupVO mockClientTransactionGroupVO = new ClientTransactionGroupVO()
		       .setClientInternalId(CLIENT_INTERNAL_ID)
			   .setAccountingPeriodId(ACCOUNTING_PERIOD_ID)
			   .setGroupNumber(GROUP_INVOICE_LINK_ID);

		when(creditMemoSuiteAnalyticsService.findByCustomerAndAccountingPeriod(any(), any())).thenReturn(new ArrayList<>());

		clientCreditMemoService.findByTransactionGroup(mockClientTransactionGroupVO);

		verify(creditMemoSuiteAnalyticsService, times(1)).findByCustomerAndAccountingPeriod(eq(mockClientTransactionGroupVO.getClientInternalId()), eq(mockClientTransactionGroupVO.getAccountingPeriodId().toString()));
	}

	private ClientCreditMemoVO generatedMockClientCreditMemoVO() {
		return new ClientCreditMemoVO()
				.setInternalId("-" + System.currentTimeMillis())
				.setExternalId("xx")
				.setTranId("xxx")
				.setDocId(0L)
				.setDocLineId(1L)
				.setGrouped(true);
	}
}

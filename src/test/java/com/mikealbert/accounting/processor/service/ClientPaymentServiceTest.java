package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerPaymentSuiteTalkService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;
import com.mikealbert.accounting.processor.vo.ClientVO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a request")
public class ClientPaymentServiceTest extends BaseTest{
	@Resource ClientPaymentService clientPaymentService;

	@MockBean CustomerPaymentSuiteTalkService customerPaymentSuiteTalkService;
	@MockBean TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	@MockBean ClientInvoiceService clientInvoiceService;

	@BeforeEach
	void up() throws Exception {}

	@Test
	@DisplayName("when request the last payment applied to an invoice, the applied payment with the most recent posted date is returned")
	public void testGetInvoiceLastPayment() throws Exception {
		ClientPaymentApplyVO mockClientPaymentVO = new ClientPaymentApplyVO();

		List<Map<String, Object>> mockPaymentsMap = new ArrayList<>(0);
		mockPaymentsMap.add(new HashMap<>());	
		mockPaymentsMap.get(0).put("internal_id", "00000000");
		mockPaymentsMap.get(0).put("trandate", new Timestamp(new Date().getTime()));
		mockPaymentsMap.get(0).put("transaction_type", "Payment");
		Thread.sleep(10);
		mockPaymentsMap.add(new HashMap<>());
		mockPaymentsMap.get(1).put("internal_id", "00000001");
		mockPaymentsMap.get(1).put("trandate", new Timestamp(new Date().getTime()));		
		mockPaymentsMap.get(1).put("transaction_type", "Payment");		

		when(clientInvoiceService.get(any(), any())).thenReturn(new ClientInvoiceVO("00000000", null));
		when(transactionSuiteAnalyticsService.findPaymentsByInvoice(anyString(), isNull())).thenReturn(mockPaymentsMap);
		when(customerPaymentSuiteTalkService.getPaymentApply(anyString(), anyString(), any(), anyString())).thenReturn(mockClientPaymentVO);		


		ClientPaymentApplyVO actualClientPaymentVO = clientPaymentService.getInvoiceLastPayment("00000000");

		verify(transactionSuiteAnalyticsService, times(1)).findPaymentsByInvoice(eq("00000000"), isNull());
		verify(customerPaymentSuiteTalkService, times(1)).getPaymentApply(eq("00000000"), eq("00000001"), isNull(), eq("Payment"));

		assertEquals(mockClientPaymentVO, actualClientPaymentVO);
	}

	@Test
	@DisplayName("when request the last payment made by the client, the correct parameters are passed to the method that returns client's last payment")
	public void testGetClientLastPayment() throws Exception {
		ClientVO mockClientVO = new ClientVO()
		        .setInternalId("nnnnnnnn")
				.setExternalId("1Cnnnnnnnn");

		when(customerPaymentSuiteTalkService.getPayment(any(), any())).thenReturn(new ClientPaymentVO());

		clientPaymentService.getClientLastPayment(mockClientVO.getInternalId(), mockClientVO.getExternalId());

		verify(customerPaymentSuiteTalkService, times(1)).getLastPayment(eq(mockClientVO.getInternalId()), eq(mockClientVO.getExternalId()));
	}	
	
}

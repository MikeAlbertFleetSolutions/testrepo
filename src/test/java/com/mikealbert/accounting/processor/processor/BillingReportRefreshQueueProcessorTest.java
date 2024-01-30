package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mikealbert.accounting.processor.service.BillingReportEnrichmentService;
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@SpringBootTest
@DisplayName("A Request")
class BillingReportRefreshQueueProcessorTest extends BaseTest {
	@Resource BillingReportRefreshQueueProcessor billingReportRefreshQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean BillingReportService billingReportService;
	@MockBean ServiceCache serviceCache;
	@MockBean BillingReportEnrichmentService billingReportEnrichmentService;
	@MockBean MessageLogService messageLogService;
	
	static final String ACCOUNT_CODE = "00000000";
	static final String REPORT_NAME = null;
	static final String START_PERIOD = "JAN-2022";
	static final String END_PERIOD = "JAN-2022";
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("accountCode", ACCOUNT_CODE);
		EXPECTED_PAYLOAD.put("startPeriod", START_PERIOD);
		EXPECTED_PAYLOAD.put("endPeriod", END_PERIOD);		
		EXPECTED_PAYLOAD.put("reportName", REPORT_NAME);
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is to refresh the client's billing report tranactions, then the transactions are updated")
	void testProcess() throws Exception {		
		Exchange ex = new ExchangeBuilder(context)
		    .withBody(jsonPayload)
		    .build();

		List<AccountingPeriodVO> mockAccountingPeriodVOs = new ArrayList<>(0);
		mockAccountingPeriodVOs.add(
			new AccountingPeriodVO()
			    .setName(START_PERIOD) );

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>(0);
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setAccountCode(ACCOUNT_CODE)
		);

		String messageIdPrefix = String.format("%s|%s", ACCOUNT_CODE, START_PERIOD);
		String messageId = String.format("%s|%s|%s", ACCOUNT_CODE, START_PERIOD, ex.getIn().getExchange().getExchangeId());

		when(billingReportService.get(any(), any(), any())).thenReturn(mockBillingReportTransactionVOs);
		when(billingReportService.filterReportWorthy(any(), any())).thenReturn(mockBillingReportTransactionVOs);
		when(serviceCache.findAccountingPeriodByNameRange(any(), any())).thenReturn(mockAccountingPeriodVOs);
		when(messageLogService.findWithPartialMessageId(any(), any())).thenReturn(new ArrayList<>(0));	
		doNothing().when(messageLogService).start(any(), anyString());	
		doNothing().when(messageLogService).end(any(), anyString());
		doNothing().when(billingReportService).deleteFromInternalStore(any(), any());		
		doNothing().when(billingReportService).upsertInternalStore(any(), anyBoolean());
		doNothing().when(billingReportService).mergeInternalStore(any(), any(), anyBoolean());
					
		billingReportRefreshQueueProcessor.process(ex);

		verify(serviceCache, times(1)).findAccountingPeriodByNameRange(eq(START_PERIOD), eq(END_PERIOD));
		verify(messageLogService, times(1)).findWithPartialMessageId(eq(EventEnum.REFRESH_BILLING_REPORT), eq(messageIdPrefix));
		verify(messageLogService, times(1)).start(eq(EventEnum.REFRESH_BILLING_REPORT), eq(messageId));
		verify(messageLogService, times(1)).end(eq(EventEnum.REFRESH_BILLING_REPORT), eq(messageId));		
		verify(billingReportService, times(0)).deleteFromInternalStore(eq(ACCOUNT_CODE), eq(mockAccountingPeriodVOs));
		verify(billingReportService, times(1)).get(eq(ACCOUNT_CODE), eq(mockAccountingPeriodVOs), isNull());
		verify(billingReportService, times(1)).filterReportWorthy(eq(ACCOUNT_CODE), eq(mockBillingReportTransactionVOs));	
		verify(billingReportService, times(1)).upsertInternalStore(eq(mockBillingReportTransactionVOs), eq(false));
		verify(billingReportService, times(1)).mergeInternalStore(eq(ACCOUNT_CODE), eq(mockAccountingPeriodVOs), eq(false));
	}

	@DisplayName("when processing raises an exception, and end date is stamped onto the message log")
	@Test
	void testProcessWithException() throws Exception {
		Exchange ex = new ExchangeBuilder(context)
		    .withBody(jsonPayload)
		    .build();

			String messageId = String.format("%s|%s|%s", ACCOUNT_CODE, START_PERIOD, ex.getIn().getExchange().getExchangeId());

			when(serviceCache.findAccountingPeriodByNameRange(any(), any())).thenThrow(new Exception("Mock"));
			doNothing().when(messageLogService).start(any(), anyString());	
			doNothing().when(messageLogService).end(any(), anyString());

			assertThrows(Exception.class, () -> {
			    billingReportRefreshQueueProcessor.process(ex);			
			});

			verify(messageLogService, times(1)).start(eq(EventEnum.REFRESH_BILLING_REPORT), eq(messageId));
			verify(messageLogService, times(1)).end(eq(EventEnum.REFRESH_BILLING_REPORT), eq(messageId));
	
	}
}

package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
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
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;
import com.mikealbert.util.data.DateUtil;


@SpringBootTest
@DisplayName("Given zero to many client invoice group updates")
class BillingReportRefreshPollingProcessorTest extends BaseTest {
	@Resource BillingReportRefreshPollingProcessor billingReportRefreshPollingProcessor;
	@Resource CamelContext context;	
	
	@MockBean AppLogService appLogService;
	@MockBean BillingReportService billingReportService;

	static final Date EXPECTED_FROM = new Date();
	static final Date EXPECTED_TO = new Date();	

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("customer_external_id", "0");
		EXPECTED_PAYLOAD.put("period_name", "JAN-2022");
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
		final Date EXPECTED_BASE =  DateUtil.convertToDate(super.clientBillingTransactionBaseDate, DateUtil.PATTERN_DATE);

		Exchange ex = new ExchangeBuilder(context)
		        .withHeader(CustomHeader.POLLER_NAME, PollerEnum.BILLING_REPORT_REFRESH.getName())
				.withBody(jsonPayload)
				.build();
				
		
		List<BillingReportRefreshMessageVO> mockMessages = new ArrayList<>(0);
		mockMessages.add(
			new BillingReportRefreshMessageVO()
			        .setAccountCode("0")
					.setStartPeriod("JAN-2022")
					.setEndPeriod("JAN-2022")
		);

		when(appLogService.getStartDate(any())).thenReturn(EXPECTED_FROM);
		when(appLogService.getEndDate()).thenReturn(EXPECTED_TO);				
		when(billingReportService.findAndDispatchUpdates(any(), any(), any())).thenReturn(mockMessages);
		
		billingReportRefreshPollingProcessor.process(ex);

		assertEquals(mockMessages, ex.getIn().getBody());

		verify(appLogService, times(1)).getStartDate(eq(PollerEnum.BILLING_REPORT_REFRESH.getName()));
		verify(appLogService, times(1)).getEndDate();
		verify(billingReportService, times(1)).findAndDispatchUpdates(eq(EXPECTED_BASE), eq(EXPECTED_FROM), eq(EXPECTED_TO));
	}


	
}

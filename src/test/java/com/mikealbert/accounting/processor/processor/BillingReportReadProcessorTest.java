package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.service.DriverService;
import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.util.data.DateUtil;


@SpringBootTest
@DisplayName("A Client")
class BillingReportReadProcessorTest extends BaseTest {
	@Resource BillingReportReadProcessor billingReportReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean BillingReportService billingReportService;
	@MockBean DriverService driverService;
	@MockBean ServiceCache serviceCache;
	
	static String START_PERIOD = "MON-nnnn";
	static String END_PERIOD = "MON-nnnn";
	static String ACCOUNT_CODE = "nnnnnnnn";	
	static String CUSTOMER_EXT_ID = "1Cnnnnnnnn";
	static String REPORT_NAME = null;
	static String LEASE_TYPE_CE = "Closed End Lease Type";

	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("clientExternalId", CUSTOMER_EXT_ID);
		EXPECTED_PAYLOAD.put("startingAccountingPeriod", START_PERIOD);
		EXPECTED_PAYLOAD.put("endingAccountingPeriod", END_PERIOD);		
		EXPECTED_PAYLOAD.put("reportName", REPORT_NAME);
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is for details, the billng report transactions are returned as JSON")
	void testProcess() throws Exception {
		Exchange ex = new ExchangeBuilder(context)
		.withBody(jsonPayload)
		.build();

		BillingReportTransactionVO mockBillingReportTransactionVO = new BillingReportTransactionVO();

		List<AccountingPeriodVO> accountingPeriodVOs = new ArrayList<>(0);
		
		AccountingPeriodVO accountingPeriodVO = new AccountingPeriodVO().setInternalId("0").setName(START_PERIOD);
		accountingPeriodVOs.add(accountingPeriodVO);

		List<BillingReportTransactionVO> expectedBillingReportTransactionVOs = Arrays.asList(new BillingReportTransactionVO[]{mockBillingReportTransactionVO});
												
		when(billingReportService.get(any(), any(), any())).thenReturn(expectedBillingReportTransactionVOs);
		when(billingReportService.filterReportWorthy(any(), any())).thenReturn(expectedBillingReportTransactionVOs);
		when(serviceCache.findAccountingPeriodByNameRange(any(), any())).thenReturn(accountingPeriodVOs);
					
		billingReportReadProcessor.process(ex);
		
		verify(billingReportService, times(1)).get(eq((String)EXPECTED_PAYLOAD.get("clientExternalId")), eq(accountingPeriodVOs), isNull());
		verify(billingReportService, times(1)).filterReportWorthy(eq(ACCOUNT_CODE), eq(expectedBillingReportTransactionVOs));
		verify(serviceCache, times(1)).findAccountingPeriodByNameRange(eq(START_PERIOD), eq(END_PERIOD));
		
		assertEquals(new ObjectMapper().setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME)).writeValueAsString(expectedBillingReportTransactionVOs), ex.getIn().getBody());
	}
}

package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.BillingReportLeaseService;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class BillingReportLeaseReadProcessorTest extends BaseTest{
	@Resource BillingReportLeaseReadProcessor billingReportLeaseReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean BillingReportLeaseService billingReportLeaseService;

	static final String UNIT_INTERNAL_ID = "";
	static final Date EFFECTIVE_DATE = new Date();

	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("unitInternalId", UNIT_INTERNAL_ID);
		EXPECTED_PAYLOAD.put("effectiveDate", EFFECTIVE_DATE);
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is to find billng report lease by unit's internal id and an effective date, the the matching billing report lease is returned as JSON")
	void testProcess() throws Exception {
		BillingReportLeaseVO expectedBillingReportLeaseVO = new BillingReportLeaseVO();
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(billingReportLeaseService.searchByUnitInternalIdAndEffectiveDate(any(), any())).thenReturn(expectedBillingReportLeaseVO);
					
		billingReportLeaseReadProcessor.process(ex);
		
		verify(billingReportLeaseService, times(1)).searchByUnitInternalIdAndEffectiveDate(eq(UNIT_INTERNAL_ID), eq(EFFECTIVE_DATE));
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedBillingReportLeaseVO), ex.getIn().getBody());
	}
    
}

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
import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class DriverUnitHistorySingleReadProcessorTest extends BaseTest{
	@Resource DriverUnitHistorySingleReadProcessor driverUnitHistorySingleReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean ServiceCache serviceCache;

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
	@DisplayName("when request is for the effective DUH record, the effective DUH record  is returned as JSON")
	void testProcess() throws Exception {
		DriverUnitHistoryVO expectedDuhVO = new DriverUnitHistoryVO();
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(serviceCache.findDuhByUnitInternalIdAndDate(any(), any())).thenReturn(expectedDuhVO);
					
		driverUnitHistorySingleReadProcessor.process(ex);
		
		verify(serviceCache, times(1)).findDuhByUnitInternalIdAndDate(eq(UNIT_INTERNAL_ID), eq(EFFECTIVE_DATE));
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedDuhVO), ex.getIn().getBody());
	}
    
}

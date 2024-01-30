package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class DriverUnitHistoryAllReadProcessorTest extends BaseTest{
	@Resource DriverUnitHistoryAllReadProcessor driverUnitHistoryAllReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean ServiceCache serviceCache;
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is for all DUH record, then all DUH records are returned as JSON")
	void testProcess() throws Exception {
		DriverUnitHistoryVO expectedDuhVO = new DriverUnitHistoryVO();
		
		List<DriverUnitHistoryVO> expectedDuhVOs = new ArrayList<>(0);
		expectedDuhVOs.add(expectedDuhVO);
												
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(serviceCache.finalAllDuhs()).thenReturn(expectedDuhVOs);
					
		driverUnitHistoryAllReadProcessor.process(ex);
		
		verify(serviceCache, times(1)).finalAllDuhs();
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedDuhVOs), ex.getIn().getBody());
	}
    
}

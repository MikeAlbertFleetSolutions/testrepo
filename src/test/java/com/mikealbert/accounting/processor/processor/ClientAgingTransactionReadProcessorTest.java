package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.AgingTransactionService;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("Given a request")
class ClientAgingTransactionReadProcessorTest extends BaseTest {
	@Resource ClientAgingTransactionReadProcessor clientAgingTransactionReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean AgingTransactionService agingTransactionService;

	static final String CLIENT_INTERNAL_ID = "nnnnnnnn";
	static final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";
	static final String AGING_PERIOD = AgingPeriodEnum.AGING_30.name();

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("internalId", CLIENT_INTERNAL_ID);
		EXPECTED_PAYLOAD.put("externalId", CLIENT_EXTERNAL_ID);
		EXPECTED_PAYLOAD.put("agingPeriod", AGING_PERIOD);		
	}

	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}
	
	@Test
	@DisplayName("when requesting transactions within an aging period for a given client, then a call is made to the service method to retrieve the data")
	void testProcess() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();

		when(agingTransactionService.getAging(any(), any(), any())).thenReturn(new ArrayList<>(0));

		clientAgingTransactionReadProcessor.process(ex);

		verify(agingTransactionService, times(1)).getAging(eq(CLIENT_INTERNAL_ID), eq(CLIENT_EXTERNAL_ID), eq(AgingPeriodEnum.AGING_30));
	}
}

package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("Given that all invoice groups have been completed")
class ClientTransactionGroupCompleteQueueProcessorTest extends BaseTest {
	@Resource ClientTransactionGroupCompleteQueueProcessor clientTransactionGroupCompleteQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientTransactionGroupService clientTransactionGroupService;

	static final List<String> EXPECTED_ACCOUNTING_PERIODS = Arrays.asList(new String[]{"0", "1"});

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();		
		EXPECTED_PAYLOAD.put("accountingPeriods", EXPECTED_ACCOUNTING_PERIODS);
	}
	
	String jsonPayload;
	
	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}
	
	@Test
	@DisplayName("when processing, then the correct calls are made to process the complete action")
	void testProcess() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		doNothing().when(clientTransactionGroupService).complete();
		doNothing().when(clientTransactionGroupService).emailComplete(anyList());

		clientTransactionGroupCompleteQueueProcessor.process(ex);

		verify(clientTransactionGroupService, times(1)).complete();
		verify(clientTransactionGroupService, times(1)).emailComplete(EXPECTED_ACCOUNTING_PERIODS);
	}

}

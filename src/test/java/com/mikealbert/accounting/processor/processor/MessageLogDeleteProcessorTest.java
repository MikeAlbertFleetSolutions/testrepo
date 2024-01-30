package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.accounting.processor.service.MessageLogService;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("Given a message log update")
class MessageLogDeleteProcessorTest extends BaseTest {
	@Resource MessageLogDeleteProcessor messageLogDeleteProcessor;
	@Resource CamelContext context;	
	
	@MockBean MessageLogService messageLogService;

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("mlgId", 0);
	}

	String jsonPayload;
	String jsonEmptyPayload;	

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
		jsonEmptyPayload = "[]";
	}
	
	@DisplayName("when deleting a message log, then the message log with the matching id is deleted")
	@Test
	void testProcess() throws Exception {										

		Exchange ex = new ExchangeBuilder(context)
		        .withHeader(CustomHeader.POLLER_NAME, PollerEnum.CLIENT_TRANSACTION_GROUPS.getName())
				.withBody(jsonPayload)
				.build();
	
		doNothing().when(messageLogService).delete(any());
	
		messageLogDeleteProcessor.process(ex);

		verify(messageLogService, times(1)).delete(eq(0L));
	}
	
}

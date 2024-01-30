package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.util.data.DateUtil;

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
class MessageLogUpdateProcessorTest extends BaseTest {
	@Resource MessageLogUpdateProcessor messageLogUpdateProcessor;
	@Resource CamelContext context;	
	
	@MockBean MessageLogService messageLogService;

	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("mlgId", 0);
		EXPECTED_PAYLOAD.put("eventName", "blah");
		EXPECTED_PAYLOAD.put("messageId", "0|0|0");
		EXPECTED_PAYLOAD.put("startDate", DateUtil.convertToDateTimeString(new Date()));
		EXPECTED_PAYLOAD.put("endDate", DateUtil.convertToDateTimeString(new Date()));						
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

		Exchange ex = new ExchangeBuilder(context)
		        .withHeader(CustomHeader.POLLER_NAME, PollerEnum.CLIENT_TRANSACTION_GROUPS.getName())
				.withBody(jsonPayload)
				.build();
	
		doNothing().when(messageLogService).save(any());
	
		messageLogUpdateProcessor.process(ex);

		verify(messageLogService, times(1)).save(isA(MessageLog.class));
	}
	
}

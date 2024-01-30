package com.mikealbert.accounting.processor.processor;

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
import com.mikealbert.accounting.processor.enumeration.ClientFieldEnum;
import com.mikealbert.accounting.processor.service.ClientService;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;


@SpringBootTest
@DisplayName("A client update from the external system")
class ClientQueueProcessorTest extends BaseTest {
	@Resource ClientQueueProcessor clientQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientService clientService;
	@MockBean JmsTemplate jmsTemplate;

	static final String PARENT_ACCOUNT_CODE = "00000001";
	static final String CLIENT_ACCOUNT_CODE = "00000002";

	static final String NEW_CLIENT_PAYLOAD = String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}", ClientFieldEnum.INTERNAL_ID.getScriptId(), CLIENT_ACCOUNT_CODE, ClientFieldEnum.EXTERNAL_ID.getScriptId(), null);
	
	@Test
	@DisplayName("when new client, then the child is updated ")
	void testProcessNewClient() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(NEW_CLIENT_PAYLOAD)
				.build();
		
		@SuppressWarnings("unchecked")
		Map<String, String> clientMapMock = new ObjectMapper().readValue(NEW_CLIENT_PAYLOAD, Map.class);

		when(clientService.getClientParents(ArgumentMatchers.any())).thenReturn(new ArrayList<Map<String, String>>(0));

		clientQueueProcessor.process(ex);

		verify(jmsTemplate, times(0)).convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
		verify(clientService, times(1)).process(ArgumentMatchers.eq(clientMapMock));
	}

	@Test
	@DisplayName("when new child client, then the parent is sent to the queue and the child is updated")
	void testProcessNewChildClient() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(NEW_CLIENT_PAYLOAD)
				.build();
		
		Map<String, String> parentMapMock = new HashMap<>();
		parentMapMock.put(ClientFieldEnum.INTERNAL_ID.getScriptId(), null);
		parentMapMock.put(ClientFieldEnum.EXTERNAL_ID.getScriptId(), "1C" + PARENT_ACCOUNT_CODE);
		parentMapMock.put(ClientFieldEnum.ACCOUNT_CODE.getScriptId(), PARENT_ACCOUNT_CODE); 

		List<Map<String, String>> parentMapsMock = new ArrayList<>(0);
		parentMapsMock.add(parentMapMock);

		@SuppressWarnings("unchecked")
		Map<String, String> clientMapMock = new ObjectMapper().readValue(NEW_CLIENT_PAYLOAD, Map.class);

		when(clientService.getClientParents(ArgumentMatchers.any())).thenReturn(parentMapsMock);

		clientQueueProcessor.process(ex);

		verify(jmsTemplate, times(1)).convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.eq(parentMapMock), ArgumentMatchers.any());
		verify(clientService, times(1)).process(ArgumentMatchers.eq(clientMapMock));
	}
}

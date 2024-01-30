package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.ClientCreditMemoFieldEnum;
import com.mikealbert.accounting.processor.service.ClientCreditMemoService;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;


@SpringBootTest
@DisplayName("An updated credit memo from the external system")
class ClientCreditMemoQueueProcessorTest extends BaseTest {
	@Resource ClientCreditMemoQueueProcessor clientCreditMemoQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientCreditMemoService clientCreditMemoService;
	@MockBean JmsTemplate jmsTemplate;

	static final String INTERNAL_ID = "n";
	static final String EXTERNAL_ID = "n-n";	
	static final String TRAN_ID = "n-n-n";	
	static final String TRANSANCTION_NUMBER = "n-n-n-n";	


	static final Map<String, Object> EXPECTED_PAYLOAD_GROUPED;
	static {
		EXPECTED_PAYLOAD_GROUPED = new HashMap<>();
		EXPECTED_PAYLOAD_GROUPED.put(ClientCreditMemoFieldEnum.INTERNAL_ID.getScriptId(), INTERNAL_ID);
		EXPECTED_PAYLOAD_GROUPED.put(ClientCreditMemoFieldEnum.TRAN_ID.getScriptId(), TRAN_ID);
		EXPECTED_PAYLOAD_GROUPED.put(ClientCreditMemoFieldEnum.EXTERNAL_ID.getScriptId(), EXTERNAL_ID);
	}

	static final Map<String, Object> EXPECTED_PAYLOAD_UNGROUPED;
	static {
		EXPECTED_PAYLOAD_UNGROUPED = new HashMap<>();
		EXPECTED_PAYLOAD_UNGROUPED.put(ClientCreditMemoFieldEnum.INTERNAL_ID.getScriptId(), INTERNAL_ID);
		EXPECTED_PAYLOAD_UNGROUPED.put(ClientCreditMemoFieldEnum.TRAN_ID.getScriptId(), TRAN_ID);
		EXPECTED_PAYLOAD_UNGROUPED.put(ClientCreditMemoFieldEnum.EXTERNAL_ID.getScriptId(), EXTERNAL_ID);
		EXPECTED_PAYLOAD_UNGROUPED.put(ClientCreditMemoFieldEnum.TRANSACTION_NUMBER.getScriptId(), TRANSANCTION_NUMBER);
	}	
	
	String jsonPayloadGrouped;
	String jsonPayloadUngrouped;	
	String jsonEmptyPayload;	

	@BeforeEach
	void init() throws Exception {
		jsonPayloadGrouped = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD_GROUPED);
		jsonPayloadUngrouped = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD_UNGROUPED);		
	}	
	
	@Test
	@DisplayName("when grouped, then the correct details are passed in the call to the appropriate handler")
	void testProcessGrouped() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayloadGrouped)
				.build();
				
		ClientCreditMemoVO clientCreditMemoVO = new ClientCreditMemoVO()
		        .setInternalId(INTERNAL_ID)
				.setExternalId(EXTERNAL_ID)
				.setTranId(TRAN_ID);

		when(clientCreditMemoService.findUpdatedUngroupedCreditMemos(any(), any())).thenReturn(new ArrayList<Map<String, Object>>(0));

		clientCreditMemoQueueProcessor.process(ex);

		verify(jmsTemplate, times(0)).convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
		verify(clientCreditMemoService, times(1)).process(ArgumentMatchers.eq(clientCreditMemoVO));
	}

	@Test
	@DisplayName("when ungrouped, then the correct details are passed in the call to the appropriate handler")
	void testProcessUngrouped() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayloadUngrouped)
				.build();
				
		ClientCreditMemoVO clientCreditMemoVO = new ClientCreditMemoVO()
		        .setInternalId(INTERNAL_ID)
				.setExternalId(EXTERNAL_ID)
				.setTranId(TRAN_ID)
				.setTransactionNumber(TRANSANCTION_NUMBER);

		when(clientCreditMemoService.findUpdatedUngroupedCreditMemos(any(), any())).thenReturn(new ArrayList<Map<String, Object>>(0));

		clientCreditMemoQueueProcessor.process(ex);

		verify(jmsTemplate, times(0)).convertAndSend(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
		verify(clientCreditMemoService, times(1)).process(ArgumentMatchers.eq(clientCreditMemoVO));
	}	
}

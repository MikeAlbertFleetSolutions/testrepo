package com.mikealbert.accounting.processor.processor;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
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
@DisplayName("Given a client's invoice group update")
class ClientTransactionReadProcessorTest extends BaseTest {
	@Resource ClientTransactionReadProcessor clientTransactionReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientTransactionGroupService clientTransactionGroupService;

	static final String ACCOUNTING_PERIOD_ID = "0";
	static final String CLIENT_ACCOUNTL_CODE = "nnnnnnnn";
	static final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";

	static final Map<String, Object> EXPECTED_PAYLOAD_ALL;
	static {
		EXPECTED_PAYLOAD_ALL = new HashMap<>();
		EXPECTED_PAYLOAD_ALL.put("accountingPeriodId", ACCOUNTING_PERIOD_ID);
		EXPECTED_PAYLOAD_ALL.put("clientAccountCode", CLIENT_ACCOUNTL_CODE);
	}

	static final Map<String, Object> EXPECTED_PAYLOAD_GROUPED;
	static {
		EXPECTED_PAYLOAD_GROUPED = new HashMap<>();
		EXPECTED_PAYLOAD_GROUPED.put("accountingPeriodId", ACCOUNTING_PERIOD_ID);
		EXPECTED_PAYLOAD_GROUPED.put("clientAccountCode", CLIENT_ACCOUNTL_CODE);
		EXPECTED_PAYLOAD_GROUPED.put("isGrouped", true);		
	}	

	static final Map<String, Object> EXPECTED_PAYLOAD_UNGROUPED;
	static {
		EXPECTED_PAYLOAD_UNGROUPED = new HashMap<>();
		EXPECTED_PAYLOAD_UNGROUPED.put("accountingPeriodId", ACCOUNTING_PERIOD_ID);
		EXPECTED_PAYLOAD_UNGROUPED.put("clientAccountCode", CLIENT_ACCOUNTL_CODE);
		EXPECTED_PAYLOAD_UNGROUPED.put("isGrouped", false);		
	}	

	String jsonPayloadAll;
	String jsonPayloadGrouped;
	String jsonPayloadUngrouped;		

	@BeforeEach
	void init() throws Exception {
		jsonPayloadAll = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD_ALL);
		jsonPayloadGrouped = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD_GROUPED);
		jsonPayloadUngrouped = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD_UNGROUPED);		
	}
	
	@Test
	@DisplayName("when requesting group transactions within an accounting period for a given client, then a call is made to the service method to retrieve the data")
	void testProcessAll() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayloadAll)
				.build();

		when(clientTransactionGroupService.findGroupTransactions(anyString(), anyString(), anyBoolean())).thenReturn(new ArrayList<>(0));

		clientTransactionReadProcessor.process(ex);

		verify(clientTransactionGroupService, times(1)).findGroupTransactions(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID));	
	}

	@Test
	@DisplayName("when requesting grouped group transactions within an accounting period for a given client, then a call is made to the service method to retrieve the data")
	void testProcessGrouped() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayloadGrouped)
				.build();

		when(clientTransactionGroupService.findGroupTransactions(anyString(), anyString(), anyBoolean())).thenReturn(new ArrayList<>(0));

		clientTransactionReadProcessor.process(ex);

		verify(clientTransactionGroupService, times(1)).findGroupTransactions(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID), eq(true));	
	}	

	@Test
	@DisplayName("when requesting ungrouped group transactions within an accounting period for a given client, then a call is made to the service method to retrieve the data")
	void testProcessUngrouped() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayloadUngrouped)
				.build();

		when(clientTransactionGroupService.findGroupTransactions(anyString(), anyString(), anyBoolean())).thenReturn(new ArrayList<>(0));

		clientTransactionReadProcessor.process(ex);

		verify(clientTransactionGroupService, times(1)).findGroupTransactions(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID), eq(false));	
	}

}

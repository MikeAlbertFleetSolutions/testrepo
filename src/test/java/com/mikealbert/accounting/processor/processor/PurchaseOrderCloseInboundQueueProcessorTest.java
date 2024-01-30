package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;

@SpringBootTest
@DisplayName("A Request")
class PurchaseOrderCloseInboundQueueProcessorTest extends BaseTest {
	@Resource PurchaseOrderCloseInboundQueueProcessor purchaseOrderCloseInboundQueueProcessor;
	@Resource CamelContext context;	
	
	@MockBean PurchaseOrderService purchaseOrderService;
	
	static final String externalId = "12345";
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("externalId", externalId);		
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	void testProcess() throws Exception {		
		Exchange ex = new ExchangeBuilder(context)
		    .withBody(jsonPayload)
		    .build();

		doNothing().when(purchaseOrderService).closeInternal(any());		
					
		purchaseOrderCloseInboundQueueProcessor.process(ex);

		verify(purchaseOrderService, times(1)).closeInternal(eq(Long.valueOf(externalId)));
		
	}

	@Test
	void testProcessWithException() throws Exception {
		
		
		Exchange ex = new ExchangeBuilder(context)
		    .withBody(jsonPayload)
		    .build();

			doThrow(new Exception("Mock")).when(purchaseOrderService).closeInternal(any());
			
			assertThrows(Exception.class, () -> {
			    purchaseOrderCloseInboundQueueProcessor.process(ex);			
			});

	
	}
}

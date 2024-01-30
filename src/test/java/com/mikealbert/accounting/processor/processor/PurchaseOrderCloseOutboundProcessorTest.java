package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkNoRecordFoundException;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;
import com.netsuite.webservices.platform.core_2023_2.Status;
import com.netsuite.webservices.platform.core_2023_2.StatusDetail;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;


@SpringBootTest
@DisplayName("An request to close PO in external system")
class PurchaseOrderCloseOutboundProcessorTest extends BaseTest {
	@Resource PurchaseOrderCloseOutboundProcessor purchaseOrderCloseOutboundProcessor;
	@Resource CamelContext context;	
	
	@MockBean PurchaseOrderService purchaseOrderService;
	@MockBean JmsTemplate jmsTemplate;

	static final Long DOC_ID = 0L;
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
	static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("docId", DOC_ID);
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}	
	
	@Test
	@DisplayName("when purchase order does not exist and retry is maxed out, then the exception is supressed")
	void testProcessWithSuiteTalkNoRecordFoundException() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.withHeader(Exchange.REDELIVERY_COUNTER, 0)
				.build();

		Status mockStatus = new Status();
		mockStatus.setIsSuccess(false);
		mockStatus.setStatusDetail(new StatusDetail[]{new StatusDetail()});

		WriteResponse mockWriteResponse = new WriteResponse();
		mockWriteResponse.setStatus(mockStatus);	

		doThrow(new SuiteTalkNoRecordFoundException("message", mockWriteResponse)).doNothing().when(purchaseOrderService).closeExternal(anyLong()); 

		purchaseOrderCloseOutboundProcessor.process(ex);

		verify(jmsTemplate, times(0)).convertAndSend(anyString(), any(), any());
		verify(purchaseOrderService, times(1)).closeExternal(eq(DOC_ID));
	}

	@Test
	@DisplayName("when exception other than no record found, then the exception is raised")
	void testProcessWithSuiteTalkException() throws Exception {										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.withHeader(Exchange.REDELIVERY_COUNTER, 0)
				.build();

		Status mockStatus = new Status();
		mockStatus.setIsSuccess(false);
		mockStatus.setStatusDetail(new StatusDetail[]{new StatusDetail()});
		mockStatus.getStatusDetail()[0].setMessage("Message error");

		WriteResponse mockWriteResponse = new WriteResponse();
		mockWriteResponse.setStatus(mockStatus);	

		doThrow(new SuiteTalkException("message", mockWriteResponse)).doNothing().when(purchaseOrderService).closeExternal(anyLong()); 

		assertThrows(SuiteTalkException.class, () -> purchaseOrderCloseOutboundProcessor.process(ex));

	}	

}

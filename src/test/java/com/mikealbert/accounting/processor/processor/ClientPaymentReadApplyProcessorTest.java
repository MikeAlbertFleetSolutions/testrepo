package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientPaymentService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("A Client's Invoice externalId")
class ClientPaymentReadApplyProcessorTest extends BaseTest {
	@Resource ClientPaymentApplyReadProcessor clientPaymentReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientInvoiceService clientInvoiceService;
	@MockBean ClientPaymentService clientPaymentService;
	
	static final String PAYLOAD = "{\"internalId\":null, \"externalId\":\"00000001\"}";
	
	@Test
	@DisplayName("when request is for the last payment applied , the client's applied payment details is returned as JSON")
	void testProcess() throws Exception {
		ClientInvoiceVO mockClientInvoiceVO = new ClientInvoiceVO();
		mockClientInvoiceVO.setInternalId("00000000");
		mockClientInvoiceVO.setExternalId("00000001");

		ClientPaymentApplyVO expectedClientPaymentVO = new ClientPaymentApplyVO();
		expectedClientPaymentVO.setInternalId("00000002");
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(PAYLOAD)
				.build();

		when(clientInvoiceService.get(any(), any())).thenReturn(mockClientInvoiceVO);
		when(clientPaymentService.getInvoiceLastPayment(any())).thenReturn(expectedClientPaymentVO);
					
		clientPaymentReadProcessor.process(ex);
		
		verify(clientInvoiceService, times(1)).get(isNull(), eq(mockClientInvoiceVO.getExternalId()));				
		verify(clientPaymentService, times(1)).getInvoiceLastPayment(eq(mockClientInvoiceVO.getInternalId()));		

		assertEquals(new ObjectMapper().writeValueAsString(expectedClientPaymentVO), ex.getIn().getBody());
	}
}

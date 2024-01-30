package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.AgingTransactionService;
import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientPaymentService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("A Client")
class ClientAgingBalanceReadProcessorTest extends BaseTest {
	@Resource ClientReadProcessor clientReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean ClientService clientService;
	@MockBean ClientInvoiceService clientInvoiceService;
	@MockBean ClientPaymentService clientPaymentService;	
	@MockBean AgingTransactionService agingTransactionService;
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static {
		EXPECTED_PAYLOAD = new HashMap<>();
		EXPECTED_PAYLOAD.put("externalId", "nnnnnnnn");
		EXPECTED_PAYLOAD.put("loadAgingDetail", true);
		EXPECTED_PAYLOAD.put("loadPurchaseBalance", true);
	}
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is for details, the client's details is returned as JSON")
	void testProcess() throws Exception {
		ClientVO expectedClientVO = new ClientVO();
		expectedClientVO.setAccountCode("00000000");
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(clientService.get(any(), anyBoolean())).thenReturn(expectedClientVO);
		when(clientInvoiceService.findOutStanding(any(), any(), any())).thenReturn(new ArrayList<>());
		when(clientInvoiceService.sumBalance(any())).thenReturn(BigDecimal.ONE);		
					
		clientReadProcessor.process(ex);
		
		verify(clientService, times(1)).get(eq((String)EXPECTED_PAYLOAD.get("externalId")), eq(true));
		verify(clientInvoiceService, times(1)).findOutStanding(isNull(), eq(expectedClientVO.getExternalId()), eq(ControlCodeEnum.AM_LC));
		verify(clientInvoiceService, times(1)).sumBalance(notNull());				
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedClientVO), ex.getIn().getBody());
	}
}

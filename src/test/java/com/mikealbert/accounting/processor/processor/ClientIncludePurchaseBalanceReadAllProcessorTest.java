package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
@DisplayName("Given a request")
class ClientIncludePurchaseBalanceReadAllProcessorTest extends BaseTest {
	@Resource ClientIncludePurchaseBalanceReadAllProcessor clientIncludePurchaseBalanceReadAllProcessorTest;
	@Resource CamelContext context;	
	
	@MockBean ClientService clientService;
	@MockBean ClientInvoiceService clientInvoiceService;
	@MockBean ClientPaymentService clientPaymentService;	
	@MockBean AgingTransactionService agingTransactionService;
	
	static final Map<String, Object> EXPECTED_PAYLOAD;
    static { EXPECTED_PAYLOAD = new HashMap<>(); }
	
	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}

	@Test
	@DisplayName("when request is for all active clients include purchase balance, the all active clients including purchase balance are returned as JSON")
	void testProcess() throws Exception {
		ClientVO expectedClientVO = new ClientVO()
		        .setAccountCode("00000000")
				.setBalance(BigDecimal.ONE)
				.setUnappliedBalance(BigDecimal.ZERO);

		List<ClientVO> expectedClientVOs = Arrays.asList(new ClientVO[]{expectedClientVO});
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		
		when(clientService.findActive()).thenReturn(expectedClientVOs);
		when(clientInvoiceService.findOutStanding(any(), any(), any())).thenReturn(new ArrayList<>());
		when(clientInvoiceService.sumBalance(any())).thenReturn(BigDecimal.ONE);		
					
		clientIncludePurchaseBalanceReadAllProcessorTest.process(ex);
		
		verify(clientService, times(1)).findActive();
		verify(clientInvoiceService, times(1)).findOutStanding(isNull(), eq(expectedClientVO.getExternalId()), eq(ControlCodeEnum.AM_LC));
		verify(clientInvoiceService, times(1)).sumBalance(notNull());				
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedClientVOs), ex.getIn().getBody());
	}
}

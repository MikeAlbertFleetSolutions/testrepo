package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.LeaseFieldEnum;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("A message")
class LeaseExternalApiReadProcessorTest extends BaseTest {
	@Resource LeaseExternalApiReadProcessor leaseExternalApiReadProcessor;
	@Resource CamelContext context;	
	
	@MockBean LeaseService leaseService;

	static final String LEASE_REQUEST_PAYLOAD = String.format("{\"%s\":\"%s\"}", LeaseFieldEnum.EXTERNAL_ID.getScriptId(), "0-0");
	
	@Test
	@DisplayName("when request is for a lease with schedule, a lease with schedule is placed on the exchange")
	void testProcess() throws Exception {	
		List<LeaseVO> leases = new ArrayList<>(0);
		leases.add(new LeaseVO());
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(LEASE_REQUEST_PAYLOAD)
				.build();
		
		when(leaseService.getExternalLease(anyString(), anyBoolean())).thenReturn(leases);

		leaseExternalApiReadProcessor.process(ex);

		verify(leaseService, times(1)).getExternalLease(eq("0-0"), eq(true));

		@SuppressWarnings("unchecked")
		List<LeaseVO> actualLeases = (List<LeaseVO>)ex.getMessage().getBody();

		assertNotNull(actualLeases);
	}
}

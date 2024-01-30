package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.LeaseVO;


@SpringBootTest
@DisplayName("Given a message")
class LeaseNovateProcessorTest extends BaseTest {
	@Resource LeaseNovateProcessor leaseNovateProcessor;
	@Resource CamelContext context;	
	
	@MockBean LeaseService leaseService;
	@MockBean UnitService unitService;

	static final LeaseVO LEASE_NOVATE_MESSAGE = new LeaseVO()
			.setExternalId("1-1")
			.setFmsId(1L)
			.setInternalProductType("OE");

	static final LeaseVO LEASE_NULL_MESSAGE = null;
	
	@Test
	@DisplayName("when request is to novate a lease, then the neccessary calls are made to novate the lease in the external system")
	void testProcess() throws Exception {	

		Exchange ex = new ExchangeBuilder(context)
				.withBody(LEASE_NOVATE_MESSAGE)
				.build();
		

		doNothing().when(leaseService).novateLease(any());

		leaseNovateProcessor.process(ex);

		verify(leaseService, times(1)).novateLease(eq(LEASE_NOVATE_MESSAGE));


		LeaseVO actualLease = (LeaseVO)ex.getMessage().getBody();

		assertEquals(LEASE_NOVATE_MESSAGE, actualLease);
	}

	@Test
	@DisplayName("when request is to novate inapplicable lease, then no calls are made to novate the lease in the external system")
	void testProcessNullLease() throws Exception {	
												
		Exchange ex = new ExchangeBuilder(context)
				.withBody(LEASE_NULL_MESSAGE)
				.build();
		
		doNothing().when(leaseService).novateLease(any());

		leaseNovateProcessor.process(ex);

		verify(leaseService, times(0)).novateLease(eq(LEASE_NULL_MESSAGE));

        assertNull(ex.getMessage().getBody());
	}	

}
package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.mikealbert.accounting.processor.vo.UnitVO;


@SpringBootTest
@DisplayName("Given a message")
class LeaseNewProcessorTest extends BaseTest {
	@Resource LeaseNewProcessor leaseNewProcessor;
	@Resource CamelContext context;	
	
	@MockBean LeaseService leaseService;
	@MockBean UnitService unitService;

	static final LeaseVO LEASE_NEW_MESSAGE = new LeaseVO()
			.setExternalId("1-1")
			.setFmsId(1L)
			.setInternalProductType("OE");

	static final LeaseVO EXTENSION_LEASE_NEW_MESSAGE = new LeaseVO()
			.setParentExternalId("1")
			.setFmsId(1L)
			.setInternalProductType("OE");
	
	@Test
	@DisplayName("when request is to create a new lease in external system, then the neccessary calls are made to create the lease")
	void testProcess() throws Exception {	
		UnitVO mockUnit = new UnitVO().setFmsId(LEASE_NEW_MESSAGE.getFmsId());
												
		Exchange ex = new ExchangeBuilder(context)
				.withBody(LEASE_NEW_MESSAGE)
				.build();
		
		when(unitService.getUnitInfo(any())).thenReturn(mockUnit);
		when(leaseService.upsertLease(any())).thenReturn("");

		leaseNewProcessor.process(ex);

		verify(leaseService, times(1)).upsertLease(eq(LEASE_NEW_MESSAGE));
		verify(unitService, times(1)).getUnitInfo(eq(mockUnit));

		LeaseVO actualLease = (LeaseVO)ex.getMessage().getBody();

		assertEquals(LEASE_NEW_MESSAGE, actualLease);
	}

	@Test
	@DisplayName("when request is to create a new extension lease in external system, then the neccessary calls are made to create the extension lease")
	void testProcessExtensionLease() throws Exception {	
		UnitVO mockUnit = new UnitVO().setFmsId(EXTENSION_LEASE_NEW_MESSAGE.getFmsId());
												
		Exchange ex = new ExchangeBuilder(context)
				.withBody(EXTENSION_LEASE_NEW_MESSAGE)
				.build();
		
		when(unitService.getUnitInfo(any())).thenReturn(mockUnit);
		when(leaseService.upsertLease(any())).thenReturn("");

		leaseNewProcessor.process(ex);

		verify(leaseService, times(1)).upsertLease(eq(EXTENSION_LEASE_NEW_MESSAGE));
		verify(unitService, times(0)).getUnitInfo(isNull());

		LeaseVO actualLease = (LeaseVO)ex.getMessage().getBody();

		assertEquals(EXTENSION_LEASE_NEW_MESSAGE, actualLease);
	}

}
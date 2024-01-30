package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.service.TaxJurisdictionService;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;


@SpringBootTest
@DisplayName("A cleansed jurisdiction")
class TaxJurisdictionFinderProcessorTest extends BaseTest {
	@Resource TaxJurisdictionFinderProcessor taxJurisdictionFinderProcessor;
	@Resource CamelContext context;	
	
	@MockBean TaxJurisdictionService taxJurisdictionService;
	
	static final String PAYLOAD = "{\"country\":\"USA\",\"region\":\"OH\",\"county\":\"Hamilton\",\"city\":\"Evendale\",\"postalCode\":\"45241-2512\"}";
	

	@Test
	@DisplayName("when request received to find the old jurisdiction, the old jurisdiction is found and returned as JSON")
	void testFindOldJurisdiction() throws Exception {
		TaxJurisdictionVO expectedJurisdiction = new TaxJurisdictionVO("USA", "OH", "061", "EVENDALE", "45241-2512", "3250", "36-061-3250");
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(PAYLOAD)
				.build();
		
		when(taxJurisdictionService.find("USA", "OH", "Hamilton", "Evendale", "45241-2512", "45241-2512")).thenReturn(expectedJurisdiction);
					
		taxJurisdictionFinderProcessor.process(ex);
		
		verify(taxJurisdictionService, times(1)).find(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());		
		
		assertEquals(new ObjectMapper().writeValueAsString(expectedJurisdiction), ex.getIn().getBody());
	}
}

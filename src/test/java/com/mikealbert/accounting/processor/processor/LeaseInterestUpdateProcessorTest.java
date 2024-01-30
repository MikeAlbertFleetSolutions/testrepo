package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.entity.Quotation;
import com.mikealbert.accounting.processor.entity.QuotationModel;
import com.mikealbert.accounting.processor.exception.RetryableException;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.AccountingEventMessageVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;


@SpringBootTest
@DisplayName("Given a message")
class LeaseInterestUpdateProcessorTest extends BaseTest {
	@Resource LeaseInterestUpdateProcessor leaseInterestUpdateProcessor;
	@Resource CamelContext context;	
	
	@MockBean LeaseService leaseService;
	@MockBean QuotationModelDAO quotationModelDAO;

	static final AccountingEventMessageVO EXPECTED_PAYLOAD = new AccountingEventMessageVO()
			.setEntityId("1")
			.setEntity(AccountingNounEnum.CONTRACT)
			.setEvent(EventEnum.ACTUAL_END_DATE_UPDATE);

	String jsonPayload;

	@BeforeEach
	void init() throws Exception {
		jsonPayload = new ObjectMapper().writeValueAsString(EXPECTED_PAYLOAD);
	}			
			
	@Test
	@DisplayName("when request is to update the interest rate on a lease, then the neccessary calls are made to update the lease interest rate")
	void testProcess() throws Exception {	
		List<LeaseVO> leases = new ArrayList<>(0);
		leases.add(new LeaseVO().setExternalId("1"));
		leases.add(new LeaseVO().setExternalId("2"));
										
		Exchange ex = new ExchangeBuilder(context)
				.withBody(jsonPayload)
				.build();
		Quotation quotation = new Quotation();
		quotation.setQuoId(1L);

		QuotationModel qmd = new QuotationModel();
		qmd.setQmdId(1L);
		qmd.setQuotation(quotation);

		when(leaseService.getExternalLease(anyString(), anyBoolean())).thenReturn(leases);
		when(quotationModelDAO.findByQmdId(any())).thenReturn(Optional.of(qmd));

		leaseInterestUpdateProcessor.process(ex);

		verify(leaseService, times(1)).getExternalLease(eq("1"), eq(false));
		verify(leaseService, times(1)).updateInterestRate(eq(leases.get(0)));

		LeaseVO actualLease = (LeaseVO)ex.getMessage().getBody();

		assertNotNull(actualLease);
	}

	@Test
	@DisplayName("when lease cannot be found in external system, then a retry exception is thrown")
	void testProcessNoQMD() {
		assertThrows(RetryableException.class, () -> {
				List<LeaseVO> leases = new ArrayList<>(0);
		
				Exchange ex = new ExchangeBuilder(context)
						.withBody(jsonPayload)
						.build();
		
				when(leaseService.getExternalLease(anyString(), anyBoolean())).thenReturn(leases);
				
				leaseInterestUpdateProcessor.process(ex);		
		});
	}
}
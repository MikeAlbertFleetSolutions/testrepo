package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.BillingReportLeaseSuiteTalkService;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;
import com.mikealbert.util.data.DateUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a request")
public class BillingReportLeaseServiceTest extends BaseTest{
	@Resource BillingReportLeaseService billingReportLeaseService;

	@MockBean BillingReportLeaseSuiteTalkService billingReportLeaseSuiteTalkService;

	@BeforeEach
	void up() throws Exception {}
	
	@Test
	@DisplayName("when request is to find lease based on the unit's internal id and an effectiv date, then billing report lease matching the criteria is returned")
	public void testSearchByUnitInternalIdAndEffectiveDate() throws Exception {
		final String UNIT_INTERNAL_ID = "3749";
		final Date EFFECTIVE_DATE = DateUtil.convertToDate("2015-02-01", DateUtil.PATTERN_DATE);

		when(billingReportLeaseSuiteTalkService.searchByUnitInternalIdAndEffectiveDate(any(), any())).thenReturn(new BillingReportLeaseVO());

		BillingReportLeaseVO actualVO = billingReportLeaseService.searchByUnitInternalIdAndEffectiveDate(UNIT_INTERNAL_ID, EFFECTIVE_DATE);

		verify(billingReportLeaseSuiteTalkService, times(1)).searchByUnitInternalIdAndEffectiveDate(eq(UNIT_INTERNAL_ID), eq(EFFECTIVE_DATE));

		assertNotNull(actualVO);
	}			
}

package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;
import com.mikealbert.util.data.DateUtil;

@SpringBootTest
public class BillingReportLeaseSuiteTalkServiceTest {
	
	@Resource BillingReportLeaseSuiteTalkService billingReportLeaseSuiteTalkService;

	@Test
	public void testSearchByUnitInternalIdAndEffectiveDate() throws Exception {
		final String UNIT_INTERNAL_ID = "10746";  //TODO Lease LR13286 has a Willow End Date that is before the End Date. Should the billing report transaction include the lease when the transaction posted after the Willow End Date, but before the End date?
		final Date EFFECTIVE_DATE = DateUtil.convertToDate("2022-02-14", DateUtil.PATTERN_DATE);

		BillingReportLeaseVO actualVO = billingReportLeaseSuiteTalkService.searchByUnitInternalIdAndEffectiveDate(UNIT_INTERNAL_ID, EFFECTIVE_DATE);


		assertNotNull(actualVO.getInternalId());
		assertNotNull(actualVO.getExternalId());
		assertNotNull(actualVO.getName());
		assertNotNull(actualVO.getAltname());
		assertNotNull(actualVO.getLeaseType());
		assertNotNull(actualVO.getUnitExternalId());
		assertNotNull(actualVO.getUnitNo());
		assertNotNull(actualVO.getEndDate());

		assertTrue(actualVO.getCommencementDate().compareTo(EFFECTIVE_DATE) <= 0);
		//assertTrue(actualVO.getEndDate().compareTo(EFFECTIVE_DATE) >= 0);

		assertEquals(UNIT_INTERNAL_ID, actualVO.getUnitInternalId());
	}
	

}

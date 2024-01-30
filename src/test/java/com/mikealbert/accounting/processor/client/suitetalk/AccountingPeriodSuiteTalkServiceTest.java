package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;

@DisplayName("Given a request ")
@SpringBootTest
public class AccountingPeriodSuiteTalkServiceTest extends BaseTest{	

	@Resource AccountingPeriodSuiteTalkService accountingPeriodSuiteTalkService;
	
	@DisplayName("when requesting an accounting period, then the accounting period returned")
	@Test
	public void testGet() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "119";

		AccountingPeriodVO  actualAccountingPeriodVO = accountingPeriodSuiteTalkService.get(ACCOUNTING_PERIOD_ID);

		assertEquals(ACCOUNTING_PERIOD_ID, actualAccountingPeriodVO.getInternalId());
		assertNotNull(actualAccountingPeriodVO.getName());
		assertNotNull(actualAccountingPeriodVO.getStart());
		assertNotNull(actualAccountingPeriodVO.getEnd());
	}		
	

	@DisplayName("when requesting an accounting period by name, then the matching accounting period is returned")
	@Test
	public void testGetByName() throws Exception {
		final String ACCOUNTING_PERIOD_NAME = "Jan 2022";

		AccountingPeriodVO  actualAccountingPeriodVO = accountingPeriodSuiteTalkService.getByName(ACCOUNTING_PERIOD_NAME);

		assertEquals(ACCOUNTING_PERIOD_NAME, actualAccountingPeriodVO.getName());
		assertNotNull(actualAccountingPeriodVO.getInternalId());
		assertNotNull(actualAccountingPeriodVO.getStart());
		assertNotNull(actualAccountingPeriodVO.getEnd());
	}		


}

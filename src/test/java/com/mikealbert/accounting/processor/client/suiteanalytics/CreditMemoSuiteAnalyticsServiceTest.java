package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;


@SpringBootTest
@DisplayName("A reqeust")
public class CreditMemoSuiteAnalyticsServiceTest extends BaseTest{
	@Resource CreditMemoSuiteAnalyticsService creditMemoSuiteAnalyticsService;
	
	@Disabled //TODO Enable once credits with docid and lined exists in the accounting system
	@Test
	@DisplayName("when request is for all credit memo transactions that have been updated between a window of time, then all credit memo  transactions for the time period are returned")
	public void testFindUpdatedUngroupedCreditMemos() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -240 );

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> result = creditMemoSuiteAnalyticsService.findUpdatedUngroupedCreditMemos(start.getTime(), end.getTime());
				
		assertTrue(result.size() > 0, "Customer Credit Memo records do not exists");
	}	
	
	//@Disabled //TODO Enable once credits with docid and lined exists in the accounting system
	@Test
	@DisplayName("when request is for all credit memo transactions applied to an invoice group, then all the credit memo transactions applied to the grouped invoice is returned")
	public void testFindByGroupedInvoice() throws Exception {		
		final String CLIENT_INTERNAL_ID = "13183";
		final String ACCOUNTING_PERIOD_ID = "127";
		
		List<Map<String, Object>> result = creditMemoSuiteAnalyticsService.findByCustomerAndAccountingPeriod(CLIENT_INTERNAL_ID, ACCOUNTING_PERIOD_ID);
				
		assertTrue(result.size() > 0, "Customer Credit Memo records do not exists");
	}		
}

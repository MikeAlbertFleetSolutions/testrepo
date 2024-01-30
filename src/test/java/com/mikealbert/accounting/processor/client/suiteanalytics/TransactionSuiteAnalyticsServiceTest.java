package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.util.data.DateUtil;

@Disabled("Must have ungrouped and grouped transaction activity to run these tests")
@SpringBootTest
@DisplayName("A request")
public class TransactionSuiteAnalyticsServiceTest extends BaseTest{
	@Resource TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	
	//@Disabled //TODO Find a txn that has been grouped then use it to determin the date rage
	@Test
	@DisplayName("when request is for updated client transaction groups within a period of time, then a distinct list of clint transaction groups that were updated within the time period are returned")
	public void testFindUpdatedClientTransactionGroups() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -15);

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findUpdatedClientTransactionGroups(start.getTime(), end.getTime());
				
		assertTrue(result.size() > 0, "Invoice group records do not exists"); //TODO Add a invoice in NS	
	}

	@Test
	@DisplayName("when request is for updated ungrouped client transactions within a period of time, then a distinct list of client & accounting period(s) are returned")
	public void testFindUpdatedUngroupedClientBillingTransactions() throws Exception {
		Calendar base = DateUtil.convertToCalendar(DateUtil.convertToDate(super.clientBillingTransactionBaseDate, DateUtil.PATTERN_DATE));

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -15);

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findUpdatedUngroupedClientBillingTransactions(base.getTime(), start.getTime(), end.getTime());
				
		assertTrue(result.size() > 0, "Updated ungrouped Client & accounting period records do not exists"); 
	}

	@Test
	@DisplayName("when request is for updated client billng transactions group within a period of time, then a distinct list of client & account period(s) are returned")
	public void testfindUpdatedGroupedClientBillingTransactions() throws Exception {		
		Calendar base = DateUtil.convertToCalendar(DateUtil.convertToDate(super.clientBillingTransactionBaseDate, DateUtil.PATTERN_DATE));

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -15);

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findUpdatedGroupedClientBillingTransactions(base.getTime(), start.getTime(), end.getTime());
				
		assertTrue(result.size() > 0, "Updated grouped client & accounting period records do not exists"); 
	}
	
	@Test
	@DisplayName("when request is to check whether a client's billiing transactions have been grouped, then a boolean is returned")
	public void testIsGroupInvoiceDone() throws Exception {		
		final String ACCOUNTING_PERIOD_ID = "145";
		final String CLIENT_INTERNAL_ID = "13183";
		final String CLIENT_EXTERNAL_ID = "";

		boolean result = transactionSuiteAnalyticsService.isGroupInvoiceDone(ACCOUNTING_PERIOD_ID, CLIENT_INTERNAL_ID, CLIENT_EXTERNAL_ID);

		assertFalse(result);

	}
		
	@Disabled //TODO Enable when invoices are in prod
	@Test
	@DisplayName("when request is for all payment transactions for an invoice, then all payment transactions are returned")
	public void testFindPaymentsByInvoice() throws Exception {		
		final String INVOICE_INTERNAL_ID = "2169395";
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findPaymentsByInvoice(INVOICE_INTERNAL_ID, null);
				
		assertTrue(result.size() > 0, "Paymenttransaction records do not exists");
	}		

	@Disabled //TODO Enable when transaction data exists in prod and are in the SB
	@Test
	@DisplayName("when request is for all clients transaction groups for an accounting period, then all client transaction groups for the accounting period are returned")
	public void testFindAllClientTransactionGroupsByAccountingPeriod() throws Exception {		
		final String ACCOUNTING_PERIOD_ID = "123";
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findAllClientTransactionGroupsByAccountingPeriod(Arrays.asList(ACCOUNTING_PERIOD_ID));
				
		assertTrue(result.size() > 0, "Client transaction group records do not exists");
	}	

	//@Disabled //TODO Enable when transaction data exists in prod and are in the SB
	@Test
	@DisplayName("when request is for all groupable transactions by accounting period id and customer extid, then all matched groupable transactions are returned")
	public void testfindGroupableTransactionsByAccountingPeriodAndClientExternalId() throws Exception {		
		final String ACCOUNTING_PERIOD_ID = "126";
		final String CLIENT_EXTERNAL_ID = "1C00026612";
		
		List<Map<String, Object>> result = transactionSuiteAnalyticsService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(ACCOUNTING_PERIOD_ID,CLIENT_EXTERNAL_ID);
				
		assertTrue(result.size() > 0, "Groupable transaction records do not exists");
	}		
}

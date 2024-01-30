package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;


@SpringBootTest
@DisplayName("Given a request")
public class InvoiceSuiteAnalyticsServiceTest extends BaseTest{
	@Resource InvoiceSuiteAnalyticsService invoiceSuiteAnalyticsService;

	//@Disabled
	@Test
	@DisplayName("when request is for all grouped invoices that have a deposit application applied for a given client and accounting period, then all grouped invoices/deposit application releated data within the time period and for the client is returned")
	public void testFindGroupedInvoicesWithDepositApplicationForClientAcctPeriodGroup() throws Exception {		
		final String CLIENT_INTERNAL_ID = "16195";
		final String ACCOUNTING_PERIOD_ID = "126";
		final String GROUP_INVOICE_DOCUMENT_NO = "CI1846";
		
		List<Map<String, Object>> result = invoiceSuiteAnalyticsService.findGroupedInvoicesWithDepositApplication(CLIENT_INTERNAL_ID, ACCOUNTING_PERIOD_ID, GROUP_INVOICE_DOCUMENT_NO);
				
		assertTrue(result.size() > 0, "Invoice records do not exists"); //TODO Add a grouped invoice that has a deposit application in NS	
	}

	@Test
	@DisplayName("when request is for all grouped invoices for a given client and accounting period, group number, then all the matching grouped invoices are returned")
	public void testfindGroupedInvoices() throws Exception {		
		final String CLIENT_INTERNAL_ID = "16195";
		final String CLIENT_EXTERNAL_ID = null;		
		final String ACCOUNTING_PERIOD_ID = "126";
		final String GROUP_INVOICE_DOCUMENT_NO = "CI1846";
		
		List<Map<String, Object>> result = invoiceSuiteAnalyticsService.findGroupedInvoices(CLIENT_INTERNAL_ID, CLIENT_EXTERNAL_ID, ACCOUNTING_PERIOD_ID, GROUP_INVOICE_DOCUMENT_NO);
				
		assertTrue(result.size() > 0, "Invoice records do not exists"); //TODO Add a grouped invoice that has a deposit application in NS	
	}	
		
	@Test
	@DisplayName("Test to verify connection and query with NS")
	public void testGetDisposalInvoice() {
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, 1);		
		Date start = cal.getTime();
		
		cal.add(Calendar.DATE, 1);		
		Date end = cal.getTime();
		
		//NetSuite Connection Successful
		assertDoesNotThrow(() -> invoiceSuiteAnalyticsService.getDisposalInvoice(start, end));
	}

	//TODO Remove as this is just a dummy test until we get data for tests above
	@Test void testTrue() {assertTrue(true);}	

}

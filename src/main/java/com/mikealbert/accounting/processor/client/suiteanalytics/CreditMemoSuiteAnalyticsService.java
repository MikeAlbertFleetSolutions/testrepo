package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CreditMemoSuiteAnalyticsService {
	public List<Map<String, Object>> findUpdatedUngroupedCreditMemos(Date from, Date to) throws Exception;	 
	
	public List<Map<String, Object>> findByCustomerAndAccountingPeriod(String customerInternalId, String accountingPeriodInternalId) throws Exception;
}

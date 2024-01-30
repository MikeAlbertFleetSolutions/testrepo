package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CustomerSuiteAnalyticsService {
	public List<Map<String, Object>> getCustomers(Date from, Date to) throws Exception;
}

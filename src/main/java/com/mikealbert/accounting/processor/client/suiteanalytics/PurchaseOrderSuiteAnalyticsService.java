package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PurchaseOrderSuiteAnalyticsService {
	public List<Map<String, Object>> findClosed(Date from, Date to) throws Exception;
}

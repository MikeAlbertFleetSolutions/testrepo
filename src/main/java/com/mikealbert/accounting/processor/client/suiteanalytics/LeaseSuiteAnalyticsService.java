package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.List;
import java.util.Map;

public interface LeaseSuiteAnalyticsService {
	
	List<Map<String, Object>> getAllExternalLeases();
	List<Map<String, Object>> getExternalLeaseByExternalId(String externalId);

}

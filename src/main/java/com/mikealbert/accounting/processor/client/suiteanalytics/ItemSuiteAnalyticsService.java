package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Map;

public interface ItemSuiteAnalyticsService {
	public static String INTERNAL_ID = "type_id";
	
	Map<String, Object> get(String name);
}

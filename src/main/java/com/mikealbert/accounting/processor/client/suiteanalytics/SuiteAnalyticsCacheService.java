package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Map;

public interface SuiteAnalyticsCacheService  {

	Map<String, Object> getItem(String name) throws Exception;	
}

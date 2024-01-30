package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.List;
import java.util.Map;

public interface UnitSuiteAnalyticsService {
	
	List<Map<String, Object>> getAllExternalUnits();
	List<Map<String, Object>> getExternalUnitByExternalId(String externalId);

}

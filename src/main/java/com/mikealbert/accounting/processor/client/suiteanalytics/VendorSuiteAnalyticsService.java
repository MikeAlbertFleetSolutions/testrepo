package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VendorSuiteAnalyticsService {
	public List<Map<String, Object>> getVendors(Date from, Date to) throws Exception;	

	public List<Map<String, Object>> getAddresses() throws Exception;
	public List<Map<String, Object>> getAddresses(String entityId) throws Exception;
	public List<Map<String, Object>> getAddressesByExternalId(String externalId) throws Exception; 	
}

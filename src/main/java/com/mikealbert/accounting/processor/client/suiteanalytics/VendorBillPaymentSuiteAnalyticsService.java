package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VendorBillPaymentSuiteAnalyticsService {
	public List<Map<String, Object>> getVehiclePayments(Date from, Date to) throws Exception;	 	
}

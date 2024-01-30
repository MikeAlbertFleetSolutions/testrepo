package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VendorBillPaymentService {
	List<Map<String, Object>> getVehiclePayments(Date from, Date to) throws Exception; 
	
	void notify(Map<String, Object> notification) throws Exception;
}

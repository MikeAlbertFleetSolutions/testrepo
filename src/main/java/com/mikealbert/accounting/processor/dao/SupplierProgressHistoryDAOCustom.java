package com.mikealbert.accounting.processor.dao;

import java.util.Date;

public interface SupplierProgressHistoryDAOCustom {
	
	public void logVehicleVendorBillPayment(Long docId, Date paymentDate, String paymentMethod);
	
	
}

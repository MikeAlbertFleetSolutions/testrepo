package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InvoiceSuiteAnalyticsService {
	public List<Map<String, Object>> findGroupedInvoicesWithDepositApplication(String customerInternalId, String accountingPeriodInternalId, String groupNumber) throws Exception;

	public List<Map<String, Object>> findGroupedInvoices(String customerInternalId, String customerExternalId, String accountingPeriodInternalId, String groupNumber) throws Exception;	

	public List<Map<String, Object>> getDisposalInvoice(Date from, Date to) throws Exception;

}

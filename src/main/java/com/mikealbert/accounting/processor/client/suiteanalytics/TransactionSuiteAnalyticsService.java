package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TransactionSuiteAnalyticsService {

	public List<Map<String, Object>> findUpdatedClientTransactionGroups(Date from, Date to) throws Exception;	

	public List<Map<String, Object>> findUpdatedGroupedClientBillingTransactions(Date base, Date from, Date to) throws Exception;	

	public List<Map<String, Object>> findUpdatedUngroupedClientBillingTransactions(Date base, Date from, Date to) throws Exception;	

	public List<Map<String, Object>> findPaymentsByInvoice(String internalId, String externalId) throws Exception;	

	public List<Map<String, Object>> findGroupableTransactionsByAccountingPeriodAndClientExternalId(String accountingPeriodId, String clientExternalId) throws Exception;	
	
	public List<Map<String, Object>> findAllClientTransactionGroupsByAccountingPeriod(List<String> accountingPeriodIds) throws Exception;	

	public List<Map<String, Object>> findByPeriodAndMaType(String period, String maType) throws Exception;

	public boolean isGroupInvoiceDone(String accountingPeriodId, String clientInternalId, String clientExternalId) throws Exception;
}

package com.mikealbert.accounting.processor.dao;

public interface ClientBillingTransactionDAOCustom {
	public void mergeInternalData(String accountCode, String accountingPeriod, boolean force) throws Exception;
}

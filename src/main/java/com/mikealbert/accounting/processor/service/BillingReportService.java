package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;

public interface BillingReportService {		
	public List<BillingReportTransactionVO> get(String accountCode, List<AccountingPeriodVO> periods, BillingReportTypeEnum reportName) throws Exception;
	public void upsertInternalStore(List<BillingReportTransactionVO> billingReportTransactionVOs, boolean force) throws Exception;
	public void mergeInternalStore(String accountCode, List<AccountingPeriodVO> periods, boolean force) throws Exception;
	public void deleteFromInternalStore(String accountCode, List<AccountingPeriodVO> periods) throws Exception;
	public List<BillingReportRefreshMessageVO> findAndDispatchUpdates(Date base, Date from, Date to) throws Exception;
	public void validate(List<BillingReportTransactionVO> bilingReportTransactionVOs, boolean suppress);	
	public List<BillingReportTransactionVO> filterReportWorthy(String accountCode, List<BillingReportTransactionVO> billingReportTransactionVOs) throws Exception;
}

package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;

import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;

public interface BillingReportTransactionSuiteTalkService  {	
	public List<BillingReportTransactionVO> get(String accountCode, List<String> accountingPeriodInternalIds,  BillingReportTypeEnum maType) throws Exception;
}

package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

public interface ServiceCache {
	public DriverUnitHistoryVO findDuhByUnitInternalIdAndDate(String unitInternalId,  Date effectiveDate) throws Exception;
	public List<DriverUnitHistoryVO> finalAllDuhs() throws Exception; 
	public BillingReportLeaseVO findBillingReportLeaseByUnitInternalIdAndEffectiveDate(String unitInternalId,  Date effectiveDate) throws Exception;
	public List<AccountingPeriodVO> findAccountingPeriodByNameRange(String startPeriodName, String endPeriodName) throws Exception;	
}

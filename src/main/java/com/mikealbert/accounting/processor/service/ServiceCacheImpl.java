package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("serviceCache")
public class ServiceCacheImpl implements ServiceCache {
    @Resource DriverService driverService;
	@Resource BillingReportLeaseService billingReportLeaseService; 
    @Resource AccountingPeriodService accountingPeriodService;   

    @Cacheable(value = "readDuhByUnitInternalIdAndDate_cache")
    @Override
    public DriverUnitHistoryVO findDuhByUnitInternalIdAndDate(String unitInternalId, Date effectiveDate) throws Exception {
        return driverService.readDuhByUnitInternalIdAndDate(unitInternalId, effectiveDate);
    }

    @Cacheable(value = "searchByUnitInternalIdAndEffectiveDate_cache")
    @Override
    public BillingReportLeaseVO findBillingReportLeaseByUnitInternalIdAndEffectiveDate(String unitInternalId, Date effectiveDate) throws Exception {
        return billingReportLeaseService.searchByUnitInternalIdAndEffectiveDate(unitInternalId, effectiveDate);
    }

    @Cacheable(value = "findAccountingPeriodByRange_cache")
    @Override
    public List<AccountingPeriodVO> findAccountingPeriodByNameRange(String startPeriodName, String endPeriodName) throws Exception {
        return accountingPeriodService.getByNameRange(startPeriodName, endPeriodName);
    }

    @Cacheable(value = "finalAllDuhs_cache")
    @Override
    public List<DriverUnitHistoryVO> finalAllDuhs() throws Exception {
        return driverService.findAllDuhs();
    }

}

package com.mikealbert.accounting.processor.service;

import java.util.Date;

import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;

public interface BillingReportLeaseService {
    public BillingReportLeaseVO searchByUnitInternalIdAndEffectiveDate(String unitInternalId, Date effectiveDate) throws Exception;    
}

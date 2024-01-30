package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Date;

import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;

public interface BillingReportLeaseSuiteTalkService {
    public BillingReportLeaseVO searchByUnitInternalIdAndEffectiveDate(String unitInternalId, Date effectiveDate) throws Exception;
}

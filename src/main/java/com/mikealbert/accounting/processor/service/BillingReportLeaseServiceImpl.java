package com.mikealbert.accounting.processor.service;

import java.util.Date;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.client.suitetalk.BillingReportLeaseSuiteTalkService;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;

import org.springframework.stereotype.Service;

@Service("billingReportLeaseService")
public class BillingReportLeaseServiceImpl extends BaseService implements BillingReportLeaseService {
    @Resource BillingReportLeaseSuiteTalkService billingReportLeaseSuiteTalkService;

    @Override
    public BillingReportLeaseVO searchByUnitInternalIdAndEffectiveDate(String unitInternalId, Date effectiveDate) throws Exception {
        return billingReportLeaseSuiteTalkService.searchByUnitInternalIdAndEffectiveDate(unitInternalId, effectiveDate);
    }
    
}

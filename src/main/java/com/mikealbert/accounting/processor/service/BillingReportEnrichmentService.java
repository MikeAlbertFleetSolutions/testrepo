package com.mikealbert.accounting.processor.service;

import java.util.List;

import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;

public interface BillingReportEnrichmentService {
    @Deprecated(forRemoval = true)
    List<BillingReportTransactionVO> enrichWithDriverInfo(List<BillingReportTransactionVO> billingReportTransactionVOs) throws Exception;
}

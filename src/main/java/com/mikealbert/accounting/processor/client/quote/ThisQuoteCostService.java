package com.mikealbert.accounting.processor.client.quote;

import com.mikealbert.accounting.processor.vo.CapitalCostVO;

public interface ThisQuoteCostService {
    public CapitalCostVO thisQuoteCost(Long qmdId) throws Exception;
}

package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

public interface AgingTransactionService {
	public List<ReceivableTransactionVO<?, ?>> getAging(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) throws Exception;		
	
	public Map<AgingPeriodEnum, List<ReceivableTransactionVO<?, ?>>> groupByAgingPeriod(List<ReceivableTransactionVO<?, ?>> transactionVOs);						
}

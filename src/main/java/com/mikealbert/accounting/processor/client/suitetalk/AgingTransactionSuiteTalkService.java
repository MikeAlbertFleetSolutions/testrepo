package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;

import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

public interface AgingTransactionSuiteTalkService  {	
	public List<ReceivableTransactionVO<?, ?>> getAging(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) throws Exception;		
}

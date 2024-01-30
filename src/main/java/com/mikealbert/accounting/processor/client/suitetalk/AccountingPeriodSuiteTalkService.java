package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;

public interface AccountingPeriodSuiteTalkService  {
	public AccountingPeriodVO get(String internalId) throws Exception;
	public AccountingPeriodVO getByName(String periodName) throws Exception;	

}

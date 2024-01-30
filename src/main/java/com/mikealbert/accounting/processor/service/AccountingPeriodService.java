package com.mikealbert.accounting.processor.service;

import java.util.List;

import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;

public interface AccountingPeriodService {
	public AccountingPeriodVO get(String internalId) throws Exception;	
	public AccountingPeriodVO getByName(String name) throws Exception;

	public List<AccountingPeriodVO> getByNameRange(String startPeriodName, String endtPeriodName) throws Exception;
}

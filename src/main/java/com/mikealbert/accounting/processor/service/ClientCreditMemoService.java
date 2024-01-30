package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;

public interface ClientCreditMemoService {		
	public List<Map<String, Object>> findUpdatedUngroupedCreditMemos(Date from, Date to) throws Exception;

	public List<ClientCreditMemoVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception;

	public void process(List<ClientCreditMemoVO> clientCreditMemoVOs) throws Exception;

	public void process(ClientCreditMemoVO clientCreditMemoVO) throws Exception;
}

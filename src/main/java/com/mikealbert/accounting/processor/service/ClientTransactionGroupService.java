package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;

public interface ClientTransactionGroupService {		
	public List<ClientTransactionGroupVO> getUpdates(Date from, Date to) throws Exception;

	public boolean hasInternalGroupingBeenCompleted(Date from, Date to) throws Exception;
	
	public List<String> completedInternalGroupAccountingPeriods(Date from, Date to) throws Exception;
	
	public void process(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception;

	public void complete() throws Exception;	

	public List<String> formatMessageIds(List<ClientTransactionGroupVO> clientTransactionGroupVOs);

	public String formatMessageId(ClientTransactionGroupVO clientTransactionGroupVO);	

	public List<ClientTransactionGroupVO> convertToTransactionGroupVOs(List<Map<String, Object>> clientTransactionGroupMaps);

	public ClientTransactionGroupVO convertToTransactionGroupVO(Map<String, Object> clientTransactionGroupMaps);

	public List<Map<String, Object>> findGroupTransactions(String accountingPeriodId, String clientExternalId) throws Exception;

	public List<Map<String, Object>> findGroupTransactions(String accountingPeriodId, String clientExternalId, boolean isGrouped) throws Exception;
	
	public List<ClientTransactionGroupVO> findAllClientTransactiongGroupsByAccountingPeriod(List<String> accountingPeriodIds) throws Exception;

	public List<String> distinctAccountingPeriodId(List<ClientTransactionGroupVO> clientTransactionGroupVOs);

	public void emailComplete(List<String> accountingPeriods) throws Exception;
	
}

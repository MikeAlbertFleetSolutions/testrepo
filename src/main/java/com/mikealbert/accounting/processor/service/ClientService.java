package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.vo.ClientVO;

public interface ClientService {
		
	public ClientVO get(String externalId, boolean loadPurchaseBalance) throws Exception;
	
	public List<Map<String, Object>> getClients(Date from, Date to) throws Exception;

	public List<ClientVO> findWithBalance() throws Exception;

	public List<ClientVO> findActive() throws Exception;

	public List<Map<String, String>> getClientParents(Map<String, String> client) throws Exception;

	public ExternalAccount process(Map<String, String> client) throws Exception;

	public String parseAccountCodeFromExternalId(String externalId) throws Exception;

	public String formatExternalId(String accountCode) throws Exception;
			
}

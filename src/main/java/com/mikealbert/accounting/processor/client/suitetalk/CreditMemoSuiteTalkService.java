package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;

public interface CreditMemoSuiteTalkService  {
	
	public void create(ClientCreditMemoVO clientCreditMemoVO) throws Exception;

	public void delete(ClientCreditMemoVO clientCreditMemoVO) throws Exception;

	public ClientCreditMemoVO get(String internalId, String externalId) throws Exception;

}

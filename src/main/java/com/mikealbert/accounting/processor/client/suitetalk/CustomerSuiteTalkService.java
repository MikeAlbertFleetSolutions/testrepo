package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;

import com.mikealbert.accounting.processor.vo.ClientVO;

public interface CustomerSuiteTalkService  {
	static final String CUSTOMER_EXTERNAL_ID_PREFIX = "1C";
	
	public void create(ClientVO clientVO) throws Exception;

	public void delete(ClientVO clientVO) throws Exception;

	public void update(ClientVO clientVO) throws Exception;

	public ClientVO getCustomer(String internalId, String externalId, boolean includeParent) throws Exception;
	
	public List<ClientVO> findAllActive() throws Exception;

	public String formatExternalId(String accountCode) throws Exception;

	public ClientVO enrichWithNotes(ClientVO clientVO) throws Exception;

}

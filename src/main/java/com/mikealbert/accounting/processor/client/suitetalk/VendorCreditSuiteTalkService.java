package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Map;

import com.mikealbert.accounting.processor.vo.CreditVO;

public interface VendorCreditSuiteTalkService {
	public static final String APPROVAL_DEPARTMENT = "Accounting";	
	public static final String PAYABLE_ACCOUNT = "20010 Total Accounts Payable : Accounts Payable";
	
	Map<String, Object> get(String externalId) throws Exception;	
	
	public void create(CreditVO credit) throws Exception;
		
	public void delete(String externalId) throws Exception;
}

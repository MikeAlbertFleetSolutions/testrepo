package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.entity.ExternalAccount;

public interface VendorService {
	public static final String CHILD_ACCOUNT_CODE_PREFIX = "C";
	public static final String CHILD_ACCOUNT_CODE_SUFFIX = "";
	
	public List<Map<String, Object>> getVendors(Date from, Date to) throws Exception;
	
	public ExternalAccount upsertVendor(Map<String, String> vendor, List<Map<String, Object>> addresses) throws Exception;
	
	public List<Long> reconcileDeletedAddresses(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception;
	
	public List<ExternalAccount> closeChildAccounts(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception;
	
	public List<Map<String, Object>> getAddresses() throws Exception;
	
	public List<Map<String, Object>> getAddresses(Map<String, String> vendorMap) throws Exception;
		
	public List<Map<String, Object>> getAddresses(String externalId) throws Exception;	
	
	public String desencrypt(String data);
		
	public ExternalAccount resetDefaultAddress(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception;
		
}

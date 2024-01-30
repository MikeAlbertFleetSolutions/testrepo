package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;
import java.util.Map;

import com.netsuite.webservices.lists.relationships_2023_2.Customer;
import com.netsuite.webservices.platform.core_2023_2.types.GetCustomizationType;

public interface SuiteTalkCacheService  {
	public String searchDepartmentInternalIdByName(String name) throws Exception;
	
	public String searchClassificationRecordId(String name) throws Exception;
	
	public String searchForCustomSegmentId(String scriptId, String name) throws Exception;
	
	public String searchForCustomRecordId(String scriptId, String name) throws Exception; 

	public String searchForCustomListItemId(String scriptId, String name) throws Exception;

	public String searchForCustomListItemValueByInternalId(String scriptId, String internalId) throws Exception;	

	public String searchForCustomXId(String scriptId, GetCustomizationType type) throws Exception;

	public String searchCustomerCategoryInternalIdByName(String name) throws Exception;	

	public String searchTermInternalIdByName(String name) throws Exception;		
	
	public String getCustomRecordTypeInternalId(String scriptId) throws Exception;	

	public List<Map<String, Object>> getStates() throws Exception;

	public List<Customer> getActiveClients() throws Exception;
}

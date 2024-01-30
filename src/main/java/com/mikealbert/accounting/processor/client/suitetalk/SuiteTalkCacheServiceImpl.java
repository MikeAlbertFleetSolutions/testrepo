package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netsuite.webservices.lists.relationships_2023_2.Customer;
import com.netsuite.webservices.platform.core_2023_2.types.GetCustomizationType;

@Service("suiteTalkCacheService")
public class SuiteTalkCacheServiceImpl extends BaseSuiteTalkService implements SuiteTalkCacheService{
	
	@Cacheable(value = "searchDepartmentInternalIdByName_cache")
	@Override
	public String searchDepartmentInternalIdByName(String name) throws Exception{
		return super.service.searchDepartmentInternalIdByName(name);
	}

	@Cacheable(value = "searchClassificationRecordId_cache")
	@Override
	public String searchClassificationRecordId(String name) throws Exception {
		return super.service.searchClassificationRecordId(name);
	}

	@Cacheable(value = "searchForCustomSegmentId_cache")
	@Override
	public String searchForCustomSegmentId(String scriptId, String name) throws Exception {
		return super.service.searchForCustomSegmentId(scriptId, name);
	} 
	
	@Cacheable(value = "searchForCustomRecordId_cache")
	@Override
	public String searchForCustomRecordId(String scriptId, String name) throws Exception {
		return super.service.searchForCustomRecordId(scriptId, name); 
	} 

	@Cacheable(value = "searchForCustomXId_cache")
	@Override
	public String searchForCustomXId(String scriptId, GetCustomizationType type) throws Exception {
		return super.service.getCustomXId(scriptId, type);
	}
	
	@Cacheable(value = "searchForCustomListItemId_cache")
	@Override
	public String searchForCustomListItemId(String scriptId, String name) throws Exception {
		return super.service.searchForCustomListItemId(scriptId, name);
	}	

	@Cacheable(value = "searchForCustomListItemId_cache")
	@Override
	public String searchForCustomListItemValueByInternalId(String scriptId, String internalId) throws Exception {
		return super.service.searchForCustomListItemValueByInternalId(scriptId, internalId);
	}	

	@Cacheable(value = "searchCustomerCategoryInternalIdByName_cache")
	@Override
	public String searchCustomerCategoryInternalIdByName(String name) throws Exception {
		String internalId = null;

		Map<String, String> criteria = new HashMap<>();
		criteria.put("name", name);

		Map<String, String> result = super.service.searchCustomerCategory(criteria);
		if(!result.isEmpty()) {
			internalId = result.get("internalId");
		}

		return internalId;
	}

	@Cacheable(value = "searchTermInternalIdByName_cache")
	@Override
	public String searchTermInternalIdByName(String name) throws Exception {
		String internalId = null;

		Map<String, String> criteria = new HashMap<>();
		criteria.put("name", name);

		Map<String, String> result = super.service.searchTerm(criteria);
		if(!result.isEmpty()) {
			internalId = result.get("internalId");
		}

		return internalId;
	}

	@Cacheable(value = "getCustomRecordTypeInternalId_cache")
	@Override
	public String getCustomRecordTypeInternalId(String scriptId) throws Exception {
		return super.service.getCustomRecordTypeInternalId(scriptId);
	}
	
	@Cacheable(value = "getStates_cache")
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getStates() throws Exception {
		return new ObjectMapper().convertValue(service.getStates(), List.class);
	}

	@Cacheable(value = "getActiveClients_cache")
	@Override
	public List<Customer> getActiveClients() throws Exception {
		return service.findAllActiveCustomers(true);
	}

}

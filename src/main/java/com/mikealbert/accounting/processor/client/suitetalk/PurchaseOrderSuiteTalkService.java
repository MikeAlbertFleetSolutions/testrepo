package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

public interface PurchaseOrderSuiteTalkService {

	public Map<String, Object> getByExternalId(String externalId) throws Exception;
	
	public Map<String, Object> searchByExternalId(String externalId) throws Exception;	

	public List<Map<String, Object>> searchByPoNumberAndVendor(String poNumber, String vendorExternalId) throws Exception;
	
	public void add(PurchaseOrderVO po) throws Exception;
	
	public void delete(String externalId) throws Exception;
	
	public void update(PurchaseOrderVO po) throws Exception;
	
	public void updateEntity(String purchaseOrderExternalId, String entityExternalId) throws Exception;	
		
	public void close(Long docId) throws Exception;
}

package com.mikealbert.accounting.processor.client.suitetalk;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.mikealbert.accounting.processor.client.suiteanalytics.ItemSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.dao.ExtAccAddressDAO;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkImmutableRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkNoRecordFoundException;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.util.data.DateUtil;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.transactions.purchases_2023_2.PurchaseOrder;
import com.netsuite.webservices.transactions.purchases_2023_2.PurchaseOrderItem;
import com.netsuite.webservices.transactions.purchases_2023_2.PurchaseOrderItemList;

@Service("poSuiteTalkService")
public class PurchaseOrderSuiteTalkServiceImpl extends BaseSuiteTalkService implements PurchaseOrderSuiteTalkService{
	@Resource ExtAccAddressDAO extAccAddressDAO;
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Override
	public void add(PurchaseOrderVO poVO) throws Exception {
		PurchaseOrder po = convertToPurchaseOrder(poVO);
		WriteResponse response = service.getService().add(po);	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode().equals(StatusDetailCodeType.DUP_RCRD)) {
				throw new SuiteTalkDuplicateRecordException(String.format("Error creating purchase order. ExternalId: %s", po.getExternalId()), response);				
			} else {
				throw new SuiteTalkException(String.format("Error creating purchase order. ExternalId: %s", po.getExternalId()), response);				
			}
		}
		
		LOG.info("Successfully created the PO in the external accounting system. PO -> {}", poVO);
	}
	
	/**
	 * Gets the Purchase Order based on the external Id, sublist(s) included.
	 * 
	 * NOTE: This method is not as performant as {@link #searchByExternalId(String) searchByExternalId} do to inclusion of sublist(s)
	 */
	@Override
	public Map<String, Object> getByExternalId(String externalId) throws Exception {		
		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.purchaseOrder);		
		ReadResponse response = service.getService().get(recordRef);
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error reading po: %s", externalId), response);
		} 

		Map<String, Object> poMap = convertToMap((PurchaseOrder)response.getRecord());
		
		return poMap;
	}
	
	/**
	 * Search for the Purchase Order based on the external Id.
	 * 
	 * NOTE: The returned PO will not have the sublist. If you need sublist(s) refer to {@link #getByExternalId(String) getByExternalId}
	 */
	@Override
	public Map<String, Object> searchByExternalId(String externalId) throws Exception {
		Map<String, Object> poMap = null;
				
		PurchaseOrder po = service.searchPurchaseOrderByExternalId(externalId);		
		if(po != null) {
			poMap = convertToMap(po);
		}
		
		return poMap;
	}
	
	/**
	 * Search for a list for Purchase Orders with the same PO No.
	 * 
	 * NOTE: Sublist(s) are not included in the search result
	 */
	@Override
	public List<Map<String, Object>> searchByPoNumberAndVendor(String poNumber, String vendorExternalId) throws Exception {
		List<Map<String, Object>> poMaps = new ArrayList<>();
		
		List<PurchaseOrder> pos = service.searchPurchaseOrdersByPoNumber(poNumber);
	  
		for(PurchaseOrder po : pos) {				
			ObjectMapper mapper = new ObjectMapper();
			mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);

			poMaps.add(mapper.convertValue(po, new TypeReference<Map<String, Object>>(){}));
		} 
						
		return poMaps;
	}	
	
	@Override
	public void delete(String externalId) throws Exception {
		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.purchaseOrder);
		WriteResponse response = service.getService().delete(recordRef, null);
		
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.SSS_MISSING_REQD_ARGUMENT)) {
				LOG.warn("Delete PO handled error from accounting system: ExternalId {} - {} ", externalId, response.getStatus().getStatusDetail()[0].getMessage());
			} else {
				throw new SuiteTalkException("Error deleting purhcase order", response);				
			}
		}			
	}
	
	@Override
	public void update(PurchaseOrderVO internalPO) throws Exception {
		final String FULLY_BILLED_MESSAGE = "Items on this line have been received. To delete this line, you must first delete the corresponding line(s) in the associated transaction(s).";

		WriteResponse response = service.getService().update(convertToPurchaseOrder(internalPO));
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.USER_ERROR) 
			        && response.getStatus().getStatusDetail()[0].getMessage().equals(FULLY_BILLED_MESSAGE) ) {
				throw new SuiteTalkImmutableRecordException(String.format("Error updating purchase order. ExternalId: %s", internalPO.getExternalId()), response);										
			}
			throw new SuiteTalkException(String.format("Error updating purchase order extId %s item", internalPO.getExternalId()), response);
		}					
	}	
	
	@Override
	public void updateEntity(String purchaseOrderExternalId, String entityExternalId) throws Exception {

		PurchaseOrder po = new PurchaseOrder();
		po.setExternalId(purchaseOrderExternalId);
		po.setEntity(new RecordRef(null, null, entityExternalId, RecordType.vendor));
		
		WriteResponse response = service.getService().update(po);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error updating the entity field %s on purchase order extId %s ", entityExternalId, purchaseOrderExternalId), response);
		}				
	}
		
	@Override
	public void close(Long docId) throws Exception {
		RecordRef recordRef = new RecordRef(null, null, docId.toString(), RecordType.purchaseOrder);
		ReadResponse response = service.getService().get(recordRef);
		if(!response.getStatus().isIsSuccess()) {	
			if(StatusDetailCodeType.INVALID_KEY_OR_REF.equals(response.getStatus().getStatusDetail()[0].getCode())) {
				throw new SuiteTalkNoRecordFoundException(String.format("Error getting purchase order. ExternalId: %d", docId), response);				
			} else {
				throw new SuiteTalkException(String.format("Error closing purchase order. ExternalId: %d", docId), response);
			}
		}
		
		PurchaseOrder originalPO = (PurchaseOrder)response.getRecord();
		PurchaseOrderItemList poItems = originalPO.getItemList();
		
		Arrays.asList(poItems.getItem()).stream()
		        .forEach(poItem -> poItem.setIsClosed(true));
		
		PurchaseOrder updatedPO = new PurchaseOrder();
		updatedPO.setInternalId(originalPO.getInternalId());
		updatedPO.setItemList(poItems);		
		
		WriteResponse wResponse = service.getService().update(updatedPO);
		if(!wResponse.getStatus().isIsSuccess()) {
			LOG.error("Failed to closinging purchase order externalId {} ", docId);			
			throw new SuiteTalkException(String.format("Error closing purchase order. ExternalId: %d", docId), response);
		}
		
		LOG.info("Successfully closed the PO in the accounting system. PO -> {}", docId);		
		
	}		
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> convertToMap(PurchaseOrder purchaseOrder) {
		Map<String, Object> poMap = new HashMap<>();
		List<Map<String, Object>> items = new ArrayList<>(0);

		poMap.put(PurchaseOrderFieldEnum.INTERNAL_ID.getScriptId(), purchaseOrder.getInternalId());
		poMap.put(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId(), purchaseOrder.getExternalId());
		poMap.put(PurchaseOrderFieldEnum.TRAN_ID.getScriptId(), purchaseOrder.getTranId());
		poMap.put(PurchaseOrderFieldEnum.ENTITY.getScriptId(), purchaseOrder.getEntity().getName());
		poMap.put(PurchaseOrderFieldEnum.TRAN_DATE.getScriptId(), purchaseOrder.getTranDate());
		poMap.put(PurchaseOrderFieldEnum.BILL_ADDRESS_LIST.getScriptId(), purchaseOrder.getBillAddressList() == null ? null : purchaseOrder.getBillAddressList().getInternalId());
		poMap.put(PurchaseOrderFieldEnum.STATUS.getScriptId(), purchaseOrder.getStatus());		

		Map<Object, CustomFieldRef> customListMap = Arrays.asList(purchaseOrder.getCustomFieldList().getCustomField()).stream()
				.collect(Collectors.toMap(e -> e.getScriptId(), e -> e));
		poMap.put(PurchaseOrderFieldEnum.APPROVAL_DEPARTMENT.getScriptId(), ((SelectCustomFieldRef)customListMap.get(PurchaseOrderFieldEnum.APPROVAL_DEPARTMENT.getScriptId())).getValue().getName());
		poMap.put(PurchaseOrderFieldEnum.AUTO_APPROVE.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(PurchaseOrderFieldEnum.AUTO_APPROVE.getScriptId())).isValue());
		poMap.put(PurchaseOrderFieldEnum.MAIN_PO.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(PurchaseOrderFieldEnum.MAIN_PO.getScriptId())).isValue());
		poMap.put(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId(), ((StringCustomFieldRef)customListMap.get(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId())).getValue());			

		if(purchaseOrder.getItemList() != null) {			
			//@SuppressWarnings("unchecked")			
			items = new ObjectMapper().convertValue(Arrays.asList(purchaseOrder.getItemList().getItem()), List.class);
		}
		
		poMap.put(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId(), items);

		return poMap;
	}

	private PurchaseOrder convertToPurchaseOrder(PurchaseOrderVO poVO) throws Exception {
		String itemInternalId = null;
		String unitInternalId = null;
		String departmentInternalId = null;
		String classInternalId = null;
		PurchaseOrder po;		
		List<Object> customFields, itemCustomFields;
		
		PurchaseOrderLineVO poLineVO = poVO.getLines().get(0);
		
		po = new PurchaseOrder();
		po.setExternalId(String.valueOf(poVO.getExternalId())); 
		po.setEntity(new RecordRef(null, null, poVO.getVendor(), RecordType.vendor));
		po.setTranDate(DateUtil.convertToCalendar(poVO.getTranDate())); //TODO For non-untrec this will be the doc date
		po.setTranId(poVO.getTranId());
		po.setMemo(poVO.getMemo());
		po.setBillAddressList(new RecordRef(null, poVO.getVendorAddressInternalId().toString(), null, null));
			
		
		try {
			itemInternalId = (String)suiteAnalyticsCacheService.getItem(poLineVO.getItem()).get(ItemSuiteAnalyticsService.INTERNAL_ID); //TODO This a temporary fix. Item should be retrieved from API.
			if(itemInternalId == null) {
				throw new Exception("Item does not exist");
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for item %s in the external system", poVO.getExternalId(), poLineVO.getItem()), e);			
		}

		try {
			classInternalId = suiteTalkCacheService.searchClassificationRecordId(poLineVO.getBusinessUnit().getName());
		} catch(Exception e) {
			classInternalId = null;
		}
		
		try {
			departmentInternalId = suiteTalkCacheService.searchDepartmentInternalIdByName(poLineVO.getDepartment());
		} catch(Exception e) {
			departmentInternalId = null;
		}
		
		try {
			unitInternalId = suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), poLineVO.getUnit());
		} catch(Exception e) {
			throw new RetryableSuiteTalkException(String.format("Error occurred in invoice %s while searching for unit no %s in the external system", poVO.getExternalId(), poLineVO.getUnit()), e);				
		}
						
		customFields = new ArrayList<>(0);		
		SelectCustomFieldRef approvalDepartment = new SelectCustomFieldRef();
		approvalDepartment.setScriptId(PurchaseOrderFieldEnum.APPROVAL_DEPARTMENT.getScriptId());		
		approvalDepartment.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchDepartmentInternalIdByName(poVO.getApprovalDepartment()), null, null));		//TODO Need to lookup internal id
		customFields.add(approvalDepartment);
		
		BooleanCustomFieldRef automaticApproval = new BooleanCustomFieldRef();
		automaticApproval.setScriptId(PurchaseOrderFieldEnum.AUTO_APPROVE.getScriptId());
		automaticApproval.setValue(poVO.isAutoApprove());
		customFields.add(automaticApproval);
											
		BooleanCustomFieldRef mainPO = new BooleanCustomFieldRef();
		mainPO.setScriptId(PurchaseOrderFieldEnum.MAIN_PO.getScriptId());
		mainPO.setValue(poVO.isMain());
		customFields.add(mainPO);
		
		StringCustomFieldRef updateControlCode = new StringCustomFieldRef();
		updateControlCode.setScriptId(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId());
		updateControlCode.setValue(poVO.getControlCode().name());
		customFields.add(updateControlCode);		
		
		CustomFieldList poFieldList = new CustomFieldList(customFields.toArray(CustomFieldRef[]::new));		
		po.setCustomFieldList(poFieldList);
		
		itemCustomFields = new ArrayList<>(0);		
		PurchaseOrderItemList poItemList = new PurchaseOrderItemList();		

		PurchaseOrderItem item = new PurchaseOrderItem();
		item.setItem(new RecordRef(null, itemInternalId, null, RecordType.inventoryItem));
		item.setQuantity(poLineVO.getQuantity().doubleValue());
		item.setRate(poLineVO.getRate().toString());
		item.setDescription(poLineVO.getDescription());
		item.set_class(new RecordRef(null, classInternalId, null, RecordType.classification));
		item.setDepartment(new RecordRef(null, departmentInternalId, null, RecordType.department));		

		SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
		unitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
		unitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
		itemCustomFields.add(unitNo);	

		item.setCustomFieldList(new CustomFieldList(itemCustomFields.toArray(CustomFieldRef[]::new)));

		poItemList.setReplaceAll(true);
		poItemList.setItem(new PurchaseOrderItem[] {item});
		
		po.setItemList(poItemList);		
		
		return po;
	}

}

package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suiteanalytics.ItemSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorBillFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.CustomFormEnum;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.InitializeRecord;
import com.netsuite.webservices.platform.core_2023_2.InitializeRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeRefType;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeType;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorBill;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorBillItem;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorBillItemList;

@Service("vendorBillSuiteTalkService")
public class VendorBillSuiteTalkServiceImpl extends BaseSuiteTalkService implements VendorBillSuiteTalkService {
	@Resource DocDAO docDAO;
	@Resource PurchaseOrderSuiteTalkService purchaseOrderSuiteTalkService;
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void create(InvoiceVO invoice) throws Exception {		
		VendorBill vendorBill = convertInvoiceToVendorBillForVendor(invoice);
		
		LOG.info("Start NS add call for vendor bill {}", invoice.getExternalId());
		WriteResponse response = service.getService().add(vendorBill);
		LOG.info("End NS add call for vendor bill {}", invoice.getExternalId());
		LOG.info("End NS add call for vendor bill {}. response status is {}", invoice.getExternalId(), response.getStatus().isIsSuccess());		
		if(!response.getStatus().isIsSuccess()) {
			LOG.info("Failed to create vendor bill for invoice {} ", invoice);
			throw new SuiteTalkException(String.format("Error creating Vendor Bill for invoice extId %s ", invoice.getExternalId()), response);
		}	

		LOG.info("Successfully created Vendor bill for invoice extId {}", invoice.getExternalId());
	}

	@Override
	public void create(InvoiceVO invoice, PurchaseOrderVO purchaseOrder) throws Exception {
		try {
			try {
				purchaseOrderSuiteTalkService.updateEntity(purchaseOrder.getExternalId().toString(), invoice.getVendor());
			} catch(SuiteTalkException e) {
				throw new SuiteTalkException(String.format("Error creating Vendor Bill. Received error when attemting to update entity on PO extId %s for invoice extId %s ", purchaseOrder.getExternalId(), invoice.getExternalId()), e);
			}

			VendorBill vendorBill = convertInvoiceToVendorBillFromPurchaseOrder(invoice, purchaseOrder);
			WriteResponse response = service.getService().add(vendorBill);
			if(!response.getStatus().isIsSuccess()) {
				throw new SuiteTalkException(String.format("Error creating Vendor Bill from purchase order extId %s for invoice extId %s ", purchaseOrder.getExternalId(), invoice.getExternalId()), response);
			}	    	
		} catch(Exception e) {
			throw new RetryableSuiteTalkException(e);
		}

		LOG.info("Successfully created Vendor bill from purchase order extId {} for invoice extId {}", purchaseOrder.getExternalId(), invoice.getExternalId());    	        
	}

	@Override
	public void delete(String externalId) throws Exception {
		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.vendorBill);
		WriteResponse response = service.getService().delete(recordRef, null);

		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.SSS_MISSING_REQD_ARGUMENT)) {
				LOG.warn("Delete Vendor Bill handled error from accounting system: ExternalId {} - {} ", externalId, response.getStatus().getStatusDetail()[0].getMessage());			
			} else {
				throw new SuiteTalkException("Error deleting vendorBill", response);
			}
		}	
	}

	@Override
	public Map<String, Object> get(String externalId) throws Exception {
		Map<String, Object> vendorBillMap = new HashMap<>();

		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.vendorBill);		
		ReadResponse response = service.getService().get(recordRef);

		if(response.getStatus().isIsSuccess()) {
			VendorBill vendorBill = (VendorBill)response.getRecord();
			vendorBillMap.put(VendorBillFieldEnum.INTERNAL_ID.getScriptId(), vendorBill.getInternalId());
			vendorBillMap.put(VendorBillFieldEnum.EXTERNAL_ID.getScriptId(), vendorBill.getExternalId());
			vendorBillMap.put(VendorBillFieldEnum.TRAN_ID.getScriptId(), vendorBill.getTranId());
			vendorBillMap.put(VendorBillFieldEnum.ENTITY.getScriptId(), vendorBill.getEntity().getName());
			vendorBillMap.put(VendorBillFieldEnum.SUBSIDIARY.getScriptId(), vendorBill.getSubsidiary().getInternalId());
			vendorBillMap.put(VendorBillFieldEnum.ACCOUNT.getScriptId(), vendorBill.getAccount().getName());
			vendorBillMap.put(VendorBillFieldEnum.TRAN_DATE.getScriptId(), vendorBill.getTranDate());
			vendorBillMap.put(VendorBillFieldEnum.MEMO.getScriptId(), vendorBill.getMemo());
			vendorBillMap.put(VendorBillFieldEnum.USER_TOTAL.getScriptId(), new BigDecimal(vendorBill.getUserTotal()) );
			vendorBillMap.put(VendorBillFieldEnum.BILL_ADDRESS_LIST.getScriptId(), vendorBill.getBillAddressList() == null ? null : vendorBill.getBillAddressList().getInternalId());

			Map<Object, CustomFieldRef> customListMap = Arrays.asList(vendorBill.getCustomFieldList().getCustomField()).stream()
					.collect(Collectors.toMap(e -> e.getScriptId(), e -> e));
			vendorBillMap.put(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId(), ((SelectCustomFieldRef)customListMap.get(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId())).getValue().getName());
			vendorBillMap.put(VendorBillFieldEnum.AUTO_APPROVE.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(VendorBillFieldEnum.AUTO_APPROVE.getScriptId())).isValue());
			vendorBillMap.put(VendorBillFieldEnum.MAIN_PO.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(VendorBillFieldEnum.MAIN_PO.getScriptId())).isValue());
			vendorBillMap.put(CustomSegmentEnum.UNIT_NO.getScriptId(), ((SelectCustomFieldRef)customListMap.get(CustomSegmentEnum.UNIT_NO.getScriptId())).getValue().getName());			

			try {
				vendorBillMap.put(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId(), ((StringCustomFieldRef)customListMap.get(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId())).getValue());
			} catch(NullPointerException npe) {
				vendorBillMap.put(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId(), null);				
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = new ObjectMapper().convertValue(Arrays.asList(vendorBill.getItemList().getItem()), List.class);
			vendorBillMap.put(VendorBillFieldEnum.ITEM_LIST.getScriptId(), items);						
		} else {
			throw new SuiteTalkException(String.format("Error reading vendor bill: externalId = %s", externalId), response);
		}	

		return vendorBillMap;
	}

	private VendorBill convertInvoiceToVendorBillForVendor(InvoiceVO invoice) throws Exception {
		List<Object> customFields, itemCustomFields;

		String unitInternalId = null;						
		try {
			unitInternalId = suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), invoice.getLines().get(0).getUnit());
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for unit no %s in the external system", invoice.getExternalId(), invoice.getLines().get(0).getUnit()), e);				
		}		

		InitializeRef vendorOrPurchaseOrderRef = new InitializeRef();
		vendorOrPurchaseOrderRef.setType(InitializeRefType.vendor);
		vendorOrPurchaseOrderRef.setExternalId(invoice.getVendor());			

		InitializeRecord vendorBill = new InitializeRecord();
		vendorBill.setType(InitializeType.vendorBill);
		vendorBill.setReference(vendorOrPurchaseOrderRef);

		ReadResponse vendorBillResponse = service.getService().initialize(vendorBill);
		if(!vendorBillResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error initializing Vendor Bill. %s ", invoice.toString()), vendorBillResponse);
		}

		customFields = new ArrayList<>(0);
		SelectCustomFieldRef approvalDepartment = new SelectCustomFieldRef();
		approvalDepartment.setScriptId(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId());		
		approvalDepartment.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchDepartmentInternalIdByName(invoice.getApprovalDepartment()), null, null));		//TODO Need to lookup internal id
		customFields.add(approvalDepartment);

		BooleanCustomFieldRef automaticApproval = new BooleanCustomFieldRef();
		automaticApproval.setScriptId(VendorBillFieldEnum.AUTO_APPROVE.getScriptId());
		automaticApproval.setValue(invoice.isAutoApprove());
		customFields.add(automaticApproval);

		SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
		unitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
		unitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
		customFields.add(unitNo);		

		VendorBill vendorBillRecord = (VendorBill)vendorBillResponse.getRecord();
		vendorBillRecord.setExternalId(invoice.getExternalId().toString());
		vendorBillRecord.setTranId(invoice.getTranId());
		vendorBillRecord.setTranDate(DateUtil.convertToCalendar(invoice.getTranDate()));
		vendorBillRecord.setAccount(new RecordRef(invoice.getPayableAccount(), null, null, RecordType.account));
		vendorBillRecord.setMemo(invoice.getMemo());
		vendorBillRecord.setBillAddressList(new RecordRef(null, invoice.getVendorAddressInternalId().toString(), null, null));
		vendorBillRecord.setSubsidiary(new RecordRef(null, invoice.getSubsidiary().toString(), null, RecordType.subsidiary));
		vendorBillRecord.setCustomFieldList(new CustomFieldList(customFields.toArray(CustomFieldRef[]::new)));
		vendorBillRecord.setCustomForm(new RecordRef(null, CustomFormEnum.MA_VENDOR_BILL.getInternalId(), null, null));


		List<VendorBillItem> vendorBillItems = new ArrayList<>(0);
		for(InvoiceLineVO line : invoice.getLines()) {
			itemCustomFields = new ArrayList<>(0);

			VendorBillItem item = new VendorBillItem();						
			item.setItem(new RecordRef(null, lookupItemInternalId(line), null, RecordType.inventoryItem));
			item.setRate(line.getRate().toString());
			item.setDescription(line.getDescription());			
			item.set_class(new RecordRef(null, lookupClassfication(line), null, RecordType.classification));
			item.setDepartment(new RecordRef(null, lookupDepartment(line), null, RecordType.department));
			item.setQuantity(line.getQuantity() == null ? null : line.getQuantity().doubleValue());				

			SelectCustomFieldRef itemUnitNo = new SelectCustomFieldRef();
			itemUnitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
			itemUnitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
			itemCustomFields.add(itemUnitNo);	

			item.setCustomFieldList(new CustomFieldList(itemCustomFields.toArray(CustomFieldRef[]::new)));

			vendorBillItems.add(item);
		}		
		vendorBillRecord.setItemList(new VendorBillItemList(vendorBillItems.toArray(VendorBillItem[]::new), true));

		return vendorBillRecord;
	}

	private VendorBill convertInvoiceToVendorBillFromPurchaseOrder(InvoiceVO invoice, PurchaseOrderVO purchaseOrder) throws Exception {
		List<CustomFieldRef> customFields, itemCustomFields;		
		String unitInternalId = null;
		String assetTypeInternalId = null;

		try {
			unitInternalId = suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), invoice.rolledUpLineUnitNo());
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for unit no %s in the external system", invoice.getExternalId(), invoice.getLines().get(0).getUnit()), e);				
		}	

		try {
			if(invoice.externalAssetType() != null && !invoice.externalAssetType().trim().isEmpty()) { 
				assetTypeInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_TYPE.getScriptId(), invoice.externalAssetType());
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for asset type %s in the external system", invoice.getExternalId(), invoice.externalAssetType()), e);				
		}		

		InitializeRef vendorOrPurchaseOrderRef = new InitializeRef();
		vendorOrPurchaseOrderRef.setType(InitializeRefType.purchaseOrder);			
		vendorOrPurchaseOrderRef.setExternalId(purchaseOrder.getExternalId().toString());			

		InitializeRecord vendorBill = new InitializeRecord();
		vendorBill.setType(InitializeType.vendorBill);
		vendorBill.setReference(vendorOrPurchaseOrderRef);

		ReadResponse vendorBillResponse = service.getService().initialize(vendorBill);
		if(!vendorBillResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error initializing Vendor Bill. %s ", invoice.toString()), vendorBillResponse);
		}

		customFields = new ArrayList<>(0);				
		SelectCustomFieldRef approvalDepartment = new SelectCustomFieldRef();
		approvalDepartment.setScriptId(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId());		
		approvalDepartment.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchDepartmentInternalIdByName(invoice.getApprovalDepartment()), null, null));		//TODO Need to lookup internal id
		customFields.add(approvalDepartment);

		BooleanCustomFieldRef automaticApproval = new BooleanCustomFieldRef();
		automaticApproval.setScriptId(VendorBillFieldEnum.AUTO_APPROVE.getScriptId());
		automaticApproval.setValue(invoice.isAutoApprove());
		customFields.add(automaticApproval);

		SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
		unitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
		unitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
		customFields.add(unitNo);

		BooleanCustomFieldRef mainPO = new BooleanCustomFieldRef();
		mainPO.setScriptId(PurchaseOrderFieldEnum.MAIN_PO.getScriptId());
		mainPO.setValue(purchaseOrder.isMain());
		customFields.add(mainPO);

		StringCustomFieldRef updateControlCode = new StringCustomFieldRef();
		updateControlCode.setScriptId(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId());
		updateControlCode.setValue(purchaseOrder.getControlCode().name());
		customFields.add(updateControlCode);		

		//TODO Extract look up to a method that accepts script id and name then returns List ref
		itemCustomFields = new ArrayList<>(0);
		if(assetTypeInternalId != null) {
			SelectCustomFieldRef assetType = new SelectCustomFieldRef();
			assetType.setScriptId(VendorBillFieldEnum.ITEM_ASSET_TYPE.getScriptId());		
			assetType.setValue(new ListOrRecordRef(null, assetTypeInternalId, null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET_TYPE.getScriptId())));
			itemCustomFields.add(assetType);
		}

		VendorBill vendorBillRecord = (VendorBill)vendorBillResponse.getRecord();		
		vendorBillRecord.setExternalId(invoice.getExternalId().toString());
		vendorBillRecord.setTranId(invoice.getTranId());
		//vendorBillRecord.setEntity(new RecordRef(null, null, invoice.getVendor(), RecordType.vendor));
		vendorBillRecord.setTranDate(DateUtil.convertToCalendar(invoice.getTranDate()));
		vendorBillRecord.setDueDate(null);
		vendorBillRecord.setTerms(null);
		vendorBillRecord.setAccount(new RecordRef(invoice.getPayableAccount(), null, null, RecordType.account));
		vendorBillRecord.setBillAddressList(new RecordRef(null, invoice.getVendorAddressInternalId().toString(), null, null));		
		vendorBillRecord.setUserTotal(invoice.rolledUpLineRate().setScale(2).doubleValue());						
		vendorBillRecord.setCustomFieldList(new CustomFieldList(customFields.toArray(CustomFieldRef[]::new)));

		VendorBillItem vendorBillSingleItemRecord = vendorBillRecord.getItemList().getItem()[0];
		vendorBillSingleItemRecord.setRate(invoice.rolledUpLineRate().toString());
		vendorBillSingleItemRecord.setAmount(invoice.rolledUpLineRate().setScale(2).doubleValue());
		vendorBillSingleItemRecord.set_class(new RecordRef(null, lookupClassfication(invoice.rolledUpLineBusinessUnit()), null, RecordType.classification));
		vendorBillSingleItemRecord.setDepartment(new RecordRef(null, lookupDepartment(invoice.rolledUpLineDepartment()), null, RecordType.department));

		//TODO Create a helper method that will extend an NS typed array
		itemCustomFields.addAll(new ArrayList<>(Arrays.asList(vendorBillSingleItemRecord.getCustomFieldList().getCustomField())));
		vendorBillSingleItemRecord.getCustomFieldList().setCustomField(itemCustomFields.toArray(CustomFieldRef[]::new));        

		return vendorBillRecord;
	}

	//TODO Generalize
	private String lookupItemInternalId(InvoiceLineVO line) throws Exception {
		String internalId = null;

		try {
			internalId = (String)suiteAnalyticsCacheService.getItem(line.getItem()).get(ItemSuiteAnalyticsService.INTERNAL_ID); //TODO This a temporary fix. Item should be retrieved from API.
			if(internalId == null) {
				throw new Exception("Item does not exist");
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in while searching for item %s in the external system", line.getItem()), e);			
		}

		return internalId;
	}

	private String lookupDepartment(InvoiceLineVO line) {return lookupDepartment(line.getDepartment());}
	private String lookupDepartment(String name) {
		String departmentInternalId = null;

		try {
			departmentInternalId = suiteTalkCacheService.searchDepartmentInternalIdByName(name);
		} catch(Exception e) {
			departmentInternalId = null;
		}

		return departmentInternalId;
	}

	private String lookupClassfication(InvoiceLineVO line) {return line.getBusinessUnit() == null ? null : lookupClassfication(line.getBusinessUnit().getName());}
	private String lookupClassfication(String name) {
		String classInternalId = null;

		try {
			classInternalId = suiteTalkCacheService.searchClassificationRecordId(name);
		} catch(Exception e) {
			classInternalId = null;
		}

		return classInternalId;
	}

}

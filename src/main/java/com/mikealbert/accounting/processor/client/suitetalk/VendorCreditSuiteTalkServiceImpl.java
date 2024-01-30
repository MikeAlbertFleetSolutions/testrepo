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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suiteanalytics.ItemSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.VendorBillFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.util.data.DateUtil;
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
import com.netsuite.webservices.transactions.purchases_2023_2.VendorCredit;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorCreditApply;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorCreditItem;
import com.netsuite.webservices.transactions.purchases_2023_2.VendorCreditItemList;

@Service("vendorCreditSuiteTalkService")
public class VendorCreditSuiteTalkServiceImpl extends BaseSuiteTalkService implements VendorCreditSuiteTalkService {
	@Resource DocDAO docDAO;
	@Resource PurchaseOrderSuiteTalkService purchaseOrderSuiteTalkService;
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Override
	public void create(CreditVO credit) throws Exception {		
        VendorCredit vendorCredit = convertCreditToVendorCredit(credit);
        
		WriteResponse response = service.getService().add(vendorCredit);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error creating Vendor Bill for invoice extId %s ", credit.getExternalId()), response);
		}	
				
		LOG.info("Successfully created Vendor bill for invoice extId {}", credit.getExternalId());
	}
		
	@Override
	public void delete(String externalId) throws Exception {
		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.vendorCredit);
		WriteResponse response = service.getService().delete(recordRef, null);		
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.SSS_MISSING_REQD_ARGUMENT)) {			
				LOG.warn("Delete Vendor Credit handled error from accounting system: ExternalId {} - {} ", externalId, response.getStatus().getStatusDetail()[0].getMessage());						
			} else {
				throw new SuiteTalkException("Error deleting vendorCredit", response);
			}
		}	
	}

	@Override
	public Map<String, Object> get(String externalId) throws Exception {
		Map<String, Object> vendorCreditMap = new HashMap<>();
		
		RecordRef recordRef = new RecordRef(null, null, externalId, RecordType.vendorCredit);		
		ReadResponse response = service.getService().get(recordRef);		
		if(response.getStatus().isIsSuccess()) {
			VendorCredit vendorCredit = (VendorCredit)response.getRecord();
			vendorCreditMap.put(VendorBillFieldEnum.INTERNAL_ID.getScriptId(), vendorCredit.getInternalId());
			vendorCreditMap.put(VendorBillFieldEnum.EXTERNAL_ID.getScriptId(), vendorCredit.getExternalId());
			vendorCreditMap.put(VendorBillFieldEnum.TRAN_ID.getScriptId(), vendorCredit.getTranId());
			vendorCreditMap.put(VendorBillFieldEnum.ENTITY.getScriptId(), vendorCredit.getEntity().getName());
			vendorCreditMap.put(VendorBillFieldEnum.SUBSIDIARY.getScriptId(), vendorCredit.getSubsidiary().getInternalId());
			vendorCreditMap.put(VendorBillFieldEnum.ACCOUNT.getScriptId(), vendorCredit.getAccount().getName());
			vendorCreditMap.put(VendorBillFieldEnum.TRAN_DATE.getScriptId(), vendorCredit.getTranDate());
			vendorCreditMap.put(VendorBillFieldEnum.MEMO.getScriptId(), vendorCredit.getMemo());			
			vendorCreditMap.put(VendorBillFieldEnum.BILL_ADDRESS_LIST.getScriptId(), vendorCredit.getBillAddressList() == null ? null : vendorCredit.getBillAddressList().getInternalId());

			Map<Object, CustomFieldRef> customListMap = Arrays.asList(vendorCredit.getCustomFieldList().getCustomField()).stream()
					.collect(Collectors.toMap(e -> e.getScriptId(), e -> e));
			vendorCreditMap.put(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId(), ((SelectCustomFieldRef)customListMap.get(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId())).getValue().getName());
			vendorCreditMap.put(VendorBillFieldEnum.MAIN_PO.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(VendorBillFieldEnum.MAIN_PO.getScriptId())).isValue());
			
			// TODO 11/02/20 Auto Approve flag is no longer is the custom field list. Confirm with Kevin
			//vendorCreditMap.put(VendorBillFieldEnum.AUTO_APPROVE.getScriptId(), ((BooleanCustomFieldRef)customListMap.get(VendorBillFieldEnum.AUTO_APPROVE.getScriptId())).isValue());
						
			// TODO 11/02/20 Unit is no longer is the custom field list. Confirm with Kevin
			//vendorCreditMap.put(CustomSegmentEnum.UNIT_NO.getScriptId(), ((SelectCustomFieldRef)customListMap.get(CustomSegmentEnum.UNIT_NO.getScriptId())).getValue().getName());			
			
			try {
				vendorCreditMap.put(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId(), ((StringCustomFieldRef)customListMap.get(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId())).getValue());
			} catch(NullPointerException npe) {
				vendorCreditMap.put(VendorBillFieldEnum.UPDATE_CONTROL_CODE.getScriptId(), null);				
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> items = new ObjectMapper().convertValue(Arrays.asList(vendorCredit.getItemList().getItem()), List.class);
			vendorCreditMap.put(VendorBillFieldEnum.ITEM_LIST.getScriptId(), items);						
		} else {
			throw new SuiteTalkException(String.format("Error reading vendor bill: externalId = %s", externalId), response);
		}	
		
		return vendorCreditMap;
	}

	private VendorCredit convertCreditToVendorCredit(CreditVO credit) throws Exception {
		String itemInternalId = null;
		String unitInternalId = null;
		String departmentInternalId = null;
		String classInternalId = null;
		
		List<Object> customFields, itemCustomFields;
		
		try {
			itemInternalId = (String)suiteAnalyticsCacheService.getItem(credit.getLines().get(0).getItem()).get(ItemSuiteAnalyticsService.INTERNAL_ID); //TODO This a temporary fix. Item should be retrieved from API.
			if(itemInternalId == null) {
				throw new Exception("Item does not exist");
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for item %s in the external system", credit.getExternalId(), credit.getLines().get(0).getItem()), e);			
		}

		try {
			classInternalId = suiteTalkCacheService.searchClassificationRecordId(credit.getLines().get(0).getBusinessUnit().getName());
		} catch(Exception e) {
			classInternalId = null;
		}
		
		try {
			departmentInternalId = suiteTalkCacheService.searchDepartmentInternalIdByName(credit.getLines().get(0).getDepartment());
		} catch(Exception e) {
			departmentInternalId = null;
		}
		
		try {
			unitInternalId = suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), credit.getLines().get(0).getUnit());
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in invoice %s while searching for unit no %s in the external system", credit.getExternalId(), credit.getLines().get(0).getUnit()), e);				
		}		

		InitializeRef vendorBillRef = new InitializeRef();
		vendorBillRef.setType(InitializeRefType.vendorBill);
		vendorBillRef.setExternalId(credit.getParentExternalId().toString());			
		
		InitializeRecord vendorCredit = new InitializeRecord();
		vendorCredit.setType(InitializeType.vendorCredit);
		vendorCredit.setReference(vendorBillRef);
		
		ReadResponse vendorCreditResponse = service.getService().initialize(vendorCredit);
		if(!vendorCreditResponse.getStatus().isIsSuccess()) {
			if(vendorCreditResponse.getStatus().getStatusDetail(0).getCode() == StatusDetailCodeType.INVALID_REF_KEY) {
				throw new RetryableSuiteTalkException(String.format("Error initializing Vendor Credit. %s ", credit.toString()), vendorCreditResponse);				
			} else {				
				throw new SuiteTalkException(String.format("Error initializing Vendor Credit. %s ", credit.toString()), vendorCreditResponse);
			}
		}
		
		customFields = new ArrayList<>(0);
		SelectCustomFieldRef approvalDepartment = new SelectCustomFieldRef();
		approvalDepartment.setScriptId(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId());		
		approvalDepartment.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchDepartmentInternalIdByName(credit.getApprovalDepartment()), null, null));		//TODO Need to lookup internal id
		customFields.add(approvalDepartment);
		
		BooleanCustomFieldRef automaticApproval = new BooleanCustomFieldRef();
		automaticApproval.setScriptId(VendorBillFieldEnum.AUTO_APPROVE.getScriptId());
		automaticApproval.setValue(credit.isAutoApprove());
		customFields.add(automaticApproval);
		
		SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
		unitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
		unitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
		customFields.add(unitNo);		
			
		VendorCredit vendorCreditRecord = (VendorCredit)vendorCreditResponse.getRecord();
		vendorCreditRecord.setExternalId(credit.getExternalId().toString());
		vendorCreditRecord.setTranId(credit.getTranId());
		vendorCreditRecord.setTranDate(DateUtil.convertToCalendar(credit.getTranDate()));
		vendorCreditRecord.setAccount(new RecordRef(credit.getPayableAccount(), null, null, RecordType.account));
		vendorCreditRecord.setMemo(credit.getMemo());
		vendorCreditRecord.setBillAddressList(new RecordRef(null, credit.getVendorAddressInternalId().toString(), null, null));
		vendorCreditRecord.setSubsidiary(new RecordRef(null, credit.getSubsidiary().toString(), null, RecordType.subsidiary));
		vendorCreditRecord.setUserTotal(credit.rolledUpLineRate().setScale(2).doubleValue());
        vendorCreditRecord.setCustomFieldList(new CustomFieldList(customFields.toArray(CustomFieldRef[]::new)));
        vendorCreditRecord.setAutoApply(false);
        vendorCreditRecord.setApplied(vendorCreditRecord.getUserTotal());
        
        for(VendorCreditApply vca : vendorCreditRecord.getApplyList().getApply()) {
        	if(vca.getApply()) {
        		vca.setAmount(vendorCreditRecord.getApplied());
        	}
        }
        				
		List<VendorCreditItem> vendorCreditItems = new ArrayList<>(0);
		for(CreditLineVO line : credit.getLines()) {
			itemCustomFields = new ArrayList<>(0);
						
			VendorCreditItem item = new VendorCreditItem();
			item.setItem(new RecordRef(null, itemInternalId, null, RecordType.inventoryItem)); //TODO This is a patch, need to re-evaluate approach here
			item.setQuantity(line.getQuantity().doubleValue());
			item.setRate(line.getRate().toString());
			item.setDescription(line.getDescription());			
			item.set_class(new RecordRef(null, classInternalId, null, RecordType.classification));
			item.setDepartment(new RecordRef(null, departmentInternalId, null, RecordType.department));
			
			SelectCustomFieldRef itemUnitNo = new SelectCustomFieldRef();
			itemUnitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
			itemUnitNo.setValue(new ListOrRecordRef(null, unitInternalId, null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
			itemCustomFields.add(itemUnitNo);	

			item.setCustomFieldList(new CustomFieldList(itemCustomFields.toArray(CustomFieldRef[]::new)));
			
			vendorCreditItems.add(item);
		}		
		vendorCreditRecord.setItemList(new VendorCreditItemList(vendorCreditItems.toArray(VendorCreditItem[]::new), true));
        
        return vendorCreditRecord;
	}
	
}

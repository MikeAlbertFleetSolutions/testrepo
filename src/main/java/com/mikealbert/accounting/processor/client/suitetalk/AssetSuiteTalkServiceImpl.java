package com.mikealbert.accounting.processor.client.suitetalk;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suiteanalytics.AssetSuiteAnalyticsService;
import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.enumeration.AssetFieldEnum;
import com.mikealbert.accounting.processor.enumeration.AssetRevaluationFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomListEnum;
import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.service.AssetIntegrationService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.constant.accounting.enumeration.AssetRevalueTypeUpdateEnum;
import com.mikealbert.util.data.DateUtil;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomRecordRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecord;

@Service("assetSuiteTalkService")
public class AssetSuiteTalkServiceImpl extends BaseSuiteTalkService implements AssetSuiteTalkService{
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource AssetSuiteAnalyticsService assetSuiteAnalyticsService;
	@Resource UnitService fleetMasterService;
	@Resource AssetIntegrationService assetIntegrationService;
	
	//This is work in progress
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void updateExtIdOnNgAsset(NgAssetVO ngAsset, Long assetId) throws Exception {
		LOG.info(String.format("Begin updateExtIdOnNgAsset for NgAssetId: %s and Willow AssetId: %s", ngAsset.getNgAssetId(), assetId));
		Map<String, String> asset;
		
		asset = service.searchForCustomRecordsByScriptIdAndInternalId(CustomRecordEnum.ASSET.getScriptId(), ngAsset.getNgAssetId());
		
		//Update ExternalId in NS
		WriteResponse response = service.getService().update(createCustomRecordToUpdateExternalId(asset, assetId));
		
		if(response.getStatus().isIsSuccess()) {
			asset.put(AssetSuiteTalkService.externalId, String.valueOf(assetId));
			ngAsset.setNgAssetExtid(assetId);
		} else {
			throw new SuiteTalkException(String.format("Error Updating ExternalId(WillowAssetId): %s for NgAssetIntrnalId: %s", assetId, ngAsset.getNgAssetId()), response);			
		}

		LOG.info(String.format("End updateExtIdOnNgAsset for NgAssetId: %s and Willow AssetId: %s", ngAsset.getNgAssetId(), assetId));
	}
	
	private CustomRecord createCustomRecordToUpdateExternalId(Map<String, String> asset, Long assetId) throws Exception {
		CustomRecord customRecord = new CustomRecord();
	    String internalId = asset.get(AssetFieldEnum.INTERNAL_ID.getScriptId());
	    		
		customRecord.setInternalId(internalId);
		customRecord.setExternalId(String.valueOf(assetId));
		customRecord.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		
		return customRecord;
	}
	
	
	@Override
	public String putAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetVO) throws Exception {
		LOG.info(String.format("Begin putAssetPlaceInServiceRecord for AssetId: %s ", assetVO.getAssetId()));
				
		//Update ExternalId in NS
		WriteResponse response = service.getService().upsert(createCustomRecordToPlaceAssetInService(assetVO));
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error Placing Asset In Service ExternalId(WillowAssetId): %s ", assetVO.getAssetId()), response);			
		}
		
		return String.format("Asset Placed In Service: %s", assetVO.getAssetId());
	}

	private CustomRecord createCustomRecordToPlaceAssetInService(AssetPlaceInServiceVO assetVO) throws Exception {
		CustomRecord customRecord = new CustomRecord();
		List<Object> assetCustomFields = new ArrayList<>();		

		String classInternalId = null;
		//String unitInternalId = null;
		String assetTypeInternalId = null;

		StringCustomFieldRef vin = new StringCustomFieldRef();
		vin.setScriptId(AssetFieldEnum.VIN.getScriptId());
		vin.setValue(assetVO.getVin());
		assetCustomFields.add(vin);

		assetTypeInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_TYPE.getScriptId(), assetVO.getType());
		StringCustomFieldRef astType = new StringCustomFieldRef();
		astType.setScriptId(AssetFieldEnum.TYPE.getScriptId());
		astType.setValue(assetTypeInternalId);
		assetCustomFields.add(astType);
		
		//LAFS-1452
		if (assetVO.getStartDate() != null) {
			DateCustomFieldRef inServiceDate = new DateCustomFieldRef();
			inServiceDate.setScriptId(AssetFieldEnum.IN_SERVICE_DATE.getScriptId());
			inServiceDate.setValue(DateUtil.convertToCalendar(assetVO.getStartDate()));
			assetCustomFields.add(inServiceDate);
		}
		
		DateCustomFieldRef endDate = new DateCustomFieldRef();
		endDate.setScriptId(AssetFieldEnum.END_OF_LIFE_DATE.getScriptId());
		endDate.setValue(DateUtil.convertToCalendar(assetVO.getEndDate()));
		assetCustomFields.add(endDate);
		
		LongCustomFieldRef useFulLife = new LongCustomFieldRef();
		useFulLife.setScriptId(AssetFieldEnum.USEFUL_LIFE.getScriptId());
		useFulLife.setValue(assetVO.getUseFulLife());
		assetCustomFields.add(useFulLife);
		
		LongCustomFieldRef remainingUseFulLife = new LongCustomFieldRef();
		remainingUseFulLife.setScriptId(AssetFieldEnum.REMAINING_USEFUL_LIFE.getScriptId());
		remainingUseFulLife.setValue(assetVO.getUseFulLife());
		assetCustomFields.add(remainingUseFulLife);
		
		DoubleCustomFieldRef residualValueEstimate = new DoubleCustomFieldRef();
		residualValueEstimate.setScriptId(AssetFieldEnum.RESIDUAL_VALUE.getScriptId());
		residualValueEstimate.setValue(assetVO.getResidualValue().doubleValue());
		assetCustomFields.add(residualValueEstimate);
		
		DoubleCustomFieldRef extendedResidualValueEstimate = new DoubleCustomFieldRef();
		extendedResidualValueEstimate.setScriptId(AssetFieldEnum.EXTENDED_LIFE_RESIDUAL_VALUE_ESTIMATE.getScriptId());
		extendedResidualValueEstimate.setValue(0D);
		assetCustomFields.add(extendedResidualValueEstimate);

		StringCustomFieldRef penddingLive = new StringCustomFieldRef();
		penddingLive.setScriptId(AssetFieldEnum.PENDING_LIVE.getScriptId());
		penddingLive.setValue(CommonConstants.False);
		assetCustomFields.add(penddingLive);
		
		StringCustomFieldRef lockedTrans = new StringCustomFieldRef();
		lockedTrans.setScriptId(AssetFieldEnum.LOCKED_TRANS.getScriptId());
		lockedTrans.setValue(CommonConstants.False);
		assetCustomFields.add(lockedTrans);
		
		if(assetVO.getDepartment() != null) {
			SelectCustomFieldRef department = new SelectCustomFieldRef();
			department.setScriptId(AssetFieldEnum.DEPARTMENT.getScriptId());		
			department.setValue(new ListOrRecordRef(null, assetVO.getDepartment(), null, AssetFieldEnum.DEPARTMENT.getRecordTypeId()));
			assetCustomFields.add(department);
		}

		if(assetVO.getBusinessUnit() != null) {
			classInternalId = suiteTalkCacheService.searchClassificationRecordId(assetVO.getBusinessUnit());
			SelectCustomFieldRef businessUnit = new SelectCustomFieldRef();
			businessUnit.setScriptId(AssetFieldEnum.CLASS.getScriptId());		
			businessUnit.setValue(new ListOrRecordRef(null, classInternalId, null, CustomSegmentEnum.CLASS.getRecordTypeId()));
			assetCustomFields.add(businessUnit);
		}
		
		if(assetVO.getParentAssetId() != null) {
			NgAssetVO ngAsset = assetSuiteAnalyticsService.getAssetByExtId(assetVO.getParentAssetId());
			StringCustomFieldRef parentAseet = new StringCustomFieldRef();
			parentAseet.setScriptId(AssetFieldEnum.PARRENT_ASSET.getScriptId());
			parentAseet.setValue(String.valueOf(ngAsset.getNgAssetId()));
			assetCustomFields.add(parentAseet);
		}
				
		CustomFieldList assetFieldList = new CustomFieldList(assetCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord.setExternalId(String.valueOf(assetVO.getAssetId()));
		customRecord.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		customRecord.setCustomFieldList(assetFieldList);
						
		return customRecord;
	}
	
	@Override
	public String putAssetCreateRecord(AssetCreateVO assetVO) throws Exception {
		LOG.info(String.format("Begin putAssetCreateRecord for ApDocId:%s ArDocId: %s ArLineId: %s", assetVO.getInvoiceApDocId(), assetVO.getInvoiceArDocId(), assetVO.getInvoiceArLineId()));
				
		//Create New Asset in NS
		WriteResponse response = service.getService().upsert(createCustomRecordToCreateAsset(assetVO));
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Begin putAssetCreateRecord for ApDocId:%s ArDocId: %s ArLineId: %s", assetVO.getInvoiceApDocId(), assetVO.getInvoiceArDocId(), assetVO.getInvoiceArLineId()), response);			
		}
		
		return String.format("Asset Create DocId: %s LineId: %s", assetVO.getInvoiceArDocId(), assetVO.getInvoiceArLineId());
	}

	private CustomRecord createCustomRecordToCreateAsset(AssetCreateVO assetVO) throws Exception {
		CustomRecord customRecord = new CustomRecord();
		List<Object> assetCustomFields = new ArrayList<>();		

		String classInternalId = null;
		String unitInternalId = null;
		String assetTypeInternalId = null;
		String depreciationMethodInternalId = null;

		assetTypeInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_TYPE.getScriptId(), assetVO.getType());
		StringCustomFieldRef astType = new StringCustomFieldRef();
		astType.setScriptId(AssetFieldEnum.TYPE.getScriptId());
		astType.setValue(assetTypeInternalId);
		assetCustomFields.add(astType);
		
		depreciationMethodInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_DEPRECIATION_METHOD.getScriptId(), assetVO.getDepreciationMethodName());
		StringCustomFieldRef depreciationMethod = new StringCustomFieldRef();
		depreciationMethod.setScriptId(AssetFieldEnum.DEPRECIATION_METHOD.getScriptId());
		depreciationMethod.setValue(depreciationMethodInternalId);
		assetCustomFields.add(depreciationMethod);

		StringCustomFieldRef subsidary = new StringCustomFieldRef();
		subsidary.setScriptId(AssetFieldEnum.SUBSIDARY.getScriptId());
		subsidary.setValue(assetVO.getcId());
		assetCustomFields.add(subsidary);
		
		DateCustomFieldRef acquisationDate = new DateCustomFieldRef();
		acquisationDate.setScriptId(AssetFieldEnum.ACQUISATION_DATE.getScriptId());
		acquisationDate.setValue(DateUtil.convertToCalendar(assetVO.getStartDate()));
		assetCustomFields.add(acquisationDate);
		
		DateCustomFieldRef inServiceDate = new DateCustomFieldRef();
		inServiceDate.setScriptId(AssetFieldEnum.IN_SERVICE_DATE.getScriptId());
		inServiceDate.setValue(DateUtil.convertToCalendar(assetVO.getStartDate()));
		assetCustomFields.add(inServiceDate);
		
		LongCustomFieldRef useFulLife = new LongCustomFieldRef();
		useFulLife.setScriptId(AssetFieldEnum.USEFUL_LIFE.getScriptId());
		useFulLife.setValue(assetVO.getUseFulLife());
		assetCustomFields.add(useFulLife);
		
		StringCustomFieldRef vin = new StringCustomFieldRef();
		vin.setScriptId(AssetFieldEnum.VIN.getScriptId());
		vin.setValue(assetVO.getVin());
		assetCustomFields.add(vin);
		
		if(assetVO.getUpdateControlCode() != null) {
			StringCustomFieldRef updateControlCode = new StringCustomFieldRef();
			updateControlCode.setScriptId(AssetFieldEnum.UPDATE_CONTROL_CODE.getScriptId());
			updateControlCode.setValue(assetVO.getUpdateControlCode());
			assetCustomFields.add(updateControlCode);
		}
		
		if(assetVO.getStatusName() != null) {
			SelectCustomFieldRef status = new SelectCustomFieldRef();
			status.setScriptId(CustomListEnum.ASSET_STATUS.getScriptId());		
			status.setValue(new ListOrRecordRef(null, assetVO.getStatusName(), null, AssetFieldEnum.STATUS.getScriptId()));
			assetCustomFields.add(status);
		}
		
		DoubleCustomFieldRef residualValueEstimate = new DoubleCustomFieldRef();
		residualValueEstimate.setScriptId(AssetFieldEnum.RESIDUAL_VALUE.getScriptId());
		residualValueEstimate.setValue(assetVO.getResidualValue().doubleValue());
		assetCustomFields.add(residualValueEstimate);
		
		DoubleCustomFieldRef extendedResidualValueEstimate = new DoubleCustomFieldRef();
		extendedResidualValueEstimate.setScriptId(AssetFieldEnum.EXTENDED_LIFE_RESIDUAL_VALUE_ESTIMATE.getScriptId());
		extendedResidualValueEstimate.setValue(0D);
		assetCustomFields.add(extendedResidualValueEstimate);

		DoubleCustomFieldRef capitalizedValue = new DoubleCustomFieldRef();
		capitalizedValue.setScriptId(AssetFieldEnum.CAPITALIZED_VALUE.getScriptId());
		capitalizedValue.setValue(assetVO.getInitialValue().doubleValue());
		assetCustomFields.add(capitalizedValue);

		if(assetVO.getUnitNo() != null) {
			unitInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.UNIT.getScriptId(), assetVO.getUnitNo());
			StringCustomFieldRef unitNo = new StringCustomFieldRef();
			unitNo.setScriptId(AssetFieldEnum.UNIT.getScriptId());
			unitNo.setValue(unitInternalId);
			assetCustomFields.add(unitNo);

			try {
				SelectCustomFieldRef unitNoSeg = new SelectCustomFieldRef();
				unitNoSeg.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
				unitNoSeg.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), assetVO.getUnitNo()), null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
				assetCustomFields.add(unitNoSeg);		
			} catch (Exception e) {
				throw new RemoteException(String.format("Error occurred in asset %s while searching for unit no %s in the external system", assetVO.getAssetId(), assetVO.getUnitNo()), e);
			}
			
		}

		if(assetVO.getDepartment() != null) {
			SelectCustomFieldRef department = new SelectCustomFieldRef();
			department.setScriptId(AssetFieldEnum.DEPARTMENT.getScriptId());		
			department.setValue(new ListOrRecordRef(null, assetVO.getDepartment(), null, AssetFieldEnum.DEPARTMENT.getRecordTypeId()));
			assetCustomFields.add(department);
		}

		if(assetVO.getBusinessUnit() != null) {
			classInternalId = suiteTalkCacheService.searchClassificationRecordId(assetVO.getBusinessUnit());
			SelectCustomFieldRef businessUnit = new SelectCustomFieldRef();
			businessUnit.setScriptId(AssetFieldEnum.CLASS.getScriptId());		
			businessUnit.setValue(new ListOrRecordRef(null, classInternalId, null, CustomSegmentEnum.CLASS.getRecordTypeId()));
			assetCustomFields.add(businessUnit);
		}
						
		StringCustomFieldRef invoiceNo = new StringCustomFieldRef();
		invoiceNo.setScriptId(AssetFieldEnum.INVOICE_NO.getScriptId());
		invoiceNo.setValue(assetVO.getInvoiceNo());
		assetCustomFields.add(invoiceNo);
		
		CustomFieldList assetFieldList = new CustomFieldList(assetCustomFields.toArray(CustomFieldRef[]::new));		
		customRecord.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		customRecord.setAltName(assetVO.getDescription());
		customRecord.setExternalId(String.format("-%s%s",assetVO.getInvoiceArDocId(),assetVO.getInvoiceArLineId()));
		customRecord.setCustomFieldList(assetFieldList);
						
		return customRecord;
	}
	
	@Override
	public void deleteAsset(AssetCreateVO assetVO) throws Exception {
		String customRecordTypeId = suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId());		
		String customRecordInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET.getScriptId(), assetVO.getDescription());
		
		CustomRecordRef customRecordRef = new CustomRecordRef(null, customRecordInternalId, null, customRecordTypeId, null);	
		
		WriteResponse response = service.getService().delete(customRecordRef, null);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error occured while deleting unit", response);			
		}
	}

	@Override
	public String putAssetTypeUpdateRecord(AssetTypeUpdateVO assetVO) throws Exception {
		LOG.info(String.format("Begin putAssetTypeUpdateRecord for AssetId: %s ", assetVO.getAssetId()));
				
		//Update AssetType in NS
		CustomRecord customAssetRecord = createCustomAssetTypeUpdateRecord(assetVO);
		WriteResponse response = service.getService().upsert(customAssetRecord);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error Updating AssetType ExternalId(WillowAssetId): %s, New AssetType: %s.", assetVO.getAssetId(), assetVO.getNewType()), response);			
		}
		
		return String.format("Update AssetType AssetId: %s", assetVO.getAssetId());
	}

	private CustomRecord createCustomAssetTypeUpdateRecord(AssetTypeUpdateVO assetVO) throws Exception {
		CustomRecord customRecordAsset = new CustomRecord();
		List<Object> assetCustomFields = new ArrayList<>();		

		String assetTypeInternalId = null;

		if(AssetRevalueTypeUpdateEnum.RE_LEASE.equals(assetVO.getUpdateContext())) {
			StringCustomFieldRef updateControlCode = new StringCustomFieldRef();
			updateControlCode.setScriptId(AssetFieldEnum.UPDATE_CONTROL_CODE.getScriptId());
			updateControlCode.setValue(assetVO.getProductCode());
			assetCustomFields.add(updateControlCode);

			if(assetVO.getDepartment() != null) {
				SelectCustomFieldRef department = new SelectCustomFieldRef();
				department.setScriptId(AssetFieldEnum.DEPARTMENT_NEW.getScriptId());		
				department.setValue(new ListOrRecordRef(null, assetVO.getDepartment(), null, AssetFieldEnum.DEPARTMENT_NEW.getRecordTypeId()));
				assetCustomFields.add(department);
			}

			if(assetVO.getBusinessUnit() != null) {
				String classInternalId = suiteTalkCacheService.searchClassificationRecordId(assetVO.getBusinessUnit());
				SelectCustomFieldRef businessUnit = new SelectCustomFieldRef();
				businessUnit.setScriptId(AssetFieldEnum.BUSINESS_UNIT_NEW.getScriptId());		
				businessUnit.setValue(new ListOrRecordRef(null, classInternalId, null, CustomSegmentEnum.CLASS.getRecordTypeId()));
				assetCustomFields.add(businessUnit);
			}

		}

		assetTypeInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_TYPE.getScriptId(), assetVO.getNewType());
		StringCustomFieldRef astType = new StringCustomFieldRef();
		astType.setScriptId(AssetFieldEnum.NEW_ASSET_TYPE.getScriptId());
		astType.setValue(assetTypeInternalId);
		assetCustomFields.add(astType);
						
		CustomFieldList assetFieldList = new CustomFieldList(assetCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecordAsset.setExternalId(String.valueOf(assetVO.getAssetId()));
		customRecordAsset.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		customRecordAsset.setCustomFieldList(assetFieldList);
						
		return customRecordAsset;
	}

	@Override
	public String cancelPoToStock(AssetCancelPoToStockVO assetVO) throws Exception {
		LOG.info(String.format("Begin cancelPoToStock for AssetId: %s ", assetVO.getAssetId()));
				
		//Update AssetType in NS
		CustomRecord customAssetRecord = createCustomCancelPoToStockRecord(assetVO);
		WriteResponse response = service.getService().upsert(customAssetRecord);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error while CancelPoToStock ExternalId(WillowAssetId): %s.", assetVO.getAssetId()), response);			
		}
		
		return String.format("Update AssetType AssetId: %s", assetVO.getAssetId());
	}

	private CustomRecord createCustomCancelPoToStockRecord(AssetCancelPoToStockVO assetVO) throws Exception {
		CustomRecord customRecordAsset = new CustomRecord();
		List<Object> assetCustomFields = new ArrayList<>();		

		String assetTypeInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_TYPE.getScriptId(), assetVO.getAssetType());
		StringCustomFieldRef astType = new StringCustomFieldRef();
		astType.setScriptId(AssetFieldEnum.ASSET_TYPE.getScriptId());
		astType.setValue(assetTypeInternalId);
		assetCustomFields.add(astType);
						
		BooleanCustomFieldRef pendingLive = new BooleanCustomFieldRef();
		pendingLive.setScriptId(AssetFieldEnum.PENDING_LIVE.getScriptId());
		pendingLive.setValue(assetVO.isPendingLive());
		assetCustomFields.add(pendingLive);
		
		StringCustomFieldRef updateControlCode = new StringCustomFieldRef();
		updateControlCode.setScriptId(AssetFieldEnum.UPDATE_CONTROL_CODE.getScriptId());
		updateControlCode.setValue(assetVO.getUpdateControlCode());
		assetCustomFields.add(updateControlCode);
		
		CustomFieldList assetFieldList = new CustomFieldList(assetCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecordAsset.setExternalId(String.valueOf(assetVO.getAssetId()));
		customRecordAsset.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		customRecordAsset.setCustomFieldList(assetFieldList);
						
		return customRecordAsset;
	}

	@Override
	public String dispose(AssetDisposalVO assetVO) throws Exception {
		LOG.info(String.format("Begin dispose for AssetId: %s ", assetVO.getAssetId()));
				
		//Update Disposal Information in NS
		WriteResponse response = service.getService().upsert(createAssetRecordToDispose(assetVO));
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error Disposing Asset ExternalId(WillowAssetId): %s ", assetVO.getAssetId()), response);			
		}
		
		return String.format("Dispose Asset: %s", assetVO.getAssetId());
	}

	private CustomRecord createAssetRecordToDispose(AssetDisposalVO assetVO) throws Exception {
		CustomRecord customRecord = new CustomRecord();
		List<Object> assetCustomFields = new ArrayList<>();		
	
		StringCustomFieldRef disposalFlag = new StringCustomFieldRef();
		disposalFlag.setScriptId(AssetFieldEnum.DISPOSAL_FLAG.getScriptId());
		disposalFlag.setValue((assetVO.isDisposalFlag() ? CommonConstants.True : CommonConstants.False));
		assetCustomFields.add(disposalFlag);
		
		DateCustomFieldRef disposalDate = new DateCustomFieldRef();
		disposalDate.setScriptId(AssetFieldEnum.DISPOSAL_DATE.getScriptId());
		disposalDate.setValue(DateUtil.convertToCalendar(assetVO.getDisposalDate()));
		assetCustomFields.add(disposalDate);

		DoubleCustomFieldRef disposalProceeds = new DoubleCustomFieldRef();
		disposalProceeds.setScriptId(AssetFieldEnum.DISPOSAL_PROCEEDS.getScriptId());
		disposalProceeds.setValue(assetVO.getDisposalProceeds().doubleValue());
		assetCustomFields.add(disposalProceeds);
				
		CustomFieldList assetFieldList = new CustomFieldList(assetCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord.setExternalId(String.valueOf(assetVO.getAssetId()));
		customRecord.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId()), null, null));
		customRecord.setCustomFieldList(assetFieldList);
						
		return customRecord;
	}
	
	@Override
	public String revalue(AssetRevalueVO assetVO) throws Exception { 
		LOG.info(String.format("Begin revalue for AssetId: %s ", assetVO.getAssetId()));
				
		//Create/Update Revalue Asset in NS
		WriteResponse response = service.getService().upsert(createCustomAssetRevaluationRecord(assetVO));
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error Revaluing an asset ExternalId(WillowAssetId): %s ", assetVO.getAssetId()), response);			
		}

		//Update Asset Type in NS
		AssetTypeUpdateVO assetTypeUpdateVO = assetVO.getAssetTypeUpdateVO();
		putAssetTypeUpdateRecord(assetTypeUpdateVO);
				
		return String.format("Revalue Asset: %s", assetVO.getAssetId());
	}

	private CustomRecord createCustomAssetRevaluationRecord(AssetRevalueVO assetVO) throws Exception {
		CustomRecord customRecord = new CustomRecord();
		List<Object> assetRevalCustomFields = new ArrayList<>();
		
		String customRecordInternalId = this.service.getCustomRecordInternalId(CustomRecordEnum.ASSET.getScriptId(), String.valueOf(assetVO.getAssetId()));
		StringCustomFieldRef asset = new StringCustomFieldRef();
		asset.setScriptId(AssetRevaluationFieldEnum.ASSET.getScriptId());
		asset.setValue(customRecordInternalId);
		assetRevalCustomFields.add(asset);

		if(assetVO.getStatusName() != null) {
			String statusInternalId = this.service.searchForCustomListItemId(CustomListEnum.ASSET_PROPOSAL_STATUS.getScriptId(), assetVO.getStatusName());
			SelectCustomFieldRef status = new SelectCustomFieldRef();
			status.setScriptId(AssetRevaluationFieldEnum.STATUS.getScriptId());		
			status.setValue(new ListOrRecordRef(null, statusInternalId, null, AssetRevaluationFieldEnum.STATUS.getScriptId()));
			assetRevalCustomFields.add(status);
		}

		if(assetVO.getRevaluationType() != null) {
			String revaluationTypeInternalId = this.service.searchForCustomListItemId(CustomListEnum.ASSET_REVALUATION_TYPE.getScriptId(), assetVO.getRevaluationType());
			SelectCustomFieldRef revalType = new SelectCustomFieldRef();
			revalType.setScriptId(AssetRevaluationFieldEnum.TYPE.getScriptId());		
			revalType.setValue(new ListOrRecordRef(null, revaluationTypeInternalId, null, AssetRevaluationFieldEnum.TYPE.getScriptId()));
			assetRevalCustomFields.add(revalType);
		}

		DateCustomFieldRef effectiveDate = new DateCustomFieldRef();
		effectiveDate.setScriptId(AssetRevaluationFieldEnum.EFFECTIVE_DATE.getScriptId());
		effectiveDate.setValue(DateUtil.convertToCalendar(assetVO.getEffectiveDate()));
		assetRevalCustomFields.add(effectiveDate);

		String depreciationMethodInternalId = suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.ASSET_DEPRECIATION_METHOD.getScriptId(), assetVO.getDepreciationMethodName());
		StringCustomFieldRef depreciationMethod = new StringCustomFieldRef();
		depreciationMethod.setScriptId(AssetRevaluationFieldEnum.DEPRECIATION_METHOD.getScriptId());
		depreciationMethod.setValue(depreciationMethodInternalId);
		assetRevalCustomFields.add(depreciationMethod);

		if(assetVO.getResidualValueEstimate() != null) {
			DoubleCustomFieldRef residualValueEstimate = new DoubleCustomFieldRef();
			residualValueEstimate.setScriptId(AssetRevaluationFieldEnum.RESIDUAL_VALUE.getScriptId());
			residualValueEstimate.setValue(assetVO.getResidualValueEstimate().doubleValue());
			assetRevalCustomFields.add(residualValueEstimate);
		}
				
		if(assetVO.getRemainingUsefulLife() != null) {
			LongCustomFieldRef usefulLife = new LongCustomFieldRef();
			usefulLife.setScriptId(AssetRevaluationFieldEnum.REMAINING_USEFUL_LIFE.getScriptId());
			usefulLife.setValue(assetVO.getRemainingUsefulLife());
			assetRevalCustomFields.add(usefulLife);
		}

		if(assetVO.getRevalueUsefulLife() != null) {
			LongCustomFieldRef revalueUsefulLife = new LongCustomFieldRef();
			revalueUsefulLife.setScriptId(AssetRevaluationFieldEnum.REVALUE_USEFUL_LIFE.getScriptId());
			revalueUsefulLife.setValue(assetVO.getRevalueUsefulLife());
			assetRevalCustomFields.add(revalueUsefulLife);
		}

		if(assetVO.getEffectiveTo() != null) {
			DateCustomFieldRef effectiveTo = new DateCustomFieldRef();
			effectiveTo.setScriptId(AssetRevaluationFieldEnum.EFFECTIVE_TO_DATE.getScriptId());
			effectiveTo.setValue(DateUtil.convertToCalendar(assetVO.getEffectiveTo()));
			assetRevalCustomFields.add(effectiveTo);
		}
		
		CustomFieldList assetRevalFieldList = new CustomFieldList(assetRevalCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord.setExternalId(assetVO.getExternalId());
		customRecord.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET_REVALUATION.getScriptId()), null, null));
		customRecord.setCustomFieldList(assetRevalFieldList);
						
		return customRecord;
	}	
		
}

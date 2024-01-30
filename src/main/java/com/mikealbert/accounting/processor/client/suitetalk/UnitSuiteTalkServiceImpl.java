package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.CustomListEnum;
import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.UnitFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomRecordRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecord;

@Service("unitSuiteTalkService")
public class UnitSuiteTalkServiceImpl extends BaseSuiteTalkService implements UnitSuiteTalkService {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource StateSuiteTalkService stateSuiteTalkService;

	@Override
	public void deleteUnit(UnitVO unit) throws Exception {
		String customRecordTypeId = suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.UNIT.getScriptId());		
		String customRecordInternalId = suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), unit.getUnitNo());
		
		CustomRecordRef customRecordRef = new CustomRecordRef(null, customRecordInternalId, null, customRecordTypeId, null);	
		
		WriteResponse response = service.getService().delete(customRecordRef, null);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error occured while deleting unit", response);			
		}
	}

	@Override
	public String putUnit(UnitVO unitVO) throws Exception {
		String retVal;
		List<Object> customFields = new ArrayList<>(0);
		
		if(!(unitVO.getVin() == null || unitVO.getVin().isBlank())) {
			StringCustomFieldRef vin = new StringCustomFieldRef();
			vin.setScriptId(UnitFieldEnum.VIN.getScriptId());
			vin.setValue((String) unitVO.getVin());
			customFields.add(vin);
		}
		
		if (unitVO.getYear() != null) {
			StringCustomFieldRef year = new StringCustomFieldRef();
			year.setScriptId(UnitFieldEnum.YEAR.getScriptId());
			year.setValue((String) unitVO.getYear());
			customFields.add(year);
		}
		
		if (unitVO.getMake() != null) {
			StringCustomFieldRef make = new StringCustomFieldRef();
			make.setScriptId(UnitFieldEnum.MAKE.getScriptId());
			make.setValue((String) unitVO.getMake());
			customFields.add(make);
		}
		
		if (unitVO.getModel() != null) {
			StringCustomFieldRef model = new StringCustomFieldRef();
			model.setScriptId(UnitFieldEnum.MODEL.getScriptId());
			model.setValue((String) unitVO.getModel());
			customFields.add(model);
		}
		
		if (unitVO.getModelTypeDesc() != null) {
			StringCustomFieldRef modelType = new StringCustomFieldRef();
			modelType.setScriptId(UnitFieldEnum.MODEL_TYPE.getScriptId());
			modelType.setValue((String) unitVO.getModelTypeDesc());
			customFields.add(modelType);
		}
		
		if (unitVO.getFuelType() != null) {
			StringCustomFieldRef fuelType = new StringCustomFieldRef();
			fuelType.setScriptId(UnitFieldEnum.FUEL_TYPE.getScriptId());
			fuelType.setValue((String) unitVO.getFuelType());
			customFields.add(fuelType);	
		}
		
		if (unitVO.getGvr() != null) {
			StringCustomFieldRef gvr = new StringCustomFieldRef();
			gvr.setScriptId(UnitFieldEnum.GVR.getScriptId());
			gvr.setValue(Long.toString(unitVO.getGvr()));
			customFields.add(gvr);
		}
		
		if (unitVO.getHorsePower() != null) {
			StringCustomFieldRef horsePower = new StringCustomFieldRef();
			horsePower.setScriptId(UnitFieldEnum.HORSE_POWER.getScriptId());
			horsePower.setValue(unitVO.getHorsePower().toString());
			customFields.add(horsePower);
		}
		
		if (unitVO.getMsrp() != null) {
			StringCustomFieldRef msrp = new StringCustomFieldRef();
			msrp.setScriptId(UnitFieldEnum.MSRP.getScriptId());
			msrp.setValue(unitVO.getMsrp().toString());
			customFields.add(msrp);
		}

		if (unitVO.getNewUsed() != null) {
			StringCustomFieldRef newUsed = new StringCustomFieldRef();
			newUsed.setScriptId(UnitFieldEnum.NEW_USED.getScriptId());
			newUsed.setValue((String) unitVO.getNewUsed());
			customFields.add(newUsed);
		}

		if(unitVO.getCbv() !=  null) {
			DoubleCustomFieldRef cbv = new DoubleCustomFieldRef();
			cbv.setScriptId(UnitFieldEnum.CONTRACT_BOOK_VALUE.getScriptId());
			cbv.setValue(unitVO.getCbv().doubleValue());
			customFields.add(cbv);
		}

		if(unitVO.getVehicleClassification() != null) {
			SelectCustomFieldRef equipClassfication = new SelectCustomFieldRef();
			equipClassfication.setScriptId(UnitFieldEnum.EQUIPMENT_CLASSFICATION.getScriptId());		
			equipClassfication.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.EQUIPMENT_CLASSIFICATION.getScriptId(), unitVO.getVehicleClassification()), null, null));		
			customFields.add(equipClassfication);			
		}

		if(unitVO.getPlbType() != null) {
			SelectCustomFieldRef plbType = new SelectCustomFieldRef();
			plbType.setScriptId(UnitFieldEnum.PLB_TYPE.getScriptId());		
			plbType.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.PLB_TYPE.getScriptId(), unitVO.getPlbType()), null, null));		
			customFields.add(plbType);			
		}

		//Create Custom Field List, to be set in custom record 
		CustomFieldList customFieldList = new CustomFieldList(customFields.toArray(CustomFieldRef[]::new));
		
		CustomRecord custRec = new CustomRecord();
		custRec.setName(unitVO.getUnitNo());
		custRec.setExternalId(Long.toString(unitVO.getFmsId()));
		custRec.setIsInactive(false);
		custRec.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.UNIT.getScriptId()), null, null));
		custRec.setCustomFieldList(customFieldList);
		
		WriteResponse response = service.getService().upsert(custRec);	
		if(!response.getStatus().isIsSuccess()) {
			throw new RetryableSuiteTalkException(String.format("Error occured while upserting unit %s -- %s", unitVO.toString(), custRec.getExternalId()) , response);			
		}
		
		retVal = ((CustomRecordRef)response.getBaseRef()).getExternalId();
		return  retVal;				
	}

	@Override
	public UnitVO fetchByExternalId(String externalId) throws Exception {
	    CustomRecordRef custRecRef = new CustomRecordRef();
		custRecRef.setScriptId(CustomRecordEnum.UNIT.getScriptId());
		custRecRef.setExternalId(externalId);

		ReadResponse response = service.getService().get(custRecRef);
		if(!response.getStatus().isIsSuccess()) {
			throw new RetryableSuiteTalkException(String.format("Error occured while getting unit from external system. externalId = %s ", externalId) , response);			
		}

		return convertToUnitVO((CustomRecord) response.getRecord());
	}

	private UnitVO convertToUnitVO(CustomRecord customRecord)  throws Exception {
		Long fmsId = customRecord.getExternalId() == null ? null : Long.parseLong(customRecord.getExternalId());
		String unitNo = customRecord.getName();
		String vin = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.VIN) == null ? null : super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.VIN);
		String year = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.YEAR);
		String make = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.MAKE);
		String model = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.MODEL);
		String modelTypeDesc = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.MODEL_TYPE);
		String fuelType = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.FUEL_TYPE);
		Long gvr = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.GVR) == null ? null : Long.parseLong(super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.GVR));
		BigDecimal horsePower = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.HORSE_POWER) == null ? null : new BigDecimal(super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.HORSE_POWER));
		BigDecimal msrp = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.MSRP) == null ? null : new BigDecimal(super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.MSRP));
		String newUsed = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.NEW_USED);
		BigDecimal cbv = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.CONTRACT_BOOK_VALUE) == null ? null : new BigDecimal(super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.CONTRACT_BOOK_VALUE));
		String equipClassification = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.EQUIPMENT_CLASSFICATION);
		String plbType = super.getCustomFieldValue(customRecord.getCustomFieldList(), UnitFieldEnum.PLB_TYPE);

		return new UnitVO()
		        .setFmsId(fmsId)
				.setUnitNo(unitNo)
				.setVin(vin)
				.setYear(year)
				.setMake(make)
				.setModel(model)
				.setModelTypeDesc(modelTypeDesc)
				.setFuelType(fuelType)
				.setGvr(gvr)
				.setHorsePower(horsePower)
				.setMsrp(msrp)
				.setNewUsed(newUsed)
				.setCbv(cbv)
				.setVehicleClassification(equipClassification)
				.setPlbType(plbType);
	}
}

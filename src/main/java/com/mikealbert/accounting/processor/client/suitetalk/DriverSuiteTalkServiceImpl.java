package com.mikealbert.accounting.processor.client.suitetalk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.DriverUnitHistoryEnum;
import com.mikealbert.accounting.processor.enumeration.FieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.SavedSearchEnum;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchRowBasic;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomRecordRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnStringCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchRow;
import com.netsuite.webservices.platform.core_2023_2.SearchStringField;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.GetCustomizationType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchDateFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchStringFieldOperator;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecord;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearch;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearchAdvanced;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearchRow;

@Service("driverSuiteTalkService")
public class DriverSuiteTalkServiceImpl extends BaseSuiteTalkService implements DriverSuiteTalkService {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	
	static final int NUMBER_OF_RETRIES = 3;
	static final long DELAY_MILLESECONDS = 10000;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	public void upsertDriverUnitHistory(DriverUnitHistoryUpsertVO drvUnitHistoryVO) throws Exception {

		LOG.info("In upsertDriverUnitHistory : " + drvUnitHistoryVO);

		// Update ExternalId in NS
		WriteResponse response = service.getService().upsert(createCustomRecordDriverUnitHistory(drvUnitHistoryVO));

		if (!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error Upsert Driver Unit History DrvId: %s UnitNo: %s",
					drvUnitHistoryVO.getDrvId(), drvUnitHistoryVO.getUnitNo()), response);
		}

		LOG.info(String.format("Driver Unit History record upserted drvId: %s unitNo: %s ", drvUnitHistoryVO.getDrvId(),
				drvUnitHistoryVO.getUnitNo()));
	}

	private CustomRecord createCustomRecordDriverUnitHistory(DriverUnitHistoryUpsertVO drvUnitHistoryVO)
			throws Exception {
		CustomRecord customRecord = new CustomRecord();
		List<Object> driverUnitHistoryCustomFields = new ArrayList<>();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");

		LongCustomFieldRef drvId = new LongCustomFieldRef();
		drvId.setScriptId(DriverUnitHistoryEnum.DRV_ID.getScriptId());
		drvId.setValue(drvUnitHistoryVO.getDrvId());
		driverUnitHistoryCustomFields.add(drvId);

		if (drvUnitHistoryVO.getUnitNo() != null) {
			try {
					SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
					unitNo.setScriptId(DriverUnitHistoryEnum.UNIT_NO.getScriptId());
					unitNo.setValue(
							new ListOrRecordRef(null,
									suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(),
											drvUnitHistoryVO.getUnitNo()),
									null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
					driverUnitHistoryCustomFields.add(unitNo);
			} catch (Exception e) {
				throw new RetryableSuiteTalkException(String.format(
						"Error occurred in driverUnitHistory while searching for unit no in the external system %s",
						drvUnitHistoryVO.toString(), e));
			}
		}
		

		if (drvUnitHistoryVO.getAccountCode() != null) {
			StringCustomFieldRef accouuntCode = new StringCustomFieldRef();
			accouuntCode.setScriptId(DriverUnitHistoryEnum.CLIENT.getScriptId());
			accouuntCode.setValue(drvUnitHistoryVO.getAccountCode());
			driverUnitHistoryCustomFields.add(accouuntCode);
		}

		DateCustomFieldRef effectiveDate = new DateCustomFieldRef();
		effectiveDate.setScriptId(DriverUnitHistoryEnum.EFFECTIVE_FROM.getScriptId());
		effectiveDate.setValue(DateUtil.convertToCalendar(drvUnitHistoryVO.getEffectiveDate()));
		driverUnitHistoryCustomFields.add(effectiveDate);

		StringCustomFieldRef firstName = new StringCustomFieldRef();
		firstName.setScriptId(DriverUnitHistoryEnum.DRV_FIRST_NAME.getScriptId());
		firstName.setValue(drvUnitHistoryVO.getFirstName());
		driverUnitHistoryCustomFields.add(firstName);

		StringCustomFieldRef lastName = new StringCustomFieldRef();
		lastName.setScriptId(DriverUnitHistoryEnum.DRV_LAST_NAME.getScriptId());
		lastName.setValue(drvUnitHistoryVO.getLastName());
		driverUnitHistoryCustomFields.add(lastName);

		StringCustomFieldRef drvAddLine1 = new StringCustomFieldRef();
		drvAddLine1.setScriptId(DriverUnitHistoryEnum.DRV_ADD_LINE_1.getScriptId());
		drvAddLine1.setValue(drvUnitHistoryVO.getDriverAddress().getAddressLine1());
		driverUnitHistoryCustomFields.add(drvAddLine1);

		if (drvUnitHistoryVO.getDriverAddress().getAddressLine2() != null) {
			StringCustomFieldRef drvAddLine2 = new StringCustomFieldRef();
			drvAddLine2.setScriptId(DriverUnitHistoryEnum.DRV_ADD_LINE_2.getScriptId());
			drvAddLine2.setValue(drvUnitHistoryVO.getDriverAddress().getAddressLine2());
			driverUnitHistoryCustomFields.add(drvAddLine2);
		}

		StringCustomFieldRef drvAddCity = new StringCustomFieldRef();
		drvAddCity.setScriptId(DriverUnitHistoryEnum.DRV_ADD_CITY.getScriptId());
		drvAddCity.setValue(drvUnitHistoryVO.getDriverAddress().getTownDescription());
		driverUnitHistoryCustomFields.add(drvAddCity);

		StringCustomFieldRef drvAddState = new StringCustomFieldRef();
		drvAddState.setScriptId(DriverUnitHistoryEnum.DRV_ADD_STATE.getScriptId());
		drvAddState.setValue(drvUnitHistoryVO.getDriverAddress().getRegionCode());
		driverUnitHistoryCustomFields.add(drvAddState);

		StringCustomFieldRef drvAddZip = new StringCustomFieldRef();
		drvAddZip.setScriptId(DriverUnitHistoryEnum.DRV_ADD_ZIP_CODE.getScriptId());
		drvAddZip.setValue(drvUnitHistoryVO.getDriverAddress().getZipCode());
		driverUnitHistoryCustomFields.add(drvAddZip);

		if (drvUnitHistoryVO.getDriverAddress().getCountyCode() != null) {
			StringCustomFieldRef drvAddCounty = new StringCustomFieldRef();
			drvAddCounty.setScriptId(DriverUnitHistoryEnum.DRV_ADD_COUNTY.getScriptId());
			drvAddCounty.setValue(drvUnitHistoryVO.getDriverAddress().getCountyCode());
			driverUnitHistoryCustomFields.add(drvAddCounty);
		}

		StringCustomFieldRef drvAddCountry = new StringCustomFieldRef();
		drvAddCountry.setScriptId(DriverUnitHistoryEnum.DRV_ADD_COUNTRY.getScriptId());
		drvAddCountry.setValue(drvUnitHistoryVO.getDriverAddress().getCountryCode());
		driverUnitHistoryCustomFields.add(drvAddCountry);

		if (drvUnitHistoryVO.getCostCenter() != null) {
			StringCustomFieldRef costCenter = new StringCustomFieldRef();
			costCenter.setScriptId(DriverUnitHistoryEnum.DRV_COST_CENTER.getScriptId());
			costCenter.setValue(drvUnitHistoryVO.getCostCenter());
			driverUnitHistoryCustomFields.add(costCenter);
		}

		if (drvUnitHistoryVO.getCostCenterDesc() != null) {
			StringCustomFieldRef costCenterDesc = new StringCustomFieldRef();
			costCenterDesc.setScriptId(DriverUnitHistoryEnum.DRV_COST_CENTER_DESC.getScriptId());
			costCenterDesc.setValue(drvUnitHistoryVO.getCostCenterDesc());
			driverUnitHistoryCustomFields.add(costCenterDesc);
		}

		if(drvUnitHistoryVO.getSupplierAddress().getAddressLine1() != null) {
			StringCustomFieldRef ddAddLine1 = new StringCustomFieldRef();
			ddAddLine1.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_LINE_1.getScriptId());
			ddAddLine1.setValue(drvUnitHistoryVO.getSupplierAddress().getAddressLine1());
			driverUnitHistoryCustomFields.add(ddAddLine1);
		}

		if (drvUnitHistoryVO.getSupplierAddress().getAddressLine2() != null) {
			StringCustomFieldRef ddAddLine2 = new StringCustomFieldRef();
			ddAddLine2.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_LINE_2.getScriptId());
			ddAddLine2.setValue(drvUnitHistoryVO.getSupplierAddress().getAddressLine2());
			driverUnitHistoryCustomFields.add(ddAddLine2);
		}

		if(drvUnitHistoryVO.getSupplierAddress().getTownDescription() != null) {
			StringCustomFieldRef ddAddCity = new StringCustomFieldRef();
			ddAddCity.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_CITY.getScriptId());
			ddAddCity.setValue(drvUnitHistoryVO.getSupplierAddress().getTownDescription());
			driverUnitHistoryCustomFields.add(ddAddCity);
		}
		
		if(drvUnitHistoryVO.getSupplierAddress().getRegionCode() != null) {
			StringCustomFieldRef ddAddState = new StringCustomFieldRef();
			ddAddState.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_STATE.getScriptId());
			ddAddState.setValue(drvUnitHistoryVO.getSupplierAddress().getRegionCode());
			driverUnitHistoryCustomFields.add(ddAddState);
		}
		
		if(drvUnitHistoryVO.getSupplierAddress().getZipCode() != null) {
			StringCustomFieldRef ddAddZip = new StringCustomFieldRef();
			ddAddZip.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_ZIP_CODE.getScriptId());
			ddAddZip.setValue(drvUnitHistoryVO.getSupplierAddress().getZipCode());
			driverUnitHistoryCustomFields.add(ddAddZip);
		}

		if (drvUnitHistoryVO.getSupplierAddress().getCountyCode() != null) {
			StringCustomFieldRef ddAddCounty = new StringCustomFieldRef();
			ddAddCounty.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_COUNTY.getScriptId());
			ddAddCounty.setValue(drvUnitHistoryVO.getSupplierAddress().getCountyCode());
			driverUnitHistoryCustomFields.add(ddAddCounty);
		}

		if(drvUnitHistoryVO.getSupplierAddress().getCountryCode() != null) {
			StringCustomFieldRef ddAddCountry = new StringCustomFieldRef();
			ddAddCountry.setScriptId(DriverUnitHistoryEnum.DEL_DEALER_ADD_COUNTRY.getScriptId());
			ddAddCountry.setValue(drvUnitHistoryVO.getSupplierAddress().getCountryCode());
			driverUnitHistoryCustomFields.add(ddAddCountry);
		}
		
		if(drvUnitHistoryVO.getCustRecordDuhDriverRechargeCode() != null) {
			StringCustomFieldRef ddRechargeCode = new StringCustomFieldRef();
			ddRechargeCode.setScriptId(DriverUnitHistoryEnum.DRV_RECHARGE_CODE.getScriptId());
			ddRechargeCode.setValue(drvUnitHistoryVO.getCustRecordDuhDriverRechargeCode());
			driverUnitHistoryCustomFields.add(ddRechargeCode);
		}
		
		if(drvUnitHistoryVO.getCustRecordDuhFleetRefNo() != null) {
			StringCustomFieldRef ddFleetNo = new StringCustomFieldRef();
			ddFleetNo.setScriptId(DriverUnitHistoryEnum.UNIT_FLEET_REF_NO.getScriptId());
			ddFleetNo.setValue(drvUnitHistoryVO.getCustRecordDuhFleetRefNo());
			driverUnitHistoryCustomFields.add(ddFleetNo);
		}
	
		CustomFieldList assetFieldList = new CustomFieldList(
				driverUnitHistoryCustomFields.toArray(CustomFieldRef[]::new));

		customRecord.setExternalId(String.format("%s%s%s", drvUnitHistoryVO.getDrvId(), drvUnitHistoryVO.getUnitNo(),
				dateFormat.format(drvUnitHistoryVO.getEffectiveDate())));
		customRecord
				.setRecType(new RecordRef(null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.DRIVER_UNIT_HISTORY.getScriptId()), null, null));
		customRecord.setCustomFieldList(assetFieldList);

		return customRecord;
	}
	
	@Override
	public void deleteDriverUnitHistory(DriverUnitHistoryUpsertVO drvUnitHistoryVO) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		String customRecordTypeId = suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.DRIVER_UNIT_HISTORY.getScriptId());		
		String customRecordInternalId = service.getCustomRecordInternalId(CustomRecordEnum.DRIVER_UNIT_HISTORY.getScriptId(), String.format("%s%s%s", drvUnitHistoryVO.getDrvId(), drvUnitHistoryVO.getUnitNo(),
				dateFormat.format(drvUnitHistoryVO.getEffectiveDate())));
		
		CustomRecordRef customRecordRef = new CustomRecordRef(null, customRecordInternalId, null, customRecordTypeId, null);	
		
		WriteResponse response = service.getService().delete(customRecordRef, null);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error occured while deleting DriverUnitHistory", response);			
		}
	}

	@Override
	public DriverUnitHistoryVO readDuhByUnitInternalIdAndDate(String unitInternalId, Date effectiveDate) throws Exception {
		return convertToDriverUnitHistoryVO(
			super.service.searchAdvancedX(
				convertToDuhCustomRecordSearchAdvanced(unitInternalId, effectiveDate) ) );
	}

	@Override
	public List<DriverUnitHistoryVO> readAllDuhs() throws Exception {
		return convertToDriverUnitHistoryVOs(
			super.service.searchAdvancedX(
				convertToDuhCustomRecordSearchAdvanced("*", null) ) );
	}
	
	private CustomRecordSearchAdvanced convertToDuhCustomRecordSearchAdvanced(String unitInternalId,  Date effectiveDate) throws Exception {
		List<SearchCustomField> searchCustomFields = new ArrayList<>(0);

		CustomRecordSearchAdvanced crsa = new CustomRecordSearchAdvanced();
		crsa.setSavedSearchScriptId(SavedSearchEnum.MA_DRIVER_UNIT_HISTORY.getId());

		CustomRecordSearch cs = new CustomRecordSearch();

		CustomRecordSearchBasic crsb = new CustomRecordSearchBasic();
		crsb.setRecType(new RecordRef(null, suiteTalkCacheService.searchForCustomXId(CustomRecordEnum.DRIVER_UNIT_HISTORY.getScriptId(), GetCustomizationType.customRecordType), null, null));
		crsb.setExternalIdString(new SearchStringField(null,SearchStringFieldOperator.notEmpty)); 
			
		if(!unitInternalId.equals("*")) {
			SearchMultiSelectCustomField unitNoField = new SearchMultiSelectCustomField(null, DriverUnitHistoryEnum.UNIT_NO.getScriptId(), SearchMultiSelectFieldOperator.anyOf, new ListOrRecordRef[]{new ListOrRecordRef(null, unitInternalId, null, null)});
			searchCustomFields.add(unitNoField);
		}

		if(effectiveDate != null) {
			SearchDateCustomField effectiveDateField = new SearchDateCustomField(null, DriverUnitHistoryEnum.EFFECTIVE_FROM.getScriptId(), SearchDateFieldOperator.onOrBefore, null, DateUtil.convertToCalendar(effectiveDate), null);
			searchCustomFields.add(effectiveDateField);
		}

		crsb.setCustomFieldList(new SearchCustomFieldList( searchCustomFields.toArray(SearchCustomField[]::new)));

		cs.setBasic(crsb);
		crsa.setCriteria(cs);

		return crsa;
	}	

	private DriverUnitHistoryVO convertToDriverUnitHistoryVO(List<SearchRow> searchResult) {
		DriverUnitHistoryVO duh =  null;

		CustomRecordSearchRow effSearchRow = searchResult.stream()
		    .map(r -> (CustomRecordSearchRow)r)    
			.max(Comparator.comparing(r -> parseDateField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.EFFECTIVE_FROM)))
			.orElse(null);

		if(effSearchRow != null ) {
		 	duh = new DriverUnitHistoryVO()
			    .setInternalId(effSearchRow.getBasic().getId()[0].getSearchValue().toString())
				.setExternalId(effSearchRow.getBasic().getExternalId()[0].getSearchValue().getExternalId())
				.setAccountCode(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.CLIENT))
				.setUnitNo(parseUnitNo(effSearchRow))
				.setDriverId(Long.valueOf(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_ID)))
				.setDriverFirstName(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_FIRST_NAME))
				.setDriverLastName(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_LAST_NAME))
				.setCostCenterCode(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_COST_CENTER))
				.setCostCenterDescription(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_COST_CENTER_DESC))
				.setDriverRechargeCode(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_RECHARGE_CODE))
				.setDriverFleetNo(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.UNIT_FLEET_REF_NO))
				.setEffectiveDate(parseDateField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.EFFECTIVE_FROM))
				.setDriverAddressState(parseStringField(effSearchRow.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_ADD_STATE));
		}

		return duh;
	}

	private List<DriverUnitHistoryVO> convertToDriverUnitHistoryVOs(List<SearchRow> searchResult) {
		return searchResult.stream()
		    .map(r -> (CustomRecordSearchRow)r)    
			.map(r -> {
				return new DriverUnitHistoryVO()
				    .setInternalId(r.getBasic().getId()[0].getSearchValue().toString())
				    .setExternalId(r.getBasic().getExternalId()[0].getSearchValue().getExternalId())
					.setAccountCode(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.CLIENT))					
					.setUnitNo(parseUnitNo(r))
				    .setDriverId(Long.valueOf(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_ID)))
				    .setDriverFirstName(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_FIRST_NAME))
				    .setDriverLastName(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_LAST_NAME))
				    .setCostCenterCode(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_COST_CENTER))
				    .setCostCenterDescription(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_COST_CENTER_DESC))
					.setDriverRechargeCode(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.DRV_RECHARGE_CODE))
					.setDriverFleetNo(parseStringField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.UNIT_FLEET_REF_NO))
				    .setEffectiveDate(parseDateField(r.getBasic().getCustomFieldList(), DriverUnitHistoryEnum.EFFECTIVE_FROM));
			})
			.collect(Collectors.toList());
	}

	private Date parseDateField(SearchColumnCustomFieldList fieldList, FieldEnum fieldEnum) {
		return Arrays.asList(fieldList.getCustomField()).stream()
		    .filter(r -> (r instanceof SearchColumnDateCustomField))
			.map(r -> (SearchColumnDateCustomField) r)
			.filter(r -> r.getScriptId().equalsIgnoreCase(fieldEnum.getScriptId()))
			.map(r -> r.getSearchValue().getTime())
			.findFirst()
			.orElse(null);
	}

	private String parseStringField(SearchColumnCustomFieldList fieldList, FieldEnum fieldEnum) {
		return Arrays.asList(fieldList.getCustomField()).stream()
		    .filter(r -> (r instanceof SearchColumnStringCustomField))
			.map(r -> (SearchColumnStringCustomField) r)
			.filter(r -> r.getScriptId().equalsIgnoreCase(fieldEnum.getScriptId()))
			.map(r -> r.getSearchValue())
			.findFirst()
			.orElse(null);
	}
	
	//TODO Must evaluate the script id of the CustomizationRef to make sure you have the right custom search join
	private String parseUnitNo(CustomRecordSearchRow csr) {
		String unitNo = null;		

		if(csr.getCustomSearchJoin() == null) return unitNo;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(csr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals("custrecordma_duh_unit_no"))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {
			unitNo = crsrb.getName()[0].getSearchValue();
		}
			
		return unitNo;
	}	

}
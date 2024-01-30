package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.LeaseFieldEnum;
import com.mikealbert.accounting.processor.enumeration.LeaseModificationFieldEnum;
import com.mikealbert.accounting.processor.enumeration.LeaseScheduleFieldEnum;
import com.mikealbert.accounting.processor.exception.NoDataFoundException;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.RecordTypeEnum;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomRecordRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.Record;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchResult;
import com.netsuite.webservices.platform.core_2023_2.SearchStringField;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.Status;
import com.netsuite.webservices.platform.core_2023_2.StatusDetail;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchStringFieldOperator;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecord;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearch;

@Service("leaseSuiteTalkService")
public class LeaseSuiteTalkServiceImpl extends BaseSuiteTalkService implements LeaseSuiteTalkService {
    @Resource SuiteTalkCacheService suiteTalkCacheService;
    
	static final int NUMBER_OF_RETRIES = 3;
	static final long DELAY_MILLESECONDS = 10000;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	public String upsertLease(LeaseVO lease) throws Exception {
		String leaseId = null;
		
		LOG.info("In putLease : " + lease);
		
		WriteResponse response = service.getService().upsert(convertLeaseToConstomRecord(lease));	
		if(response.getStatus().isIsSuccess()) {
			Map<String, String> leaseMap = getLease(lease.getExternalId(), NUMBER_OF_RETRIES);
			leaseId = leaseMap.get(LeaseFieldEnum.INTERNAL_ID.name());
			
			int i = 0;			
			for(LeaseAccountingScheduleVO leaseSchedule : lease.getLeaseAccountingSchedule()) {	
				service.getService().upsert(convertLeaseScheduleToConstomRecord(leaseId, String.format("%s-%d",lease.getExternalId(), i+=1), leaseSchedule));
			}
		} else {
			throw new SuiteTalkException(String.format("Error creating lease: %s", lease.toString()), response);
		}
		
		LOG.info("Successfully upserted lease in the accounting system : " + lease);		
		
		return  leaseId;
	}

	public String amendLease(LeaseVO lease) throws Exception {
		String leaseId = null;

		LOG.info("In amendLease : " + lease);
		
		WriteResponse response = service.getService().upsert(convertLeaseToConstomRecord(lease));	
		if(response.getStatus().isIsSuccess()) {
			leaseId = ((CustomRecordRef)response.getBaseRef()).getInternalId();
			int i = 0;
			
			for(LeaseAccountingScheduleVO leaseSchedule : lease.getLeaseAccountingSchedule()) {	
				service.getService().upsert(convertLeaseScheduleToConstomRecord(leaseId, String.format("%s-%d",lease.getExternalId(), i+=1), leaseSchedule));
			}
		} else {
			throw new SuiteTalkException(String.format("Error creating lease in external system: %s ", lease.toString()), response);
		}				

		LOG.info("Successfully amended lease in the accounting system : " + lease);
		
		return  leaseId;
	}

	/**
	 * Terminates existing lease(s), based on quoid, by updating a flag on the lease
	 * 
	 * @param String quoId 
	 * @return List<Map<String, String>> containing the lease(s) that were terminated
	 * @exception any thrown exception
	 */
	public List<Map<String, String>> terminateLease(String quoId) throws Exception {
		List<Map<String, String>> terminatedLeases = new ArrayList<>(0);

		LOG.info("In terminateLease : quoId = " + quoId);
		
		List<Map<String, String>> leaseMaps = service.searchForCustomRecordsByScriptIdAndExternalId(CustomRecordEnum.LEASE.getScriptId(), quoId);
		if(leaseMaps.isEmpty()) {
			throw new SuiteTalkException(String.format("Lease does not exist in external system for quoId: %s", quoId), new WriteResponse(new Status(new StatusDetail[] {new StatusDetail(null, "Call from mars", true, null)}, false), null));
		}
		
		for(Map<String, String> leaseMap : leaseMaps) {
			WriteResponse response = service.getService().update(convertTerminatedLeaseToConstomRecord(leaseMap));
			if(response.getStatus().isIsSuccess()) {
				CustomRecordRef recordRef = (CustomRecordRef)response.getBaseRef();
				
				Map<String, String> terminatedLease = new HashMap<>();
				terminatedLease.put(LeaseFieldEnum.INTERNAL_ID.name(), recordRef.getInternalId());
				terminatedLease.put(LeaseFieldEnum.EXTERNAL_ID.name(), recordRef.getExternalId());
				terminatedLease.put(LeaseFieldEnum.NAME.name(), recordRef.getName());
				
				terminatedLeases.add(terminatedLease);
				
			} else {
				throw new SuiteTalkException(String.format("Error terminating lease in external system: %s ", quoId), response);			
			}
			
			LOG.info("Terminated lease in accounting system successfully " + leaseMap.toString());			
		}
				
		return terminatedLeases;
	}
	
	/**
	 * Creates a lease modification record in the external system 
	 * that will represent the revision made to the lease. New payment
	 * schedules will be created whey they exist on the passed in lease.
	 * 
	 * @param LeaseVO revised lease including new payment schedule(s)
	 * @return String the external system's internalId of the modified lease
	 * @exception any thrown exception 
	 */
	public String modifyLease(LeaseVO lease) throws Exception {
		String leaseId = null;
		String leaseModId = null;
		WriteResponse response = null;
		
		LOG.info("In modifyLease : " + lease);	

		response = service.getService().add(convertLeaseModificationToConstomRecord(lease));	
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error modifying lease in the external system: %s ", lease.toString()), response);
		} else {						
			Map<String, String> leaseMap = getLease(lease.getExternalId(), NUMBER_OF_RETRIES);			
			leaseId = leaseMap.get(LeaseFieldEnum.INTERNAL_ID.name());
			
			deleteCurrentAndFutruePaymentSchedules(lease);
			
			for(LeaseAccountingScheduleVO leaseSchedule : lease.getLeaseAccountingSchedule()) {	
				service.getService().add(convertLeaseScheduleToConstomRecord(leaseId, String.format("%s-%d", lease.getExternalId(), System.currentTimeMillis()), leaseSchedule));
			}
					
			CustomRecordRef leaseModRef = (CustomRecordRef) response.getBaseRef();
			leaseModId = leaseModRef.getInternalId();
			
			WriteResponse updateLeaseResponse = service.getService().update(convertUpdatedLeaseToConstomRecord(lease, leaseMap, leaseModId));			
			if(!updateLeaseResponse.getStatus().isIsSuccess()) {
				throw new SuiteTalkException(String.format("Error updating lease with modification reference. %s ", lease.toString()), response);
			}
		}
		
		LOG.info("Modified lease in accounting system successfully " + lease);
		
		return  leaseId;		
	}	
	
	/**
	 * At the time of writing, only updates the client on the lease.
	 * @param LeaseVO Lease containing the new client 
     * @exception any thrown exception 
	 */
	@Override
	public void novateLease(LeaseVO lease) throws Exception {
		CustomRecord customRecord = convertLeaseToConstomRecordForUpdateClient(lease);
		
		WriteResponse response = service.getService().update(customRecord);	
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error novating lease: %s", lease), response);
		}
	}	
	
	/**
	 * Update interest rate on lease
	 * @param LeaseVO Lease containing the new interest rate
     * @exception any thrown exception 
	 */
	@Override
	public void updateInterestRate(LeaseVO lease) throws Exception {
		CustomRecord customRecord = convertLeaseToConstomRecordForUpdateInterestRate(lease);
		
		WriteResponse response = service.getService().update(customRecord);	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode().equals(StatusDetailCodeType.USER_ERROR)) {
				throw new RetryableSuiteTalkException(String.format("Error updating interest rate on lease: %s", lease), response);	
			} else {
				throw new SuiteTalkException(String.format("Error updating interest rate on lease: %s", lease), response);
			}
		}
	}	

	/**
	 * Update actual end date on lease
	 * @param LeaseVO Lease containing the updated actual end date
     * @exception any thrown exception 
	 */
	@Override
	public void updateActualEndDate(LeaseVO lease) throws Exception {
		CustomRecord customRecord = convertLeaseToConstomRecordForUpdateActualEndDate(lease);
		
		WriteResponse response = service.getService().update(customRecord);	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode().equals(StatusDetailCodeType.USER_ERROR)) {
				throw new RetryableSuiteTalkException(String.format("Error updating actual end date on lease: %s", lease), response);	
			} else {
				throw new SuiteTalkException(String.format("Error updating updating actual end date on lease: %s", lease), response);
			}
		}
	}
	
	/**
	 * Retrieves the lease in the form of a map, key/value pair. 
	 * @param String External Id of the lease
	 * @param int The number of times to retry when an exception occurs
	 * @return Map<String, String> Key/value pair representing the lease
	 */
	public Map<String, String> getLease(String externalId, int numberOfRetries) throws Exception {
		Map<String, String> retVal = new HashMap<>();
		
		try {
			CustomRecordRef recRef = new CustomRecordRef("", "", externalId, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.LEASE.getScriptId()), "");
			ReadResponse response = service.getService().get(recRef);
			if(response.getStatus().isIsSuccess()) {
				CustomRecord customRecord = (CustomRecord) response.getRecord();

				retVal.put(LeaseFieldEnum.INTERNAL_ID.name(), customRecord.getInternalId());
				retVal.put(LeaseFieldEnum.EXTERNAL_ID.name(),  customRecord.getExternalId());
				retVal.put(LeaseFieldEnum.NAME.name(),  customRecord.getName());
				retVal.put(LeaseFieldEnum.ALT_NAME.name(),  customRecord.getAltName());

				for(CustomFieldRef customFieldRef : customRecord.getCustomFieldList().getCustomField()) {
					if(LeaseFieldEnum.isField(customFieldRef.getScriptId())) {
						LeaseFieldEnum leaseField =  LeaseFieldEnum.getField(customFieldRef.getScriptId());
						retVal.put(leaseField.name(), getFieldValue(customFieldRef));					
					}
				}

				System.out.println(retVal.toString());
			} else {
				throw new SuiteTalkException(String.format("No lease found for externalId %s", externalId), response);
			}
		} catch(Exception e) {
			if(numberOfRetries == 0) {
				throw e;
			}
			
			Thread.sleep(1000);
			retVal = getLease(externalId, --numberOfRetries);
		}
		return retVal;
	}

	/**
	 * Fetch lease(s) based on full or partial externalId
	 * 
	 * @param externalId Full or partial external id. For partial, a '-' character will be appended to the string if it does not already exist
	 */
	public List<LeaseVO> getLease(String externalId) throws Exception {
		List<LeaseVO> leaseVOs = new ArrayList<>(0);
		LeaseVO leaseVO  = null;

		if(!externalId.contains("-")) {
			externalId = externalId.concat("-");
		}

		CustomRecordSearchBasic crsb = new CustomRecordSearchBasic();		
		crsb.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		crsb.setExternalIdString(new SearchStringField(externalId, SearchStringFieldOperator.startsWith));

		CustomRecordSearch crs = new CustomRecordSearch();
		crs.setBasic(crsb);		

		SearchResult response = service.getService().search(crs);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Could not retrieve lease %s ", externalId), response);
		}

		if(response.getRecordList().getRecord() != null) {
			for(Record rec : response.getRecordList().getRecord()) {
				CustomRecord record = (CustomRecord)rec;					
	
				leaseVO = new LeaseVO();
				leaseVO.setInternalId(record.getInternalId());
				leaseVO.setExternalId(record.getExternalId());
				leaseVO.setName(record.getAltName());
	
				for(CustomFieldRef customFieldRef : record.getCustomFieldList().getCustomField()) {
					if(LeaseFieldEnum.INTERNAL_ID.getScriptId().equals(customFieldRef.getScriptId())) {
						leaseVO.setInternalId(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.LEASE_TYPE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setExternalProductType(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.CLASS_BUSINESS_UNIT.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setClassification(super.getFieldValue(customFieldRef));
					} 			
					if(LeaseFieldEnum.SUBSIDIARY.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setSubsidiary(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.CURRENCY.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setCurrency(super.getFieldValue(customFieldRef));
					}		
					if(LeaseFieldEnum.COMMENCEMENT_DATE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setCommencementDate(DateUtil.convertToDate(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.WILLOW_END_DATE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setEndDate(DateUtil.convertToDate(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.IN_SERVICE_DATE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setInServiceDate(DateUtil.convertToDate(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.LEASE_MODIFICATION_EFFECTIVE_DATE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setEffectiveDate(DateUtil.convertToDate(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.TERM.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setTerm(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.ASSET_FAIR_VALUE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setLeaseAssetFairValue(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.ASSET_CARRING_COST.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setLeaseAssetCostCarrying(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.PREPAYMENT.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setPrePayment(new BigDecimal(super.getFieldValue(customFieldRef)));
					}			
					if(LeaseFieldEnum.VARIABLE_RATE_LEASE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setVariablePayment(Boolean.toString(DataUtil.convertToBoolean(super.getFieldValue(customFieldRef))));
					}				
					if(LeaseFieldEnum.VARIABLE_RATE_INDEX.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setVariableRateIndex(super.getFieldValue(customFieldRef));
					}
					if(CustomSegmentEnum.UNIT_NO.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setUnitNo(super.getFieldValueName(customFieldRef));
					}
					if(LeaseFieldEnum.THIRD_PARTY_RESIDUAL.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setResidualValueGuaranteeBy3rdParty(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.LESEE_RESIDUAL.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setResidualValueGuranteeByLesee(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.TOTAL_RESIDUAL.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setResidualValueEstimate(new BigDecimal(super.getFieldValue(customFieldRef)));
					}		
					if(LeaseFieldEnum.COLLECTIBILITY_PROBABLE.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setCollectibilityProbable(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.LEASE_TRANSFER_OWNERSHIP.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setLeaseTransferOwnership(super.getFieldValue(customFieldRef));
					}	
					if(LeaseFieldEnum.PURCHASE_OPTION.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setPurchaseOptionReasonablyCertain(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.UNDERLYING_ASSET.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setUnderlyingAssestSpecialized(super.getFieldValue(customFieldRef));
					}			
					if(LeaseFieldEnum.DEPOSIT_REFUNABLE_AMOUNT.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setDepositAmount(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.CUSTOMER.getScriptId().equals(customFieldRef.getScriptId())){
						leaseVO.setClientInternalId(super.getFieldValue(customFieldRef));
					}
					if(LeaseFieldEnum.CUSTOMER_CAP_COST.getScriptId().equals(customFieldRef.getScriptId())) {
						leaseVO.setClientCapitalCost(new BigDecimal(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.INTEREST_RATE.getScriptId().equals(customFieldRef.getScriptId())) {
						leaseVO.setInterestRate(super.getFieldValue(customFieldRef) == null ? null : Double.valueOf(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.IS_AMENDMENT.getScriptId().equals(customFieldRef.getScriptId())) {
						leaseVO.setAmendment(DataUtil.convertToBoolean(super.getFieldValue(customFieldRef)));
					}
					if(LeaseFieldEnum.ACTUAL_END_DATE.getScriptId().equals(customFieldRef.getScriptId())) {
						leaseVO.setActualEndDate(DateUtil.convertToDate(super.getFieldValue(customFieldRef))); 
					}
				}
	
				leaseVOs.add(leaseVO);
			}
	    }

		return leaseVOs;
	}

	//TODO WS - I am not clear on how to get all the schedules for a given lease.
	@Deprecated
	public Map<String, String> getLeasePayments() throws Exception {
		Map<String, String> retVal = new HashMap<>();
		CustomRecordRef recRef = new CustomRecordRef("", CustomRecordEnum.PAYMENT.getInternalId(), "", suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.PAYMENT.getScriptId()), "");
		ReadResponse readResponse = service.getService().get(recRef);
		if(readResponse.getStatus().isIsSuccess()) {
			CustomRecord customRecord = (CustomRecord) readResponse.getRecord();
			System.out.println("Custom Record Name:" + customRecord.getName());
			System.out.println("Custom Record Alt Name: " + customRecord.getAltName());
			System.out.println("Custom Record Extrnal Id: " + customRecord.getExternalId());
			
			for(CustomFieldRef customFieldRef : customRecord.getCustomFieldList().getCustomField()) {
				
				if(LeaseFieldEnum.isField(customFieldRef.getScriptId())) {
					LeaseFieldEnum leaseField =  LeaseFieldEnum.getField(customFieldRef.getScriptId());
					if(customFieldRef instanceof SelectCustomFieldRef) {
						SelectCustomFieldRef field = (SelectCustomFieldRef) customFieldRef;	
						System.out.println(leaseField.name() + " ----->: " + field.getValue().getName());
					}
					if(customFieldRef instanceof DoubleCustomFieldRef) {
						DoubleCustomFieldRef field = (DoubleCustomFieldRef) customFieldRef;	
						System.out.println(leaseField.name() + " ----->: " + field.getValue());
					}		
					if(customFieldRef instanceof BooleanCustomFieldRef) {
						BooleanCustomFieldRef field = (BooleanCustomFieldRef) customFieldRef;	
						System.out.println(leaseField.name() + " ----->: " + field.isValue());
					}	
					if(customFieldRef instanceof LongCustomFieldRef) {
						LongCustomFieldRef field = (LongCustomFieldRef) customFieldRef;	
						System.out.println(leaseField.name() + " ----->: " + field.getValue());
					} 
					if(customFieldRef instanceof DateCustomFieldRef) {
						DateCustomFieldRef field = (DateCustomFieldRef) customFieldRef;	
						System.out.println(leaseField.name() + " ----->: " + field.getValue());
					}
					
				}
			}
		} else {
			System.out.println("Failed");
		}
				
		return retVal;
	}

	@Override
	public List<LeaseAccountingScheduleVO> getSchedules(LeaseVO leaseVO) throws Exception {
		List<LeaseAccountingScheduleVO> leaseScheduleVOs = new ArrayList<>(0);

		CustomRecordSearchBasic crsb = new CustomRecordSearchBasic();		
		crsb.setRecType(new RecordRef(null, CustomRecordEnum.LEASE_SCHEDULE.getRecordTypeId(), null, null));

		SearchMultiSelectCustomField smf = new SearchMultiSelectCustomField();
		smf.setScriptId(LeaseScheduleFieldEnum.LEASE.getScriptId());
		smf.setOperator(SearchMultiSelectFieldOperator.anyOf);
		smf.setSearchValue(new ListOrRecordRef[]{new ListOrRecordRef(null, leaseVO.getInternalId(), null, null)});

		crsb.setCustomFieldList(new SearchCustomFieldList(new SearchCustomField[]{smf}));

		CustomRecordSearch crs = new CustomRecordSearch();
		crs.setBasic(crsb);

		SearchResult result = service.getService().search(crs);
		if(result.getStatus().isIsSuccess()) {
			if(result.getRecordList().getRecord() != null) {
				for(Record record : result.getRecordList().getRecord()) {
					CustomRecord custRecord = (CustomRecord)record;
				 	LeaseAccountingScheduleVO leaseScheduleVO = new LeaseAccountingScheduleVO();

				 	for(CustomFieldRef customFieldRef : custRecord.getCustomFieldList().getCustomField()) {
						if(LeaseScheduleFieldEnum.LEASE.getScriptId().equals(customFieldRef.getScriptId())) {
							leaseScheduleVO.setLeaseInternalId(super.getFieldValue(customFieldRef));
					 	}
					 	if(LeaseScheduleFieldEnum.PAYMENT.getScriptId().equals(customFieldRef.getScriptId())) {
							leaseScheduleVO.setAmount(new BigDecimal(super.getFieldValueName(customFieldRef)));
					 	}
					 	if(LeaseScheduleFieldEnum.PERIOD_START_DATE.getScriptId().equals(customFieldRef.getScriptId())) {
						 	leaseScheduleVO.setTransDate(DateUtil.convertToDate(super.getFieldValueName(customFieldRef)));
					 	}
				 	}
				 
				 	leaseScheduleVOs.add(leaseScheduleVO);
				}
		 	}
		} else {
			throw new SuiteTalkException(String.format("Could not retrieve lease schedule"), result);
		}

		return leaseScheduleVOs;
	}
	
	// TODO WS - Lease payment schedules should be retrieved from the external system, then deleted. Right now, this is just a HACK for unit test
	@Deprecated
	public void deleteLease(LeaseVO lease) throws Exception {
		Map<String, String> leaseMap = getLease(lease.getExternalId(), 3);
		String leaseInternalId = leaseMap.get(LeaseFieldEnum.INTERNAL_ID.name());
		String leaseExternalId = leaseMap.get(LeaseFieldEnum.EXTERNAL_ID.name());			

		try {
			CustomRecordRef leaseScheduleRecRef = new CustomRecordRef(null, null, String.format("%s-%d",leaseExternalId, 1), suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.PAYMENT.getScriptId()), null);
			WriteResponse leaseScheduleResponse = service.getService().delete(leaseScheduleRecRef, null);		
			if(!leaseScheduleResponse.getStatus().isIsSuccess()) {
				throw new SuiteTalkException(String.format("Error deleting lease schedule in external system: externalId %s ", leaseExternalId), leaseScheduleResponse);					
			}
		} catch(Exception e) {
			LOG.warn("Error when deleting the lease schedule. Odds are that it was already deleted by modify lease, or one did not exist");
		}

		CustomRecordRef leaseRecRef = new CustomRecordRef(null, leaseInternalId, null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.LEASE.getScriptId()), null);
		WriteResponse leaseResponse = service.getService().delete(leaseRecRef, null);

		if(!leaseResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error deleting lease in external system: internal/externalId %s/%s ", leaseInternalId, leaseExternalId), leaseResponse);					
		}
	}
	
	public void deleteCurrentAndFutruePaymentSchedules(LeaseVO lease) throws Exception {		
		List<Map<String, String>> leaseScheduleRefs = service.searchForCustomRecordsByScriptIdAndExternalId(CustomRecordEnum.PAYMENT.getScriptId(), lease.getExternalId());
		leaseScheduleRefs.stream()
		.filter(map -> new Date(Long.parseLong(map.get(LeaseFieldEnum.LEASE_PAYMENT_DATE.getScriptId()))).compareTo(DateUtil.convertLocalDateToDate(LocalDate.now())) >= 0)
		.forEach(map -> {
			try {
				CustomRecordRef leaseScheduleRecRef = new CustomRecordRef(null, map.get(LeaseFieldEnum.INTERNAL_ID.getScriptId()), null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.PAYMENT.getScriptId()), null);
				WriteResponse leaseScheduleResponse = service.getService().delete(leaseScheduleRecRef, null);
				
				if(!leaseScheduleResponse.getStatus().isIsSuccess()) {
					throw new SuiteTalkException("Error deleting lease payment schEdule", leaseScheduleResponse);
				}
			} catch(Exception e) {
				throw new RuntimeException();
			}
		});
		
		System.out.println("test");		
	}
		
	/**
	 * Converts the lease to a custom record with the appropriate field(s) set to terminate the lease
	 * 
	 * @param lease LeaseVO
	 * @return CustomRecord with terminate field(s) initialized
	 * @throws Exception all exceptions are thrown
	 */
	private CustomRecord convertTerminatedLeaseToConstomRecord(Map<String, String> leaseRef) throws Exception{
		CustomRecord customRecord = new CustomRecord();
	    String externalId = leaseRef.get(LeaseFieldEnum.EXTERNAL_ID.getScriptId()); 
	    
		List<Object> leaseCustomFields = new ArrayList<>();	

		BooleanCustomFieldRef termFlag = new BooleanCustomFieldRef();
		termFlag.setScriptId(LeaseFieldEnum.TERMINATION_FLAG.getScriptId());
		termFlag.setValue(true);
		leaseCustomFields.add(termFlag);
				
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord.setExternalId(externalId);
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		customRecord.setCustomFieldList(leaseFieldList);
		
		return customRecord;		
	}
	
	/**
	 * Converts the lease to a custom record with the appropriate field(s) set for the lease update
	 * 
	 * @param leaseRef contains ids for the lease
	 * @param internal id for the lease mod
	 * @return CustomRecord with update lease fields initialized
	 * @throws Exception all exceptions are thrown
	 */
	private CustomRecord convertUpdatedLeaseToConstomRecord(LeaseVO lease, Map<String, String> leaseRef, String leaseModId) throws Exception{
		CustomRecord customRecord = new CustomRecord();
		List<Object> leaseCustomFields = new ArrayList<>();
		String internalId = leaseRef.get(LeaseFieldEnum.INTERNAL_ID.name()); 
	   		
		SelectCustomFieldRef leaseMod = new SelectCustomFieldRef();
		leaseMod.setScriptId(LeaseFieldEnum.LEASE_MODIFICATION.getScriptId());
		leaseMod.setValue(new ListOrRecordRef(null, leaseModId, null, null));
		leaseCustomFields.add(leaseMod);	
		
		DateCustomFieldRef endDate = new DateCustomFieldRef();
		endDate.setScriptId(LeaseFieldEnum.WILLOW_END_DATE.getScriptId());
		endDate.setValue(DateUtil.convertToCalendar(lease.getEndDate()));
		leaseCustomFields.add(endDate);		
				
		SelectCustomFieldRef customer = new SelectCustomFieldRef();		
		customer.setScriptId(LeaseFieldEnum.CUSTOMER.getScriptId());		
		customer.setValue(new ListOrRecordRef(null, lease.getClientInternalId(), null, null)); 			
		leaseCustomFields.add(customer);

		DoubleCustomFieldRef interestRate = new DoubleCustomFieldRef();
		interestRate.setScriptId(LeaseFieldEnum.INTEREST_RATE.getScriptId());
		interestRate.setValue(lease.getInterestRate());
		leaseCustomFields.add(interestRate);

		//CE Leases do not have a depreciation factor
		if(lease.getDepreciationFactor() != null) {
			DoubleCustomFieldRef depreciationRate = new DoubleCustomFieldRef();
			depreciationRate.setScriptId(LeaseFieldEnum.DEPRECIATION_RATE.getScriptId());
			depreciationRate.setValue(lease.getDepreciationFactor());
			leaseCustomFields.add(depreciationRate);			
		}			
		
				
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord.setInternalId(internalId);
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		customRecord.setCustomFieldList(leaseFieldList);
		
		return customRecord;		
	}	
	
	private CustomRecord convertLeaseToConstomRecord(LeaseVO lease) throws Exception{
		CustomRecord customRecord = null;
		
		List<Object> leaseCustomFields = new ArrayList<>();		
		
		if(lease.getParentExternalId() != null && !lease.getParentExternalId().isBlank() ) {
			try {
				SelectCustomFieldRef parentLease = new SelectCustomFieldRef();
				parentLease.setScriptId(LeaseFieldEnum.LEASE_PAYMENT_PARENT.getScriptId());
				parentLease.setValue(new ListOrRecordRef(null, service.getCustomRecordInternalId(CustomRecordEnum.LEASE.getScriptId(), lease.getParentExternalId()), null, LeaseFieldEnum.LEASE_PARENT.getRecordTypeId()));
				leaseCustomFields.add(parentLease);
			} catch (Exception e) {
				throw new RemoteException(String.format("Error occurred while fecthing parent for lease %s. Parent lease %s reference failed. ", lease.getExternalId(), lease.getParentExternalId()), e);
			}
		}
								
		SelectCustomFieldRef leaseType = new SelectCustomFieldRef();		
		leaseType.setScriptId(LeaseFieldEnum.LEASE_TYPE.getScriptId());		
		leaseType.setValue(new ListOrRecordRef(null, lease.getExternalProductType(), null, CustomRecordEnum.LEASE_TYPE.getRecordTypeId()));		
		leaseCustomFields.add(leaseType);

		if(lease.getClientInternalId() != null) {
			SelectCustomFieldRef customer = new SelectCustomFieldRef();		
			customer.setScriptId(LeaseFieldEnum.CUSTOMER.getScriptId());		
			customer.setValue(new ListOrRecordRef(null, lease.getClientInternalId(), null, null)); 			
			leaseCustomFields.add(customer);
		}
					
		SelectCustomFieldRef subsidiary = new SelectCustomFieldRef();
		subsidiary.setScriptId(LeaseFieldEnum.SUBSIDIARY.getScriptId());
		subsidiary.setValue(new ListOrRecordRef(null, lease.getSubsidiary(), null, RecordTypeEnum.SUBSIDIARY.getId()));		
		leaseCustomFields.add(subsidiary);
		
		SelectCustomFieldRef currency = new SelectCustomFieldRef();
		currency.setScriptId(LeaseFieldEnum.CURRENCY.getScriptId());
		currency.setValue(new ListOrRecordRef(null, lease.getCurrency(), null, RecordTypeEnum.CURRENCY.getId()));		
		leaseCustomFields.add(currency);
		
		DateCustomFieldRef commencementDate = new DateCustomFieldRef();
		commencementDate.setScriptId(LeaseFieldEnum.COMMENCEMENT_DATE.getScriptId());
		commencementDate.setValue(DateUtil.convertToCalendar(lease.getCommencementDate()));
		leaseCustomFields.add(commencementDate);
		
		DateCustomFieldRef endDate = new DateCustomFieldRef();
		endDate.setScriptId(LeaseFieldEnum.WILLOW_END_DATE.getScriptId());
		endDate.setValue(DateUtil.convertToCalendar(lease.getEndDate()));
		leaseCustomFields.add(endDate);
		
		if(lease.getInServiceDate() != null) {
			DateCustomFieldRef inServiceDate = new DateCustomFieldRef();
			inServiceDate.setScriptId(LeaseFieldEnum.IN_SERVICE_DATE.getScriptId());
			inServiceDate.setValue(DateUtil.convertToCalendar(lease.getInServiceDate()));
			leaseCustomFields.add(inServiceDate);
		}
		
		LongCustomFieldRef term = new LongCustomFieldRef();
		term.setScriptId(LeaseFieldEnum.TERM.getScriptId());
		term.setValue(lease.getTerm().longValue());
		leaseCustomFields.add(term);
		
		DoubleCustomFieldRef assertFairValue = new DoubleCustomFieldRef();
		assertFairValue.setScriptId(LeaseFieldEnum.ASSET_FAIR_VALUE.getScriptId());
		assertFairValue.setValue(lease.getLeaseAssetFairValue().doubleValue());
		leaseCustomFields.add(assertFairValue);
		
		DoubleCustomFieldRef assetCarryingCost = new DoubleCustomFieldRef();
		assetCarryingCost.setScriptId(LeaseFieldEnum.ASSET_CARRING_COST.getScriptId());
		assetCarryingCost.setValue(lease.getLeaseAssetCostCarrying().doubleValue());
		leaseCustomFields.add(assetCarryingCost);
		
		DoubleCustomFieldRef prepayment = new DoubleCustomFieldRef();
		prepayment.setScriptId(LeaseFieldEnum.PREPAYMENT.getScriptId());
		prepayment.setValue(lease.getPrePayment().doubleValue());
		leaseCustomFields.add(prepayment);
		
		DoubleCustomFieldRef leseeResidual = new DoubleCustomFieldRef();
		leseeResidual.setScriptId(LeaseFieldEnum.LESEE_RESIDUAL.getScriptId());
		leseeResidual.setValue(lease.getResidualValueGuranteeByLesee().doubleValue());		
		leaseCustomFields.add(leseeResidual);
		
		DoubleCustomFieldRef thridPartyResidual = new DoubleCustomFieldRef();
		thridPartyResidual.setScriptId(LeaseFieldEnum.THIRD_PARTY_RESIDUAL.getScriptId());
		thridPartyResidual.setValue(lease.getResidualValueGuaranteeBy3rdParty().doubleValue());
		leaseCustomFields.add(thridPartyResidual);
		
		DoubleCustomFieldRef totalResidual = new DoubleCustomFieldRef();
		totalResidual.setScriptId(LeaseFieldEnum.TOTAL_RESIDUAL.getScriptId());
		totalResidual.setValue(lease.getResidualValueEstimate().doubleValue());
		leaseCustomFields.add(totalResidual);
		
		BooleanCustomFieldRef variableRateLease = new BooleanCustomFieldRef();
		variableRateLease.setScriptId(LeaseFieldEnum.VARIABLE_RATE_LEASE.getScriptId());
		variableRateLease.setValue(DataUtil.convertToBoolean(lease.getVariablePayment()));
		leaseCustomFields.add(variableRateLease);
		
		if(DataUtil.convertToBoolean(lease.getVariablePayment())) { 
			SelectCustomFieldRef variableRateIndex = new SelectCustomFieldRef();
			variableRateIndex.setScriptId(LeaseFieldEnum.VARIABLE_RATE_INDEX.getScriptId());		
			variableRateIndex.setValue(new ListOrRecordRef(null, lease.getVariableRateIndex(), null, null));
			leaseCustomFields.add(variableRateIndex);
		}
		
		DoubleCustomFieldRef depositRefundableAmount = new DoubleCustomFieldRef();
		depositRefundableAmount.setScriptId(LeaseFieldEnum.DEPOSIT_REFUNABLE_AMOUNT.getScriptId());
		depositRefundableAmount.setValue(lease.getDepositAmount().longValue());
		leaseCustomFields.add(depositRefundableAmount);
		
		BooleanCustomFieldRef collectibilityProbable = new BooleanCustomFieldRef();
		collectibilityProbable.setScriptId(LeaseFieldEnum.COLLECTIBILITY_PROBABLE.getScriptId());
		collectibilityProbable.setValue(DataUtil.convertToBoolean(lease.getCollectibilityProbable()));		
		leaseCustomFields.add(collectibilityProbable);
		
		BooleanCustomFieldRef leaseTransferOwnership = new BooleanCustomFieldRef();
		leaseTransferOwnership.setScriptId(LeaseFieldEnum.LEASE_TRANSFER_OWNERSHIP.getScriptId());
		leaseTransferOwnership.setValue(DataUtil.convertToBoolean(lease.getLeaseTransferOwnership()));
		leaseCustomFields.add(leaseTransferOwnership);
		
		BooleanCustomFieldRef purchaseOption = new BooleanCustomFieldRef();
		purchaseOption.setScriptId(LeaseFieldEnum.PURCHASE_OPTION.getScriptId());
		purchaseOption.setValue(DataUtil.convertToBoolean(lease.getPurchaseOptionReasonablyCertain()));
		leaseCustomFields.add(purchaseOption);
		
		BooleanCustomFieldRef underlyingAsset = new BooleanCustomFieldRef();
		underlyingAsset.setScriptId(LeaseFieldEnum.UNDERLYING_ASSET.getScriptId());
		underlyingAsset.setValue(DataUtil.convertToBoolean(lease.getUnderlyingAssestSpecialized()));
		leaseCustomFields.add(underlyingAsset);
		
		SelectCustomFieldRef classBusinessUnit = new SelectCustomFieldRef();
		classBusinessUnit.setScriptId(LeaseFieldEnum.CLASS_BUSINESS_UNIT.getScriptId());		
		classBusinessUnit.setValue(new ListOrRecordRef(null, LeaseFieldEnum.CLASS_BUSINESS_UNIT.getInternalId(), null, RecordTypeEnum.CLASSIFICATION.getId()));		
		leaseCustomFields.add(classBusinessUnit);
		
//		SelectCustomFieldRef category = new SelectCustomFieldRef();
//		category.setScriptId(LeaseFieldEnum.CATEGORY.getScriptId());		
//		category.setValue(new ListOrRecordRef(null, LeaseFieldEnum.CATEGORY.getInternalId(), null, LeaseFieldEnum.CATEGORY.getRecordTypeId()));	 //TODO Id changed in SB1	
//		leaseCustomFields.add(category);

		BooleanCustomFieldRef autoRenewal = new BooleanCustomFieldRef();
		autoRenewal.setScriptId(LeaseFieldEnum.AUTO_RENEWAL.getScriptId());
		autoRenewal.setValue(lease.isAutoRenewal());
		leaseCustomFields.add(autoRenewal);

		LongCustomFieldRef autoRenewalTerm = new LongCustomFieldRef();
		autoRenewalTerm.setScriptId(LeaseFieldEnum.AUTO_RENEWAL_TERM.getScriptId());
		autoRenewalTerm.setValue(lease.getAutoRenewalTerm());
		leaseCustomFields.add(autoRenewalTerm);
		
		try {
			SelectCustomFieldRef unitNo = new SelectCustomFieldRef();
			unitNo.setScriptId(CustomSegmentEnum.UNIT_NO.getScriptId());		
			unitNo.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchForCustomSegmentId(CustomSegmentEnum.UNIT_NO.getScriptId(), lease.getUnitNo()), null, CustomSegmentEnum.UNIT_NO.getRecordTypeId()));
			leaseCustomFields.add(unitNo);		
		} catch (Exception e) {
			throw new RemoteException(String.format("Error occurred in lease %s while searching for unit no %s in the external system", lease.getExternalId(), lease.getUnitNo()), e);
		}

		if(lease.getClientCapitalCost() != null) {
			DoubleCustomFieldRef clientCapCost = new DoubleCustomFieldRef();
			clientCapCost.setScriptId(LeaseFieldEnum.CUSTOMER_CAP_COST.getScriptId());
			clientCapCost.setValue(lease.getClientCapitalCost().doubleValue());
			leaseCustomFields.add(clientCapCost);
		}

		BooleanCustomFieldRef cbvImpact = new BooleanCustomFieldRef();
		cbvImpact.setScriptId(LeaseFieldEnum.CBV_IMPACT.getScriptId());
		cbvImpact.setValue(lease.isCbvImpact());
		leaseCustomFields.add(cbvImpact);

		BooleanCustomFieldRef isAmendment = new BooleanCustomFieldRef();
		isAmendment.setScriptId(LeaseFieldEnum.IS_AMENDMENT.getScriptId());
		isAmendment.setValue(lease.isAmendment());
		leaseCustomFields.add(isAmendment);

		DoubleCustomFieldRef interestRate = new DoubleCustomFieldRef();
		interestRate.setScriptId(LeaseFieldEnum.INTEREST_RATE.getScriptId());
		interestRate.setValue(lease.getInterestRate());
		leaseCustomFields.add(interestRate);
			
		//CE Leases do not have a depreciation factor
		if(lease.getDepreciationFactor() != null) {
			DoubleCustomFieldRef depreciationRate = new DoubleCustomFieldRef();
			depreciationRate.setScriptId(LeaseFieldEnum.DEPRECIATION_RATE.getScriptId());
			depreciationRate.setValue(lease.getDepreciationFactor());
			leaseCustomFields.add(depreciationRate);			
		}			
				
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
	
		customRecord = new CustomRecord();
		customRecord.setAltName(lease.getName());
		customRecord.setExternalId(lease.getExternalId().toString());
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		customRecord.setCustomFieldList(leaseFieldList);
		//customRecord.setCustomForm(new RecordRef(null, "custform_ma_lessor_lease_form", null, null));		
		
		return customRecord;
	}
	
	private CustomRecord convertLeaseScheduleToConstomRecord(String leaseId, String extIdSequence, LeaseAccountingScheduleVO schedule) throws Exception{
		CustomRecord customRecord = null;
		List<Object> leaseScheduleCustomFields = new ArrayList<>();		
	
			DoubleCustomFieldRef paymentAmount = new DoubleCustomFieldRef();
			paymentAmount.setScriptId(LeaseFieldEnum.LEASE_PAYMENT_AMOUNT.getScriptId());
			paymentAmount.setValue(schedule.getAmount().doubleValue());
			leaseScheduleCustomFields.add(paymentAmount);

			DateCustomFieldRef paymentDate = new DateCustomFieldRef();
			paymentDate.setScriptId(LeaseFieldEnum.LEASE_PAYMENT_DATE.getScriptId());
			paymentDate.setValue(DateUtil.convertToCalendar(schedule.getTransDate()));	
			leaseScheduleCustomFields.add(paymentDate);
			
			SelectCustomFieldRef parentLease = new SelectCustomFieldRef();
			parentLease.setScriptId(LeaseFieldEnum.LEASE_PARENT.getScriptId());
			parentLease.setValue(new ListOrRecordRef(null, leaseId, null, LeaseFieldEnum.LEASE_PARENT.getRecordTypeId()));
			leaseScheduleCustomFields.add(parentLease);				

			CustomFieldList leaseSchedulFieldList = new CustomFieldList(leaseScheduleCustomFields.toArray(CustomFieldRef[]::new));			

			customRecord = new CustomRecord();
			customRecord.setExternalId(extIdSequence);
			customRecord.setRecType(new RecordRef(null, "199", null, null));
			customRecord.setCustomFieldList(leaseSchedulFieldList);
			customRecord.setCustomForm(new RecordRef(null, "-1199", null, null));
		
		
		return customRecord;		
	}	
	
	private CustomRecord convertLeaseModificationToConstomRecord(LeaseVO lease) throws Exception{
		CustomRecord customRecord = null;
		List<Object> leaseCustomFields = new ArrayList<>();		
		
		DoubleCustomFieldRef assetCarryingCost = new DoubleCustomFieldRef();
		assetCarryingCost.setScriptId(LeaseModificationFieldEnum.ASSET_CARRING_COST.getScriptId());
		assetCarryingCost.setValue(lease.getLeaseAssetCostCarrying().setScale(2).doubleValue());
		leaseCustomFields.add(assetCarryingCost);
		
		DoubleCustomFieldRef assertFairValue = new DoubleCustomFieldRef();
		assertFairValue.setScriptId(LeaseModificationFieldEnum.ASSET_FAIR_VALUE.getScriptId());
		assertFairValue.setValue(lease.getLeaseAssetFairValue().setScale(2).doubleValue());
		leaseCustomFields.add(assertFairValue);
		
		BooleanCustomFieldRef toBeProcessed = new BooleanCustomFieldRef();
		toBeProcessed.setScriptId(LeaseModificationFieldEnum.TO_BE_PROCESSED.getScriptId());
		toBeProcessed.setValue(true);
		leaseCustomFields.add(toBeProcessed);		
		
		LongCustomFieldRef term = new LongCustomFieldRef();
		term.setScriptId(LeaseModificationFieldEnum.TERM.getScriptId());
		term.setValue(lease.getTerm().longValue());		
		leaseCustomFields.add(term);
		
		DoubleCustomFieldRef leseeResidual = new DoubleCustomFieldRef();
		leseeResidual.setScriptId(LeaseModificationFieldEnum.LESEE_RESIDUAL.getScriptId());
		leseeResidual.setValue(lease.getResidualValueGuranteeByLesee().setScale(2).doubleValue());
		leaseCustomFields.add(leseeResidual);
		
		DoubleCustomFieldRef thridPartyResidual = new DoubleCustomFieldRef();
		thridPartyResidual.setScriptId(LeaseModificationFieldEnum.THIRD_PARTY_RESIDUAL.getScriptId());
		thridPartyResidual.setValue(0d); // <-- Based on mapping this is always zero		
		leaseCustomFields.add(thridPartyResidual);
						
		DoubleCustomFieldRef totalResidual = new DoubleCustomFieldRef();
		totalResidual.setScriptId(LeaseModificationFieldEnum.TOTAL_RESIDUAL.getScriptId());
		totalResidual.setValue(lease.getResidualValueEstimate().setScale(2).doubleValue());		
		leaseCustomFields.add(totalResidual);		
		
		if(lease.getEffectiveDate() != null) {
			DateCustomFieldRef effectiveDate = new DateCustomFieldRef();
			effectiveDate.setScriptId(LeaseModificationFieldEnum.EFFECTIVE_DATE.getScriptId());
			effectiveDate.setValue(DateUtil.convertToCalendar(lease.getEffectiveDate()));
			leaseCustomFields.add(effectiveDate);
		}
		
		SelectCustomFieldRef parentLease = new SelectCustomFieldRef();
		parentLease.setScriptId(LeaseModificationFieldEnum.LEASE_PARENT.getScriptId());
		parentLease.setValue(new ListOrRecordRef(null, service.getCustomRecordInternalId(CustomRecordEnum.LEASE.getScriptId(), lease.getExternalId()), null, suiteTalkCacheService.getCustomRecordTypeInternalId(CustomRecordEnum.LEASE.getScriptId())));		
		leaseCustomFields.add(parentLease);	
		
		DoubleCustomFieldRef clientCapCost = new DoubleCustomFieldRef();
		clientCapCost.setScriptId(LeaseModificationFieldEnum.CUSTOMER_CAP_COST.getScriptId());
		clientCapCost.setValue(lease.getClientCapitalCost().doubleValue());
		leaseCustomFields.add(clientCapCost);			
				
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		
		customRecord = new CustomRecord();
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE_MODIFICATION.getRecordTypeId(), null, null));
		customRecord.setCustomFieldList(leaseFieldList);
		customRecord.setCustomForm(new RecordRef("MA NG Lessor Lease Modification Form", null, null, null));
		
		return customRecord;
	}

	private CustomRecord convertLeaseToConstomRecordForUpdateClient(LeaseVO lease) throws Exception{
		if(lease.getClientInternalId() == null) {
			throw new NullPointerException("Lease does not have a client internal Id. Lease: " + lease);
		}

		List<Object> leaseCustomFields = new ArrayList<>();		

		CustomRecord customRecord = new CustomRecord();
        customRecord.setInternalId(lease.getInternalId());
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		
		SelectCustomFieldRef customer = new SelectCustomFieldRef();		
		customer.setScriptId(LeaseFieldEnum.CUSTOMER.getScriptId());		
		customer.setValue(new ListOrRecordRef(null, lease.getClientInternalId(), null, null)); 			
		leaseCustomFields.add(customer);
	
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		customRecord.setCustomFieldList(leaseFieldList);	

		return customRecord;
	}

	private CustomRecord convertLeaseToConstomRecordForUpdateInterestRate(LeaseVO lease) throws Exception{
		if(lease.getInterestRate() == null) { throw new NoDataFoundException("No interest rate found for lease: " + lease);}

		List<Object> leaseCustomFields = new ArrayList<>();		

		CustomRecord customRecord = new CustomRecord();
        customRecord.setInternalId(lease.getInternalId());
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		
		DoubleCustomFieldRef interestRate = new DoubleCustomFieldRef();
		interestRate.setScriptId(LeaseFieldEnum.INTEREST_RATE.getScriptId());
		interestRate.setValue(lease.getInterestRate());
		leaseCustomFields.add(interestRate);
	
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		customRecord.setCustomFieldList(leaseFieldList);	

		return customRecord;
	}

	private CustomRecord convertLeaseToConstomRecordForUpdateActualEndDate(LeaseVO lease) throws Exception{
		if(lease.getActualEndDate() == null) { throw new NoDataFoundException("No actual end date found for lease: " + lease);}

		List<Object> leaseCustomFields = new ArrayList<>();		

		CustomRecord customRecord = new CustomRecord();
        customRecord.setInternalId(lease.getInternalId());
		customRecord.setRecType(new RecordRef(null, CustomRecordEnum.LEASE.getRecordTypeId(), null, null));
		
		DateCustomFieldRef actualEndDate = new DateCustomFieldRef();
		actualEndDate.setScriptId(LeaseFieldEnum.ACTUAL_END_DATE.getScriptId());
		actualEndDate.setValue(DateUtil.convertToCalendar(lease.getActualEndDate()));
		leaseCustomFields.add(actualEndDate);
	
		CustomFieldList leaseFieldList = new CustomFieldList(leaseCustomFields.toArray(CustomFieldRef[]::new));
		customRecord.setCustomFieldList(leaseFieldList);	

		return customRecord;
	}
	
}
package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.FieldEnum;
import com.mikealbert.accounting.processor.vo.SuiteTalkDiagnostic;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.client.SuiteTalkClientService;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnBooleanCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnLongCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnStringCustomField;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;

public abstract class BaseSuiteTalkService {
	@Value("${spring.profiles.active}")
	protected String activeProfile;

	@Value("${mafs.suitetalk.domain}")
	String domain;

	@Value("${mafs.suitetalk.version}")
	String version;
	
	@Value("${mafs.suitetalk.account.id}" )
	String accountId;
	
	@Value( "${mafs.suitetalk.consumer.key}" )
	String consumerKey;
	
	@Value( "${mafs.suitetalk.consumer.secret}" )
	String consumerSecret;
	
	@Value( "${mafs.suitetalk.token.key}" )
	String tokenKey;
	
	@Value( "${mafs.suitetalk.token.secret}" )
	String tokenSecret;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource
	Environment env;
	
	protected SuiteTalkClientService service;
	
	@PostConstruct
	private void init() {
		 service = new SuiteTalkClientService(domain, version, accountId, consumerKey, consumerSecret, tokenKey, tokenSecret);		
	}
	
	protected String getEmployee(String id) throws Exception {
		return service.getEmployee(id).getEntityId();
	}
		
	protected CustomFieldRef getCustomFieldRef(String scriptId, CustomFieldList customFieldList) {
		if(customFieldList == null) return null;
		return Arrays.asList(customFieldList.getCustomField()).stream()
		        .filter(field -> field.getScriptId().equals(scriptId))
		        .findFirst()
		        .orElse(null);
	}
	
	protected String getCustomFieldValue(CustomFieldList customFieldList, FieldEnum fieldEnum) {
		String retVal = null;

		if(customFieldList == null) return retVal;

		CustomFieldRef val = Arrays.asList(customFieldList.getCustomField()).stream()
	            .filter(field -> field.getScriptId().equals(fieldEnum.getScriptId()))
		        .findFirst()
		        .orElse(null);

		retVal = getFieldValueName(val);

		return retVal;
	}

	protected String getSearchColumnCustomFieldValue(SearchColumnCustomFieldList customFieldList, FieldEnum fieldEnum) {
		String retVal = null;

		if(customFieldList == null) return retVal;

		SearchColumnCustomField val = Arrays.asList(customFieldList.getCustomField()).stream()
	            .filter(field -> field.getScriptId().equals(fieldEnum.getScriptId()))
		        .findFirst()
		        .orElse(null);

		retVal = getSearchColumnCustomFieldValue(val);

		return retVal;
	}	

	protected String getFieldValue(CustomFieldRef customFieldRef) {
		String retVal = null;
		
		if(customFieldRef instanceof SelectCustomFieldRef) {
			SelectCustomFieldRef field = (SelectCustomFieldRef) customFieldRef;	
			retVal = field.getValue().getInternalId();
		}
		if(customFieldRef instanceof DoubleCustomFieldRef) {
			DoubleCustomFieldRef field = (DoubleCustomFieldRef) customFieldRef;	
			retVal = String.valueOf(field.getValue());
		}		
		if(customFieldRef instanceof BooleanCustomFieldRef) {
			BooleanCustomFieldRef field = (BooleanCustomFieldRef) customFieldRef;	
			retVal =  String.valueOf(field.isValue());
		}	
		if(customFieldRef instanceof LongCustomFieldRef) {
			LongCustomFieldRef field = (LongCustomFieldRef) customFieldRef;	
			retVal = String.valueOf(field.getValue());
		} 
		if(customFieldRef instanceof DateCustomFieldRef) {
			DateCustomFieldRef field = (DateCustomFieldRef) customFieldRef;
			retVal = DateUtil.convertToDateTimeString(field.getValue().getTime());
		}
		if(customFieldRef instanceof StringCustomFieldRef) {
			StringCustomFieldRef field = (StringCustomFieldRef) customFieldRef;
			retVal = field.getValue();
		}		
		
		return retVal;
	}	
	
	protected String getFieldValueName(CustomFieldRef customFieldRef) {
		String retVal = null;
		
		if(customFieldRef instanceof SelectCustomFieldRef) {
			SelectCustomFieldRef field = (SelectCustomFieldRef) customFieldRef;	
			retVal = field.getValue().getName();
		}
		if(customFieldRef instanceof DoubleCustomFieldRef) {
			DoubleCustomFieldRef field = (DoubleCustomFieldRef) customFieldRef;	
			retVal = String.valueOf(field.getValue());
		}		
		if(customFieldRef instanceof BooleanCustomFieldRef) {
			BooleanCustomFieldRef field = (BooleanCustomFieldRef) customFieldRef;	
			retVal =  String.valueOf(field.isValue());
		}	
		if(customFieldRef instanceof LongCustomFieldRef) {
			LongCustomFieldRef field = (LongCustomFieldRef) customFieldRef;	
			retVal = String.valueOf(field.getValue());
		} 
		if(customFieldRef instanceof DateCustomFieldRef) {
			DateCustomFieldRef field = (DateCustomFieldRef) customFieldRef;
			retVal = DateUtil.convertToDateTimeString(field.getValue().getTime());
		}
		if(customFieldRef instanceof StringCustomFieldRef) {
			StringCustomFieldRef field = (StringCustomFieldRef) customFieldRef;
			retVal = field.getValue();
		}		
		
		return retVal;
	}

	protected String getSearchColumnCustomFieldValue(SearchColumnCustomField customField) {
		String retVal = null;

		if(customField instanceof SearchColumnBooleanCustomField){
			SearchColumnBooleanCustomField field = (SearchColumnBooleanCustomField) customField;
			retVal = field.getSearchValue().toString();
		} 
		if(customField instanceof SearchColumnDateCustomField) {
			SearchColumnDateCustomField field = (SearchColumnDateCustomField) customField;
			retVal = DateUtil.convertToDateString(field.getSearchValue().getTime());
		}		
		if(customField instanceof SearchColumnLongCustomField) {
			SearchColumnLongCustomField field = (SearchColumnLongCustomField) customField;
			retVal = String.valueOf(field.getSearchValue());
		}
		if(customField instanceof SearchColumnSelectCustomField){
			SearchColumnSelectCustomField field = (SearchColumnSelectCustomField) customField;
			retVal = field.getSearchValue().getName() == null ? field.getSearchValue().getInternalId() : field.getSearchValue().getName();
		}
		if(customField instanceof SearchColumnStringCustomField) {
			SearchColumnStringCustomField field = (SearchColumnStringCustomField) customField;	
			retVal = field.getSearchValue();
		}		
	
		return retVal;
	}	

	protected SuiteTalkDiagnostic getSuiteTalkDiagnostic() throws Exception {
		String leaseRecordTypeId = null;
		String leaseModificationRecordTypeId = null;
		String leasePaymentRecordTypeId = null; 
		String unitCustomRecordId = null;
		String AssetTypeCustomRecordId = null;
		String AssetCustomRecordId = null;
		Map<String, String> dataCenterInfo = null;
		
		try {
			leaseRecordTypeId = service.getCustomRecordTypeInternalId(CustomRecordEnum.LEASE.getScriptId());
		} catch(Exception e) {
			LOG.warn("Failed to get lease record type", e);
		}
		
		try {
			leaseModificationRecordTypeId = service.getCustomRecordTypeInternalId(CustomRecordEnum.LEASE_MODIFICATION.getScriptId());
		} catch(Exception e) {
			LOG.warn("Failed to get lease modification record type", e);
		}
		
		try {
			leasePaymentRecordTypeId = service.getCustomRecordTypeInternalId(CustomRecordEnum.PAYMENT.getScriptId());			
		} catch(Exception e) {
			LOG.warn("Failed to get lease payment record type", e);
		}

		try {
			unitCustomRecordId = service.getCustomRecordTypeInternalId(CustomRecordEnum.UNIT.getScriptId());
		} catch(Exception e) {
			LOG.warn("Failed to get unit segment", e);
		}		 

		try {
			AssetTypeCustomRecordId = service.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET_TYPE.getScriptId());
		} catch(Exception e) {
			LOG.warn("Failed to get asset type", e);
		}		 

		try {
			AssetCustomRecordId = service.getCustomRecordTypeInternalId(CustomRecordEnum.ASSET.getScriptId());
		} catch(Exception e) {
			LOG.warn("Failed to get asset", e);
		}		 
		
		try {
			dataCenterInfo = service.getDataCenterInfo();			
		} catch(Exception e) {
			LOG.warn("Failed to get data center info", e);
		}
		
		return new SuiteTalkDiagnostic()
				.setLeaseRecord(leaseRecordTypeId != null && !leaseRecordTypeId.isBlank() ? true : false)
				.setLeaseModificationRecord(leaseModificationRecordTypeId != null && !leaseModificationRecordTypeId.isBlank() ? true : false)
				.setLeasePaymentRecord(leasePaymentRecordTypeId != null && !leasePaymentRecordTypeId.isBlank() ? true : false)
				.setUnitCustomSegment(unitCustomRecordId != null && !unitCustomRecordId.isBlank() ? true : false)
				.setAssetTypeRecord(AssetTypeCustomRecordId != null && !AssetTypeCustomRecordId.isBlank() ? true : false)
				.setAssetRecord(AssetCustomRecordId != null && !AssetCustomRecordId.isBlank() ? true : false)
				.setDataCenterInfo(dataCenterInfo);
	}
}

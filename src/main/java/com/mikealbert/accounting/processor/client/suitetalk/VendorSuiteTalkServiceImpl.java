package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.BankDetailFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.VendorSuiteTalkFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.VendorVO;
import com.mikealbert.util.data.DataUtil;
import com.netsuite.webservices.lists.relationships_2023_2.Vendor;
import com.netsuite.webservices.lists.relationships_2023_2.VendorAddressbook;
import com.netsuite.webservices.platform.common_2023_2.Address;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchResult;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.GetCustomizationType;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecord;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearch;

@Service("vendorSuiteTalkService")
public class VendorSuiteTalkServiceImpl extends BaseSuiteTalkService implements VendorSuiteTalkService {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public VendorVO get(String internalId, String externalId) throws Exception {
		RecordRef recRef = new RecordRef();
		recRef.setType(RecordType.vendor);
		recRef.setInternalId(internalId);
		recRef.setExternalId(externalId);

		ReadResponse readResponse = service.getService().get(recRef);
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve vendor with internal id=%s, external id=%s .", internalId, externalId), readResponse);
		}
		
		Vendor vendor = (Vendor)readResponse.getRecord();

		String payeeOrCompanyName = vendor.getPrintOnCheckAs() == null ? vendor.getCompanyName() : vendor.getPrintOnCheckAs();
		VendorVO vendorVO = new VendorVO()
				.setcId(Long.valueOf(vendor.getSubsidiary().getInternalId()))
				.setAccountCode(vendor.getEntityId())
				.setAccountName(payeeOrCompanyName)
				.setShortName(payeeOrCompanyName)
				.setPayeeName(vendor.getPrintOnCheckAs())
				.setAccStatus(vendor.getIsInactive() ? "C" : "O")
				.setEmail(vendor.getEmail())
				.setTaxIdNum(vendor.getTaxIdNum())
				.setRegName(vendor.getCompanyName())
				.setEntityId(vendor.getEntityId())
				.setGroupCode(String.valueOf(vendor.getIs1099Eligible()))
				.setPaymentMethod(super.getCustomFieldValue(vendor.getCustomFieldList(), VendorSuiteTalkFieldEnum.PAYMENT_METHOD))
				.setPhone(vendor.getPhone())
				.setFax(vendor.getFax())
				.setOrgizationType(vendor.getCategory() ==  null ? null : vendor.getCategory().getName())
				.setPaymentTerm(vendor.getTerms().getName())
				.setPayeeName(payeeOrCompanyName)
				.setIsDeliveringDealer(DataUtil.convertToBoolean(super.getCustomFieldValue(vendor.getCustomFieldList(), VendorSuiteTalkFieldEnum.DELIVERING_DEALER)) ? "T" : "F")
				.setContactJobTitle(super.getCustomFieldValue(vendor.getCustomFieldList(), VendorSuiteTalkFieldEnum.CONTACT_JOB_TITLE))
				.setContactFirstname(super.getCustomFieldValue(vendor.getCustomFieldList(), VendorSuiteTalkFieldEnum.CONTACT_FIRST_NAME))
				.setContactLastName(super.getCustomFieldValue(vendor.getCustomFieldList(), VendorSuiteTalkFieldEnum.CONTACT_LAST_NAME))
				.setInactive(vendor.getIsInactive());
				
		CustomRecordSearch crs = new CustomRecordSearch();
		CustomRecordSearchBasic crsb = new CustomRecordSearchBasic();
		crsb.setRecType(new RecordRef(null, super.service.getCustomXId(CustomRecordEnum.BANK_DETAIL.getScriptId(), GetCustomizationType.customRecordType), null, null));

		SearchMultiSelectCustomField smf = new SearchMultiSelectCustomField();
		smf.setScriptId("custrecord_2663_parent_vendor");
		smf.setOperator(SearchMultiSelectFieldOperator.anyOf);
		smf.setSearchValue(new ListOrRecordRef[]{new ListOrRecordRef(null, vendor.getInternalId(), vendor.getExternalId(), null)});

		crsb.setCustomFieldList(new SearchCustomFieldList(new SearchCustomField[]{smf}));
				
		crs.setBasic(crsb);
								 
		SearchResult bankDetailSearchresult = service.getService().search(crs);				
		if(!bankDetailSearchresult.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Search for vendor's bank details failed. parentVendorid=%s.", vendor.getInternalId()), bankDetailSearchresult);
		}

		CustomRecord bankDetail = null;
		if(bankDetailSearchresult.getRecordList().getRecord() != null) {
			bankDetail = Arrays.stream(bankDetailSearchresult.getRecordList().getRecord())
					.map(record -> (CustomRecord)record)
					.filter(custRec -> super.getFieldValue(super.getCustomFieldRef(BankDetailFieldEnum.BANK_TYPE.getScriptId(), custRec.getCustomFieldList())).equals("1"))
					.findFirst()
					.orElse(null);
		}

		if(bankDetail != null) {
			vendorVO
					.setBankName(bankDetail.getName())
					.setBankAccountTypeId(Long.valueOf(super.getFieldValue(super.getCustomFieldRef(BankDetailFieldEnum.BANK_ACCOUNT_TYPE.getScriptId(), bankDetail.getCustomFieldList()))))
					.setBankAccountName(super.getCustomFieldValue(bankDetail.getCustomFieldList(), BankDetailFieldEnum.BANK_ACCOUNT_NAME))
					.setBankAccountNumber(super.getCustomFieldValue(bankDetail.getCustomFieldList(), BankDetailFieldEnum.BANK_ACCOUNT_NUMBER))
					.setBankSortCode(super.getCustomFieldValue(bankDetail.getCustomFieldList(), BankDetailFieldEnum.BANK_NUMBER));
		}
							
		return vendorVO;
	}

	@Override
	public void updateVendorExternalId(String vendorInternalId, String vendorExternalId) throws Exception {
        WriteResponse writeResponse;
		ReadResponse readResponse;
		Vendor vendor;
	
		LOG.info("Pre update vendor external call to suitetalk ");
		
        RecordRef recRef = new RecordRef();
		recRef.setInternalId(vendorInternalId);
		recRef.setType(RecordType.vendor);
		readResponse = service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve vendor with internal id=%s .", vendorInternalId), readResponse);
		}
		
		vendor = (Vendor) readResponse.getRecord();																			
		vendor.setExternalId(vendorExternalId);
		 
		writeResponse = service.getService().update(vendor);
		if(!writeResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while updating vendor externalId addressInternalId=%s and addressExternalId=%s.", vendorInternalId, vendorExternalId), writeResponse);			
		}
		
		LOG.info("Post update vendor external call to suitetalk ");				
	}
	
	public void updateVendorExternalIdAndAddressExternalId(String vendorInternalId, String vendorExternalId, String addressInternalId, String addressExternalId) throws Exception {
        WriteResponse writeResponse;
		ReadResponse readResponse;
		Vendor vendor;
		List<Object> customFields = new ArrayList<>(0);
		
		LOG.info("Pre update vendor and address external call to suitetalk ");
		
        RecordRef recRef = new RecordRef();
		recRef.setInternalId(vendorInternalId);
		recRef.setType(RecordType.vendor);
		readResponse = service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve vendor with internal id=%s . ", vendorInternalId), readResponse);
		}
		
		vendor = (Vendor) readResponse.getRecord();								
				
		StringCustomFieldRef extId = new StringCustomFieldRef();
		extId.setScriptId("custrecord_ma_external_id");
		extId.setValue(addressExternalId);
		customFields.add(extId);
						
		CustomFieldList addressFieldList = new CustomFieldList(customFields.toArray(CustomFieldRef[]::new));		
		
		Address address = new Address();
		address.setInternalId(addressInternalId);
		address.setCustomFieldList(addressFieldList);
		
		vendor.getAddressbookList().setReplaceAll(false);
		for(VendorAddressbook vab : vendor.getAddressbookList().getAddressbook()) {
			if(vab.getInternalId().equals(address.getInternalId())) {
				vab.setAddressbookAddress(address);
			}
		}
		
		vendor.setExternalId(vendorExternalId);
		 
		writeResponse = service.getService().update(vendor);
		if(!writeResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while updating vendor address with addressInternalId=%s and addressExternalId=%s. ", addressInternalId, addressExternalId), writeResponse);
		}
		
		LOG.info("Post update vendor and address external call to suitetalk ");		
	}
	
}
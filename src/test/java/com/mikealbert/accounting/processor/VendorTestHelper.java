package com.mikealbert.accounting.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.entity.Supplier;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorFieldEnum;
import com.mikealbert.accounting.processor.service.VendorService;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.accounting.processor.vo.VendorVO;

public class VendorTestHelper {
	public static final long ACCOUNT_C_ID = 1l;
	public static final String ACCOUNT_TYPE = "S";
	public static final String ACCOUNT_CODE = "00000000";
	public static final String CHILD_ACCOUNT_CODE = VendorService.CHILD_ACCOUNT_CODE_PREFIX + "00000000" + VendorService.CHILD_ACCOUNT_CODE_SUFFIX;	
	public static final String CONTACT_TYPE_PRIMARY = "PRIMARY";
	public static final String ORIGINATOR = "PERSONNEL_BASE";
	public static final TaxJurisdictionVO TAX_JURISDICTION = new TaxJurisdictionVO("US", "OH", "061", "EVENDALE", "45241", "0021", "00-00-0021");
	public static final String ATTENTION = "ATTENTION";
	
	public static List<Map<String, Object>> generateMockNSAddresses(int size) {
		List<Map<String, Object>> addresses = new ArrayList<>();
		
		for(int i=0; i < size; i++) {
			Map<String, Object> address = new HashMap<>();
			address.put(VendorAddressFieldEnum.INTERNAL_ID.getName(), Long.valueOf(i));
			address.put(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);
			address.put(VendorAddressFieldEnum.ATTENTION.getName(), ATTENTION);			
			address.put(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "F");
			address.put(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), i == 0 ? "T" : "F");
			address.put(VendorAddressFieldEnum.ADDRESS_LINE_1.getName(), String.format("Address Line 1 - %d", i));		
			address.put(VendorAddressFieldEnum.ADDRESS_LINE_2.getName(), String.format("Address Line 2 - %d", i));
			address.put(VendorAddressFieldEnum.COUNTRY.getName(), TAX_JURISDICTION.getCountry());
			address.put(VendorAddressFieldEnum.COUNTY.getName(), TAX_JURISDICTION.getCounty());
			address.put(VendorAddressFieldEnum.STATE.getName(), TAX_JURISDICTION.getRegion());
			address.put(VendorAddressFieldEnum.CITY.getName(), TAX_JURISDICTION.getCity());		
			address.put(VendorAddressFieldEnum.ZIP.getName(), TAX_JURISDICTION.getPostalCode());
			
			addresses.add(address);
		}
		
		return addresses;		
	}
	
	public static ExternalAccount generateAccount(int numberOfAddresses) {
		ExternalAccount account = new ExternalAccount(new ExternalAccountPK(1L, "S", ACCOUNT_CODE));
		account.setAccountName("UNIT-TEST");
		account.setTelephoneNumber("(513 555-5555)");
		account.setTaxRegNo("TAX ID");				
		account.setExternalAccountAddresses(new ArrayList<>());
		
		for(int i=0; i < numberOfAddresses; i++) {		
			account.getExternalAccountAddresses().add(new ExtAccAddress());
			account.getExternalAccountAddresses().get(i).setExternalAccount(account);				
			account.getExternalAccountAddresses().get(i).setEaaId(Long.valueOf(i));			
			account.getExternalAccountAddresses().get(i).setAddressType("POST");
			account.getExternalAccountAddresses().get(i).setDefaultInd(i == 0 ? "Y" : null);
			account.getExternalAccountAddresses().get(i).setAddressCode((i == 0 || numberOfAddresses == 1) ? "W9" : String.valueOf(i));					
			account.getExternalAccountAddresses().get(i).setDefaultInd(i == 0 ? "Y" : null);		
			account.getExternalAccountAddresses().get(i).setAddressLine1("Test " + i);
			account.getExternalAccountAddresses().get(i).setGeoCode("GEO Code");
			account.getExternalAccountAddresses().get(i).setCountry("USA");
			account.getExternalAccountAddresses().get(i).setRegion("OH");
			account.getExternalAccountAddresses().get(i).setCountyCode("061");			
			account.getExternalAccountAddresses().get(i).setTownCity("Evendale");
			account.getExternalAccountAddresses().get(i).setPostcode("45241");	
			account.setSuppliers(new ArrayList<>(0));
			account.getSuppliers().add(new Supplier());
			account.getSuppliers().get(0).setSupId(i);
			account.getSuppliers().get(0).setInactiveInd("N");
		}
				
		return account;
		
	}	
	
	public static Map<String, String> createVendorMap() {
		Map<String, String> vendor = new HashMap<>();
		vendor.put(VendorFieldEnum.ENTITY_ID.getName(), "1");
		vendor.put(VendorFieldEnum.C_ID.getName(), "1");
		vendor.put(VendorFieldEnum.ACCOUNT_CODE.getName(), ACCOUNT_CODE);
		vendor.put(VendorFieldEnum.EXTERNAL_ID.getName(), null);
		vendor.put(VendorFieldEnum.INACTIVE.getName(), "No");
		vendor.put(VendorFieldEnum.ACCOUNT_NAME.getName(), "This a test, really a fake company name that exceeds the 80 character column length.");
		vendor.put(VendorFieldEnum.PAYEE_NAME.getName(), null);
		vendor.put(VendorFieldEnum.EMAIL.getName(), "vistest@mikealbert.com");
		vendor.put(VendorFieldEnum.PAYMENT_METHOD.getName(), "ACH");
		vendor.put(VendorFieldEnum.GROUP_CODE.getName(), "false");
		vendor.put(VendorFieldEnum.FAX.getName(), "444-444-4444");
		vendor.put(VendorFieldEnum.PHONE.getName(), "555-555-5555");		
		vendor.put(VendorFieldEnum.CATEGORY.getName(), "OTHER");
		vendor.put(VendorFieldEnum.PAYMENT_TERM.getName(), "IMMED");
		//vendor.put(VendorFieldEnum.PAYEE_NAME.getName(), "LAFS, LLC.");	
		vendor.put(VendorFieldEnum.DELIVERING_DEALER.getName(), "F");
		vendor.put(VendorFieldEnum.TAX_ID.getName(), "12-3456789");
		vendor.put(VendorFieldEnum.BANK_ACCOUNT_NAME.getName(), "Bank Account Name");
		vendor.put(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName(), "00000000");
		vendor.put(VendorFieldEnum.BANK_NAME.getName(), "Bank Name");
		vendor.put(VendorFieldEnum.BANK_NUMBER.getName(), "000");
		vendor.put(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName(), "2");		
		
		return vendor;
	}
	
	public static VendorVO createVendorVO() {
		return 	new VendorVO()
		.setEntityId("1")
		.setcId(1L)
		.setAccountCode(ACCOUNT_CODE)
		.setInactive(false)
		.setAccountName("This a test, really a fake company name that exceeds the 80 character column length.")
		.setRegName("This a test, really a fake company name that exceeds the 80 character column length.")
		.setEmail("vistest@mikealbert.com")
		.setTaxIdNum("12-3456789")
		.setPaymentMethod("ACH")
		.setGroupCode("false")
		.setFax("444-444-4444")
		.setPhone("555-555-5555")
		.setOrgizationType("OTHER")
		.setPaymentTerm("IMMED")
		.setPayeeName(null)
		.setBankName("Bank Name")
		.setBankAccountName("Bank Account Name")
		.setBankAccountNumber("00000000")
		.setBankSortCode("000")
		.setBankAccountTypeId(2L)
		.setIsDeliveringDealer("F")
		.setContactJobTitle("elf")
		.setContactFirstname("John")
		.setContactLastName("Doe");		
	}
}

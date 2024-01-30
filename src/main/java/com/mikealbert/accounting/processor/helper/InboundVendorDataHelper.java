package com.mikealbert.accounting.processor.helper;

import java.util.Map;

import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorFieldEnum;
import com.mikealbert.util.data.DataUtil;

public class InboundVendorDataHelper {
	public static final String DEFAULT_TAX_ID = "NA";
	
	public static String convertTaxId(Map<String, String> vendor) {
		String taxId = vendor.get(VendorFieldEnum.TAX_ID.getName());
		
		if(taxId == null) {
			taxId = DEFAULT_TAX_ID;
		} else {
			taxId = taxId.replace("-", "").replace(" ", "").strip();
		}
		
		return taxId == null || taxId.isBlank() ? DEFAULT_TAX_ID : taxId;
	}
	
	public static String convertZip(Map<String, Object> address) {
		String zip = (String) address.get(VendorAddressFieldEnum.ZIP.getName());
		String country = (String) address.get(VendorAddressFieldEnum.COUNTRY.getName());
		
		zip = country.equals("US") ? DataUtil.substr(zip,  0, 5) : DataUtil.substr(zip,  0, 7);
		
		return zip;
		
	}
	
	//TODO XRef maybe?
	public static Long convertBankAccountTypeId(Map<String, String> bankAccount) {
		Long bankAccountId = bankAccount.get(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName()).equals("1") ? 1L : 4L;
		return bankAccountId;
	}

}

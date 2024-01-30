package com.mikealbert.accounting.processor.enumeration;

public enum VendorFieldEnum {
	ENTITY_ID("entityId"),
	C_ID("cId"),
	ACCOUNT_CODE("accountCode"),	
	EXTERNAL_ID("vendorExtId"),
	INACTIVE("isInactive"),
	ACCOUNT_NAME("accountName"),
	EMAIL("email"),
	TAX_ID("taxIdNum"),
	PAYMENT_METHOD("paymentMethod"),
	GROUP_CODE("groupCode"),
	FAX("fax"),
	PHONE("phone"),
	CATEGORY("category"),
	PAYMENT_TERM("paymentTerm"),
	PAYEE_NAME("payeeName"),
	BANK_NAME("bankName"),
	BANK_ACCOUNT_NAME("bankAccountName"),
	BANK_ACCOUNT_NUMBER("bankAccountNumber"),
	BANK_ACCOUNT_TYPE_ID("bankAccountTypeId"),
	BANK_NUMBER("bankNumber"),
	DELIVERING_DEALER("deliveringDealer"),	
	CONTACT_JOB_TITLE("jobTitle"),
	CONTACT_FIRST_NAME("contactFirstName"),
	CONTACT_LAST_NAME("contactLastName"),
	COMPANY_NAME("companyName");
	
	private final String name;

	private VendorFieldEnum(String name) {
		this.name = name;
	}
	
	public static VendorFieldEnum getVendorField(String name) {
		for(VendorFieldEnum vendorField : values()) {
			if(vendorField.getName().equals(name)) {
				return vendorField;
			}
		}
		throw new IllegalArgumentException("Unknown Vendor Field: " + name);
	}
	
	public static boolean isVendorField(String name) {
		boolean retVal = false;
		for(VendorFieldEnum vendorField : values()) {
			if(vendorField.getName().equals(name)) {
		      retVal = true;
		      break;
			}
		}		
		return retVal;
	}
	
	public String getName() {
		return name;
	}
}

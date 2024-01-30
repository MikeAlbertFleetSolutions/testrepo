package com.mikealbert.accounting.processor.enumeration;

public enum VendorAddressFieldEnum {
	INTERNAL_ID("internalId"),
	ATTENTION("attention"),
	DEFAULT_BILLING_ADDRESS("isDefaulBillAddress"),
	ADDRESS_LINE_1("addressLine1"),
	ADDRESS_LINE_2("addressLine2"),
	ADDRESS_LINE_3("addressLine3"),	
	COUNTRY("country"),
	STATE("state"),
	COUNTY("county"),	
	CITY("city"),	
	ZIP("zip"),
	CHILD_VENDOR("childVendor"),
	EXTERNAL_ID("externalId");
	
	private final String name;

	private VendorAddressFieldEnum(String name) {
		this.name = name;
	}
	
	public static VendorAddressFieldEnum getField(String name) {
		for(VendorAddressFieldEnum field : values()) {
			if(field.getName().equals(name)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Address Field: " + name);
	}
	
	public static boolean isField(String name) {
		boolean retVal = false;
		for(VendorAddressFieldEnum field : values()) {
			if(field.getName().equals(name)) {
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

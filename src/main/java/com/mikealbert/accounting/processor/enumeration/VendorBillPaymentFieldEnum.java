package com.mikealbert.accounting.processor.enumeration;

public enum VendorBillPaymentFieldEnum {
	EXTERNAL_ID("externalId"),
	PAYMENT_DATE("paymentDate"),
	PAYMENT_METHOD("paymentMethod");	
	
	private final String name;
	
	private VendorBillPaymentFieldEnum(String name) {
		this.name = name;
	}
	
	public static VendorBillPaymentFieldEnum getField(String name) {
		for(VendorBillPaymentFieldEnum field : values()) {
			if(field.getName().equals(name)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Bill Payment Field: " + name);
	}
	
	public static boolean isField(String name) {
		boolean retVal = false;
		for(VendorBillPaymentFieldEnum field : values()) {
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

package com.mikealbert.accounting.processor.enumeration;

public enum PaymentMethodEnum {
	CHECK("CHECKDAILY"),
	ACH("EFTDAILY"),
	WIRE("WIRE"),
	INTERCOMPANY("EFTOTHER");
	
	private final String value;

	private PaymentMethodEnum(String value) {
		this.value = value;
	}
	
	public PaymentMethodEnum getPaymentMethod(String value) {
		for(PaymentMethodEnum paymentMethod : values()) {
			if(paymentMethod.getValue().equals(value.toUpperCase())) {
				return paymentMethod;
			}
		}
		throw new IllegalArgumentException("Unknown Payment Method: " + value);
	}
	
	public String getValue() {
		return value;
	}
}

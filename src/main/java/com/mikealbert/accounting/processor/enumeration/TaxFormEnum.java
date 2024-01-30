package com.mikealbert.accounting.processor.enumeration;

public enum TaxFormEnum {
	NONE("1099-NONE"),
	MISC("1099-MISC");
	
	private final String value;

	private TaxFormEnum(String value) {
		this.value = value;
	}
	
	public TaxFormEnum getPaymentMethod(String value) {
		for(TaxFormEnum taxForm : values()) {
			if(taxForm.getValue().equals(value.toUpperCase())) {
				return taxForm;
			}
		}
		throw new IllegalArgumentException("Unknown Tax Form: " + value);
	}
	
	public String getValue() {
		return value;
	}
}

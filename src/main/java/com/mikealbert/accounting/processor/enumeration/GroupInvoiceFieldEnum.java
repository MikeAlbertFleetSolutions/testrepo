package com.mikealbert.accounting.processor.enumeration;

public enum GroupInvoiceFieldEnum implements FieldEnum {

    CREATED_DATE("custrecord_ci_created_date"),
    INVOICED_TOTAL("custrecord_ci_amount");

	private final String scriptId;

	private GroupInvoiceFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}    

    @Override
    public String getScriptId() {
        return scriptId;
    }
    
}

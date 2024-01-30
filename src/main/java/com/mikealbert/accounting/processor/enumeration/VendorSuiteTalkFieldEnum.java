package com.mikealbert.accounting.processor.enumeration;

public enum VendorSuiteTalkFieldEnum implements FieldEnum {
	CONTACT_FIRST_NAME("custentity_ma_contact_first_name"),
	CONTACT_JOB_TITLE("custentity_ma_job_title"),
	CONTACT_LAST_NAME("custentity_ma_contact_last_name"),	
    DELIVERING_DEALER("custentity_mafs_delivery_dealer"),
	PAYMENT_METHOD("custentity_mafs_default_payment_method");

	private final String scriptId;

	private VendorSuiteTalkFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}

	@Override
	public String getScriptId() {
		return scriptId;
	}

}

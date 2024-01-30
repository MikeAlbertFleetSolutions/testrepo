package com.mikealbert.accounting.processor.enumeration;

public enum BankDetailFieldEnum implements FieldEnum {

	BANK_ACCOUNT_NAME("custrecord_2663_entity_acct_name"),
	BANK_ACCOUNT_NUMBER("custrecord_2663_entity_acct_no"),
	BANK_ACCOUNT_TYPE("custrecord_2663_entity_bank_acct_type"),
	BANK_NUMBER("custrecord_2663_entity_bank_no"),
	BANK_TYPE("custrecord_2663_entity_bank_type");

	private final String scriptId;

	private BankDetailFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}

	@Override
	public String getScriptId() {
		return scriptId;
	}

}

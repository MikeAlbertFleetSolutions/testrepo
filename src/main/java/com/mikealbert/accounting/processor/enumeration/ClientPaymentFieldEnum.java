package com.mikealbert.accounting.processor.enumeration;

public enum ClientPaymentFieldEnum {
	EXTERNAL_ID("externalId"), 
	INTERNAL_ID("internalId"),
	TRAN_ID("tranid"),
	TRANSACTION_NUMBER("transactionnumber");	
	
	private final String scriptId;
	
	private ClientPaymentFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public String getScriptId() {
		return scriptId;
	}
		
}

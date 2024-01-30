package com.mikealbert.accounting.processor.enumeration;

public enum ClientTransactionGroupFieldEnum implements FieldEnum {
	ACCOUNTING_PERIOD_DATE("ending"),
	ACCOUNTING_PERIOD_ID("accounting_period_id"),
	CUSTOMER_EXTERNAL_ID("customer_external_id"), 	
	CUSTOMER_INTERNAL_ID("customer_internal_id"), 
	GROUP_NUMBER("group_number");	
	
	private final String scriptId;
	
	private ClientTransactionGroupFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public static ClientTransactionGroupFieldEnum getField(String scriptId) {
		for(ClientTransactionGroupFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(ClientTransactionGroupFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
		      retVal = true;
		      break;
			}
		}		
		return retVal;
	}
	
	@Override
	public String getScriptId() {
		return scriptId;
	}
		
}

package com.mikealbert.accounting.processor.enumeration;

public enum CustomBodyEnum {
	GROUP_INVOICE_LINK("custbody_ma_ci_link");	
	
	private final String scriptId;
	
	private CustomBodyEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public String getScriptId() {
		return scriptId;
	}

}

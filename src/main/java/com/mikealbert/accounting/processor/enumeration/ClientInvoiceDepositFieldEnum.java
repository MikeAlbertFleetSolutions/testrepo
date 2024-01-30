package com.mikealbert.accounting.processor.enumeration;

public enum ClientInvoiceDepositFieldEnum implements FieldEnum {
	DEPOSIT_APPLICATION_INTERNAL_ID("depositApplicationInternalId"),
	DEPOSIT_APPLICATION_TRANID("depositApplicationTranId"),
	INVOICE_INTERNAL_ID("invoiceInternalId"), 	
	INVOICE_TRANID("invoiceTranId");	
	
	private final String scriptId;
	
	private ClientInvoiceDepositFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	@Override
	public String getScriptId() {
		return scriptId;
	}
		
}

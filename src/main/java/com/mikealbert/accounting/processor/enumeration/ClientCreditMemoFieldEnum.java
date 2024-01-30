package com.mikealbert.accounting.processor.enumeration;

public enum ClientCreditMemoFieldEnum implements FieldEnum {
	DOC_ID("custbody_ma_doc"),
	DOC_LINE_ID("custbody_ma_lineid"),
	EXTERNAL_ID("externalId"), 
	GROUP_INVOICE("custbody_ma_for_group_invoice"),	
	GROUP_NUMBER("custbody_ma_group_inv_number"),	
	INTERNAL_ID("internalId"),
	MA_DOC_ID("custcol_ma_doc_id"),
	MA_LINE_ID("custcol_malineid"),	
	TRAN_ID("tranid"),
	TRANSACTION_NUMBER("transactionnumber");	
	
	private final String scriptId;
	
	private ClientCreditMemoFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	@Override
	public String getScriptId() {
		return scriptId;
	}
		
}

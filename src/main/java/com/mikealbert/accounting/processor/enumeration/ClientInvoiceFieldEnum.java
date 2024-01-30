package com.mikealbert.accounting.processor.enumeration;

public enum ClientInvoiceFieldEnum implements FieldEnum {
	DOC_ID("custbody_ma_doc"),
	DOC_LINE_ID("custbody_ma_lineid"),
	GROUP_INVOICE("custbody_ma_for_group_invoice"),	
	GROUP_NUMBER("custbody_ma_group_inv_number"),
	MA_AUTO_APPROVE("custbody_ma_auto_approve"),
	MA_DOC_ID("custcol_ma_doc_id"),
	MA_LINE_ID("custcol_malineid"),
	MA_TYPE("custbody_ma_type"),
	SKIP_APPROVAL("custbody_skip_approval"); //TODO AgingTransactionSuiteTalkServiceImpl uses this for the client aging data


	private final String scriptId;
	
	private ClientInvoiceFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
		
	@Override
	public String getScriptId() {
		return scriptId;
	}
		
}

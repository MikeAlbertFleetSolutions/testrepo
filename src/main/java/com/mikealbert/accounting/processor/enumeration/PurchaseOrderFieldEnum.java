package com.mikealbert.accounting.processor.enumeration;

public enum PurchaseOrderFieldEnum {
	INTERNAL_ID("internalId"),
	EXTERNAL_ID("externalId"), 
	APPROVAL_DEPARTMENT("custbody_ma_approval_department"), 
	AUTO_APPROVE("custbody_ma_auto_approve"),
	TRAN_ID("tranid"),
	ENTITY("entity"),
	ACCOUNT("account"),
	TRAN_DATE("tranDate"),
	BILL_ADDRESS_LIST("billaddresslist"),
	ITEM_LIST("itemlist"),	
	ITEM("item"),
	ITEM_QUANTITY("quantity"),
	ITEM_DESCRIPTION("Description"),
	ITEM_RATE("rate"),
	ITEM_DEPARTMENT("department"),
	ITEM_CLASSIFICATION("_class"),
	ITEM_LOCATION("location"),
	ITEM_UNIT("cseg_mafs_unit"),
	MAIN_PO("custbody_ma_main_vehicle"),
	UPDATE_CONTROL_CODE("custbody_ma_update_control_code"),
	IS_CLOSED("isClosed"),
	STATUS("status");
	
	private final String scriptId;

	private PurchaseOrderFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public static PurchaseOrderFieldEnum getField(String scriptId) {
		for(PurchaseOrderFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(PurchaseOrderFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
		      retVal = true;
		      break;
			}
		}		
		return retVal;
	}
	
	public String getScriptId() {
		return scriptId;
	}
	
}

package com.mikealbert.accounting.processor.enumeration;

public enum VendorBillFieldEnum {
	INTERNAL_ID("internalId"),
	EXTERNAL_ID("externalId"),
	SUBSIDIARY("subsidiary"),
	TRAN_ID("tranid"),
	ENTITY("entity"),
	ACCOUNT("account"),
	TRAN_DATE("tranDate"),
	MEMO("memo"),
	APPROVAL_DEPARTMENT("custbody_ma_approval_department"),
	AUTO_APPROVE("custbody_ma_auto_approve"),
	BILL_ADDRESS_LIST("billaddresslist"),
	ITEM_LIST("itemlist"),	
	ITEM("item"),
	ITEM_QUANTITY("quantity"),
	ITEM_DESCRIPTION("description"),
	ITEM_RATE("rate"),
	ITEM_DEPARTMENT("department"),
	ITEM_CLASSIFICATION("_class"),
	ITEM_LOCATION("location"),
	ITEM_UNIT("cseg_mafs_unit"),
	ITEM_ASSET_TYPE("custcol_fa_asset_type"),
	MAIN_PO("custbody_ma_main_vehicle"),
	UPDATE_CONTROL_CODE("custbody_ma_update_control_code"),
	USER_TOTAL("usertotal");	
	
	private final String scriptId;
	
	private VendorBillFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public static VendorBillFieldEnum getField(String scriptId) {
		for(VendorBillFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Vendor Bill Field: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(VendorBillFieldEnum field : values()) {
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

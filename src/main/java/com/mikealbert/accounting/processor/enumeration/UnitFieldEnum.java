package com.mikealbert.accounting.processor.enumeration;

public enum UnitFieldEnum implements FieldEnum {
	INTERNAL_ID("internalId", "", ""),
	EXTERNAL_ID("externalId", "", ""),
	NAME("name", "", ""),
	VIN("custrecord_mafs_vin", "", ""),
	YEAR("custrecord_mafs_year", "", ""),
	MAKE("custrecord_mafs_make", "", ""),
	MODEL("custrecord_mafs_model", "", ""),
	MODEL_TYPE("custrecord_mafs_model_type", "", ""),
	FUEL_TYPE("custrecord_mafs_fuel_type", "", ""),
	GVR("custrecord_mafs_gvr", "", ""),
	HORSE_POWER("custrecord_mafs_horsepower", "", ""),
	MSRP("custrecord_mafs_msrp", "", ""),
	NEW_USED("custrecord_mafs_new_used", "", ""),
	CONTRACT_BOOK_VALUE("custrecord_ma_abs_cbv", "", ""),
	EQUIPMENT_CLASSFICATION("custrecord_ma_abs_nonvehicle_class", "", ""),
	PLB_TYPE("custrecord_abs_plbstatus", "", "");
		
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private UnitFieldEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static UnitFieldEnum getField(String scriptId) {
		for(UnitFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(UnitFieldEnum field : values()) {
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
	
	public String getInternalId() {
		return internalId;
	}	
	
	public String getRecordTypeId() {
		return recordTypeId;
	}	
}

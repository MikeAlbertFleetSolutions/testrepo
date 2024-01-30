package com.mikealbert.accounting.processor.enumeration;

public enum CustomSegmentEnum {
	UNIT_NO("cseg_mafs_unit", "", ""),
	CLASS("custrecord_fa_ast_class", "", "");
	
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private CustomSegmentEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static CustomSegmentEnum getCustomSegment(String scriptId) {
		for(CustomSegmentEnum customSegment : values()) {
			if(customSegment.getScriptId().equals(scriptId)) {
				return customSegment;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isCustomSegment(String scriptId) {
		boolean retVal = false;
		for(CustomSegmentEnum customSegment : values()) {
			if(customSegment.getScriptId().equals(scriptId)) {
		      retVal = true;
		      break;
			}
		}		
		return retVal;
	}
	
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

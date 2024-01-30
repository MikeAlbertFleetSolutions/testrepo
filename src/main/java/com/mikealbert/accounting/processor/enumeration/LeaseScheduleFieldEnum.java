package com.mikealbert.accounting.processor.enumeration;

public enum LeaseScheduleFieldEnum {
	LEASE("custrecord_lma_lse_lease", "", ""),
	PERIOD_START_DATE("custrecord_lma_lse_period_start_date", "", ""),
	PAYMENT("custrecord_lma_lse_payment", "", "");

	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private LeaseScheduleFieldEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static LeaseScheduleFieldEnum getField(String scriptId) {
		for(LeaseScheduleFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(LeaseScheduleFieldEnum field : values()) {
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
	
	public String getInternalId() {
		return internalId;
	}	
	
	public String getRecordTypeId() {
		return recordTypeId;
	}	
}

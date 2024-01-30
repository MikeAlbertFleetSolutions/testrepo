package com.mikealbert.accounting.processor.enumeration;

public enum CustomRecordEnum {
	ASSET("customrecord_fa_asset", "", "244"),
	ASSET_DEPRECIATION_METHOD("customrecord_fa_asset_dep_method", "", "248"),
	ASSET_DISPOSAL("customrecord_fa_disposal", "", "261"),
	ASSET_DISPOSAL_STATUS("custrecord_fa_disposal_status", "", "2444"),	
	ASSET_REVALUATION("customrecord_fa_asset_revaluation", "", "246"),
	ASSET_TYPE("customrecord_fa_asset_type", "", "247"),	
	BANK_DETAIL("customrecord_2663_entity_bank_details", "", ""),
	DRIVER_UNIT_HISTORY("customrecordma_driver_unit_history", "", ""),
	INVOICE_DESCRIPTION("customrecord_ma_inv_descbimap", "", ""),
	LEASE("customrecord_lma_lease", "", "200"),
	LEASE_MODIFICATION("customrecord_lma_lease_modification", "", "198"),	
	LEASE_SCHEDULE("customrecord_lma_lse_schedule_line", "", "201"),	
	LEASE_TYPE("customrecord_lma_lease_type", "", "205"),
	PAYMENT("customrecord_lma_lease_payment", "20", "199"),	
	UNIT("customrecord_cseg_mafs_unit", "", "147");
	
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private CustomRecordEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static CustomRecordEnum getLeaseField(String scriptId) {
		for(CustomRecordEnum leaseField : values()) {
			if(leaseField.getScriptId().equals(scriptId)) {
				return leaseField;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id " + scriptId);
	}
	
	public static boolean isLeaseField(String scriptId) {
		boolean retVal = false;
		for(CustomRecordEnum leaseField : values()) {
			if(leaseField.getScriptId().equals(scriptId)) {
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

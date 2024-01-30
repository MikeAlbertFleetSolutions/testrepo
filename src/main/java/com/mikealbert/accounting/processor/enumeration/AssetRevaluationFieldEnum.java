package com.mikealbert.accounting.processor.enumeration;

public enum AssetRevaluationFieldEnum {

	INTERNAL_ID("internalId", "", ""),
	EXTERNAL_ID("externalId", "", ""),
	STATUS("custrecord_fa_rvl_status", "", ""),
	TYPE("custrecord_fa_rvl_type", "", ""),
	FORM_ID("formid", "", ""),
	ASSET("custrecord_fa_rvl_asset", "", ""),
	EFFECTIVE_DATE("custrecord_fa_rvl_effective_date", "", ""),
	EFFECTIVE_TO_DATE("custrecord_fa_rvl_end_of_life_date", "", ""),
	IN_SERVICE_DATE("custrecord_fa_rvl_in_service_date", "", ""),
	DEPRECIATION_METHOD("custrecord_fa_rvl_depr_method", "", ""),
	RESIDUAL_VALUE("custrecord_fa_rvl_residual_value", "", ""),
	REMAINING_USEFUL_LIFE("custrecord_fa_rvl_rm_useful_life", "", ""),
	REVALUE_USEFUL_LIFE("custrecord_fa_rvl_useful_life", "", "");
		
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private AssetRevaluationFieldEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
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

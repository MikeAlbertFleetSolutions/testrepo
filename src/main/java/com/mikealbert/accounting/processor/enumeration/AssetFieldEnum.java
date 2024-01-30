package com.mikealbert.accounting.processor.enumeration;

public enum AssetFieldEnum {

	INTERNAL_ID("internalId", "", ""),
	EXTERNAL_ID("externalId", "", ""),
	VIN("custrecord_ma_vin", "", ""),
	STATUS("custrecord_fa_ast_status", "", ""),
	IN_SERVICE_DATE("custrecord_fa_ast_in_service_date", "", ""),
	ACQUISATION_DATE("custrecord_fa_ast_acquisition_date", "", ""),
	USEFUL_LIFE("custrecord_fa_ast_useful_life", "", ""),
	REMAINING_USEFUL_LIFE("custrecord_fa_ast_remaining_useful_life", "", ""),
	RESIDUAL_VALUE("custrecord_fa_ast_resid_value_estimate", "", ""),
	EXTENDED_LIFE_RESIDUAL_VALUE_ESTIMATE("custrecord_fa_ast_extended_resid_value", "", ""),
	PENDING_LIVE("custrecord_ma_pending_live", "", ""),
	LOCKED_TRANS("custrecord_fa_ast_locked", "", ""),
	DEPARTMENT("custrecord_fa_ast_department", "", ""),
	DEPARTMENT_NEW("custrecord_ma_new_department", "", ""),
	CLASS("custrecord_fa_ast_class", "", ""),
	BUSINESS_UNIT_NEW("custrecord_ma_new_business_unit", "", ""),
	PARRENT_ASSET("custrecord_fa_ast_parent", "", ""),
	TYPE("custrecord_fa_ast_type", "", ""),
	END_OF_LIFE_DATE("custrecord_fa_ast_end_date", "", ""),
	ALT_NAME("altname", "", ""),
	DEPRECIATION_METHOD("custrecord_fa_ast_depreciation_method", "", ""),
	CAPITALIZED_VALUE("custrecord_fa_ast_orig_capitalized_value", "", ""),
	SUBSIDARY("custrecord_fa_ast_subsidiary","", ""),
	UNIT("custrecord_faseg_mafs_unit", "", ""),
	TERMINATION_DATE("custrecord_ma_termination_date","",""),
	NEW_ASSET_TYPE("custrecord_ma_new_asset_type", "", ""),
	UPDATE_CONTROL_CODE("custrecord_ma_ng_update_control_code", "", ""),
	INVOICE_NO("custrecord_ma_invoice_number", "", ""),
	ASSET_TYPE("custrecord_fa_ast_type", "", ""),
	DISPOSAL_FLAG("custrecord_ma_disposal_flag", "", ""),
	DISPOSAL_PROCEEDS("custrecord_ma_disposal_proceeds", "", ""),
	DISPOSAL_DATE("custrecord_ma_disposal_date", "", "");
		
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private AssetFieldEnum(String scriptId, String internalId, String recordTypeId) {
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

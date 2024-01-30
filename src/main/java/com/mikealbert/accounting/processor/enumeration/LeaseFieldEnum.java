package com.mikealbert.accounting.processor.enumeration;

public enum LeaseFieldEnum implements FieldEnum {
	INTERNAL_ID("internalId", "", ""),
	EXTERNAL_ID("externalId", "", ""),
	NAME("name", "", ""),
	ALT_NAME("altName", "", ""),	
	LEASE_TYPE("custrecord_lma_ls_type", "", ""),
	CLASSIFICATION("custrecord_lma_ls_classification", "", ""),
	SUBSIDIARY("custrecord_lma_ls_subsidiary", "", ""),
	CUSTOMER("custrecord_lma_ls_customer", "", ""),
	CUSTOMER_CAP_COST("custrecord_mafs_client_cap_cost", "", ""),	
	CURRENCY("custrecord_lma_ls_currency", "1", ""),
	COMMENCEMENT_DATE("custrecord_lma_ls_commencement_date", "", ""),
	IN_SERVICE_DATE("custrecord_ma_lsr_inservice_date", "", ""),
	WILLOW_END_DATE("custrecord_mafs_willow_end_date", "", ""),
	TERM("custrecord_lma_ls_term", "", ""),
	ASSET_FAIR_VALUE("custrecord_lma_ls_fair_asset_value", "", ""),
	ASSET_CARRING_COST("custrecord_lma_ls_carrying_costs", "", ""),
	PREPAYMENT("custrecord_lma_ls_prepayment", "", ""),
	LESEE_RESIDUAL("custrecord_lma_ls_lease_res_guarantee", "", ""),
	THIRD_PARTY_RESIDUAL("custrecord_lma_ls_res_value_third_party", "", ""),
	TOTAL_RESIDUAL("custrecord_lma_ls_res_value_total_est", "", ""),
	VARIABLE_RATE_LEASE("custrecord_lma_ls_variable_rate_lease", "", ""),
	VARIABLE_RATE_INDEX("custrecord_lma_ls_var_rate_index", "", ""), 
	DEPOSIT_REFUNABLE_AMOUNT("custrecord_lma_ls_deposit_amount", "", ""),
	COLLECTIBILITY_PROBABLE("custrecord_lma_ls_collectibility_prob", "", ""),
	LEASE_TRANSFER_OWNERSHIP("custrecord_lma_ls_lease_transfer_owner", "", ""),
	PURCHASE_OPTION("custrecord_lma_ls_purchase_option", "", ""),
	UNDERLYING_ASSET("custrecord_lma_ls_underlying_asset", "", ""),
	CLASS_BUSINESS_UNIT("custrecord_lma_ls_class", "1", ""),
	CATEGORY("cseg_mafs_category", "27", ""),
	AUTO_RENEWAL("custrecord_lma_ls_is_autorenewal", "", ""),
	AUTO_RENEWAL_TERM("custrecord_lma_ls_auto_renew_term", "", ""),	
	LEASE_PARENT("custrecord_lma_lpa_lease", "", ""),
	LEASE_INTERNAL_ID("internalId", "", ""),
	LEASE_EXTERNAL_ID("externalId", "", ""),	
	LEASE_PAYMENT_PARENT("custrecord_lma_ls_parent_lease", "", ""),	
	LEASE_PAYMENT_AMOUNT("custrecord_lma_lpa_payment_amount", "", ""),
	LEASE_PAYMENT_DATE("custrecord_lma_lpa_effective_date", "", ""),
	TERMINATION_REASON("custrecord_lma_termination_reason", "", ""),
	TERMINATION_NOTE("custrecord_lma_termination_note", "", ""),
	TERMINATION_CASH("custrecord_lma_term_cash_received", "", ""),
	TERMINATION_ASSET_VALUE("custrecord_lma_term_asset_value", "", ""),
	TERMINATION_FLAG("custrecord_ma_termination_flag", "", ""),  	
	LEASE_MODIFICATION_EFFECTIVE_DATE("custrecord_lma_mod_effective_date", "", ""), 	
	LEASE_MODIFICATION("custrecord_lma_ls_recent_modification", "", ""),
	UNIT("cseg_mafs_unit", "", ""),
	END_DATE("custrecord_lma_ls_end_date", "", ""),
	INTEREST_RATE("custrecord_ma_abs_interestrate", "", ""),
	CBV_IMPACT("custrecord_ma_abs_cbv_impact_pending", "", ""),
	IS_AMENDMENT("custrecord_ma_abs_is_amendment", "", ""),
	DEPRECIATION_RATE("custrecord_ma_abs_depreciation_rate", "", ""), 
	ACTUAL_END_DATE("custrecord_ma_abs_actual_enddate", "", "");
	
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private LeaseFieldEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static LeaseFieldEnum getField(String scriptId) {
		for(LeaseFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(LeaseFieldEnum field : values()) {
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

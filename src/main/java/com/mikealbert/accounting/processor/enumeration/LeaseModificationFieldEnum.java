package com.mikealbert.accounting.processor.enumeration;

public enum LeaseModificationFieldEnum {
	ASSET_CARRING_COST("custrecord_lma_mod_carry_costs", "", ""),
	ASSET_FAIR_VALUE("custrecord_lma_mod_fair_value", "", ""),
	CUSTOMER_CAP_COST("custrecord_ma_abs_client_cap_cost", "", ""),
	DEPRECIATION_RATE("custrecord_ma_abs_new_depreciation_rate", "", ""),	
	ECONOMIC_LIFE("custrecord_lma_mod_economic_life", "", ""),
	EFFECTIVE_DATE("custrecord_lma_mod_effective_date", "", ""),	
	LEASE_PARENT("custrecord_lma_mod_lease", "", ""),
	LESEE_RESIDUAL("custrecord_lma_mod_lessee_res_value", "", ""),
	STATUS(" custrecord_lma_mod_status", "2", ""),
	TERM("custrecord_lma_mod_term", "", ""),
	THIRD_PARTY_RESIDUAL("custrecord_lma_mod_tp_res_value", "", ""),
	TO_BE_PROCESSED("custrecord_lma_mod_to_be_processed", "", ""),	
	TOTAL_RESIDUAL("custrecord_lma_mod_est_res_value", "", ""), 
	USEFUL_LIFE("custrecord_lma_mod_useful_life", "", "");
	
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private LeaseModificationFieldEnum(String scriptId, String internalId, String recordTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.recordTypeId = recordTypeId;
	}
	
	public static LeaseModificationFieldEnum getLeaseField(String scriptId) {
		for(LeaseModificationFieldEnum leaseField : values()) {
			if(leaseField.getScriptId().equals(scriptId)) {
				return leaseField;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isLeaseField(String scriptId) {
		boolean retVal = false;
		for(LeaseModificationFieldEnum leaseField : values()) {
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

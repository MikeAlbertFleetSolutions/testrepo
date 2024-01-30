package com.mikealbert.accounting.processor.enumeration;

public enum CustomListEnum {

	ASSET_STATUS("customlist_fa_asset_status", "", "234"),
	ASSET_REVALUATION_TYPE("customlist_fa_asset_rev_type", "", "236"),
	ASSET_PROPOSAL_STATUS("customlist_fa_proposal_status", "", "259"),
	COLLECTION_STATUS("customlist586", "", ""),
	CREDIT_MANAGEMENT_TYPE("customlist_ma_cr_mgmt_type", "", ""),
	CREDIT_STATUS("customlist_ma_cr_status", "", ""),
	EQUIPMENT_CLASSIFICATION("customlist_nonvehicle_class", "", ""),
	MA_TYPE("customlist_ma_type_list", "", ""),
	PLB_TYPE("customlist_abs_plbstatus", "", ""),
	SKIP_APPROVAL("customlist_skip_approval", "", "");
	
	private final String scriptId;
	private final String internalId;
	private final String listTypeId;
	
	CustomListEnum(String scriptId, String internalId, String listTypeId) {
		this.scriptId = scriptId;
		this.internalId = internalId;
		this.listTypeId = listTypeId;
	}
	
	public static CustomListEnum getCustomList(String scriptId) {
		for(CustomListEnum customList : values()) {
			if(customList.getScriptId().equals(scriptId)) {
				return customList;
			}
		}
		throw new IllegalArgumentException("Unknown Script Id: " + scriptId);
	}
	
	public static boolean isCustomList(String scriptId) {
		boolean retVal = false;
		for(CustomListEnum customList : values()) {
			if(customList.getScriptId().equals(scriptId)) {
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

	public String getListTypeId() {
		return listTypeId;
	}

}

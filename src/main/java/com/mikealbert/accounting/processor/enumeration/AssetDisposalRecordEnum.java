package com.mikealbert.accounting.processor.enumeration;

public enum AssetDisposalRecordEnum {

	DISPOSAL_STATUS("custrecord_fa_disposal_status", "", ""),
	ASSET("custrecord_fa_disposal_asset", "", "");
	
	private final String scriptId;
	private final String internalId;
	private final String recordTypeId;

	private AssetDisposalRecordEnum(String scriptId, String internalId, String recordTypeId) {
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

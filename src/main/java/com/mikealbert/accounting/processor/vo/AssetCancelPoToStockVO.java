package com.mikealbert.accounting.processor.vo;

public class AssetCancelPoToStockVO extends AssetBaseVO {
	
	private String assetType;

	private Boolean pendingLive;
	
	private String updateControlCode;

	public String getAssetType() {
		return assetType;
	}

	public AssetCancelPoToStockVO setAssetType(String assetType) {
		this.assetType = assetType;
		return this;
	}

	public Boolean isPendingLive() {
		return pendingLive;
	}

	public AssetCancelPoToStockVO setPendingLive(Boolean pendingLive) {
		this.pendingLive = pendingLive;
		return this;
	}

	public String getUpdateControlCode() {
		return updateControlCode;
	}

	public void setUpdateControlCode(String updateControlCode) {
		this.updateControlCode = updateControlCode;
	}
	

}

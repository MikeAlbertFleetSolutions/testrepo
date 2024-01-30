package com.mikealbert.accounting.processor.vo;

public abstract class AssetBaseVO{

	protected Long assetId;
		
	public AssetBaseVO setAssetId(Long assetId) {
		this.assetId = assetId;
		return this;
	}

	public Long getAssetId() {
		return assetId;
	}

}

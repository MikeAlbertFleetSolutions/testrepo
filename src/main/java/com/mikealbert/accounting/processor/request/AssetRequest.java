package com.mikealbert.accounting.processor.request;

import javax.validation.constraints.NotNull;

public class AssetRequest {	
	
	@NotNull(message = "AssetId is required")
	private Long assetId;
		
	public AssetRequest() {	}

	public Long getAssetId() {
		return assetId;
	}

	public AssetRequest setAssetId(Long assetId) {
		this.assetId = assetId;
		return this;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssetRequest other = (AssetRequest) obj;
		if (assetId == null) {
			if (other.assetId != null)
				return false;
		} else if (!assetId.equals(other.assetId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AssetRequest [assetId=" + assetId + "]";
	}
	
}

package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

public class CreditLineVO extends PayableTransactionLineVO<CreditLineVO> {	
	private static final long serialVersionUID = 6027534298275860555L;
	
	private boolean rebillableClientFine;
	
	private AssetTypeEnum assetType;
	
	private ProductEnum productCode;
		
	@JsonBackReference 
	private CreditVO header;

	private Long drvId;
	
	private String client;
	
	private boolean rebillableLicenseFee;
	
	private String externalAssetType;
	
	public CreditLineVO() {}
	public CreditLineVO(CreditVO credit) {
		this.header = credit;
	}
	
	public boolean isRebillableClientFine() {
		return rebillableClientFine;
	}

	public void setRebillableClientFine(boolean rebillableClientFine) {
		this.rebillableClientFine = rebillableClientFine;
	}

	public AssetTypeEnum getAssetType() {
		return assetType;
	}

	public CreditLineVO setAssetType(AssetTypeEnum assetType) {
		this.assetType = assetType;
		return this;
	}

	public ProductEnum getProductCode() {
		return productCode;
	}
	
	public void setProductCode(ProductEnum productCode) {
		this.productCode = productCode;
	}
	
	@Override
	public CreditVO getHeader() {
		return header;
	}
	
	public CreditLineVO setHeader(CreditVO header) {
		this.header = header;
		return this;
	}
	
	public Long getDrvId() {
		return drvId;
	}
	
	public CreditLineVO setDrvId(Long drvId) {
		this.drvId = drvId;
		return this;
	}
	
	public String getClient() {
		return client;
	}
	
	public CreditLineVO setClient(String client) {
		this.client = client;
		return this;
	}
	
	public boolean isRebillableLicenseFee() {
		return rebillableLicenseFee;
	}
	
	public CreditLineVO setRebillableLicenseFee(boolean rebillableLicenseFee) {
		this.rebillableLicenseFee = rebillableLicenseFee;
		return this;
	}
	
	public String getExternalAssetType() {
		return externalAssetType;
	}
	
	public void setExternalAssetType(String externalAssetType) {
		this.externalAssetType = externalAssetType;
	}
	@Override
	public String toString() {
		return "CreditLineVO [rebillableClientFine=" + rebillableClientFine + ", assetType=" + assetType
				+ ", productCode=" + productCode + ", drvId=" + drvId + ", client=" + client + ", rebillableLicenseFee="
				+ rebillableLicenseFee + ", externalAssetType=" + externalAssetType + ", toString()=" + super.toString()
				+ "]";
	}
		
}

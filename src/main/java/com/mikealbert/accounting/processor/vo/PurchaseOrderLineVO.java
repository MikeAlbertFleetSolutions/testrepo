package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

public class PurchaseOrderLineVO extends PayableTransactionLineVO<PurchaseOrderLineVO> {
	private static final long serialVersionUID = -3961562146487421909L; 
		
	private AssetTypeEnum assetType;
	
	private ProductEnum productCode;
	
	private Long drvId;
	
	private String client;	
		
	@JsonBackReference 
	private PurchaseOrderVO header;
	
	public PurchaseOrderLineVO() {
		super();
	}
	public PurchaseOrderLineVO(PurchaseOrderVO header) {
		super();
		this.header = header;
	}

	public AssetTypeEnum getAssetType() {
		return assetType;
	}

	public PurchaseOrderLineVO setAssetType(AssetTypeEnum assetType) {
		this.assetType = assetType;
		return this;
	}

	public ProductEnum getProductCode() {
		return productCode;
	}

	public PurchaseOrderLineVO setProductCode(ProductEnum productCode) {
		this.productCode = productCode;
		return this;
	}

	public Long getDrvId() {
		return drvId;
	}
	public PurchaseOrderLineVO setDrvId(Long drvId) {
		this.drvId = drvId;
		return this;
	}
	public String getClient() {
		return client;
	}
	public PurchaseOrderLineVO setClient(String client) {
		this.client = client;
		return this;
	}

	public PurchaseOrderLineVO setPurchaseOrder(PurchaseOrderVO header) {
		this.header = header;
		return this;
	}
	
	@Override
	public PurchaseOrderVO getHeader() {
		return header;
	}
	
	@Override
	public String toString() {
		return "PurchaseOrderLineVO [assetType=" + assetType + ", productCode=" + productCode + ", drvId=" + drvId
				+ ", client=" + client + ", toString()=" + super.toString() + "]";
	}
			
}

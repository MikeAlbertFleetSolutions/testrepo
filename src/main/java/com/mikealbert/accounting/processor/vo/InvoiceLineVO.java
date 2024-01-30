package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

public class InvoiceLineVO extends PayableTransactionLineVO<InvoiceLineVO> {
	private static final long serialVersionUID = 2417829804379154427L;
	
	private boolean rebillableClientFine;
		
	private AssetTypeEnum assetType;
	
	private ProductEnum productCode;
		
	@JsonBackReference 
	private InvoiceVO header;

	private Long drvId;
	
	private String client;
	
	private boolean rebillableLicenseFee;
	
	private String externalAssetType;
		
	public InvoiceLineVO() {}
	public InvoiceLineVO(InvoiceVO invoice) {
		this.header = invoice;
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

	public InvoiceLineVO setAssetType(AssetTypeEnum assetType) {
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
	public InvoiceVO getHeader() {
		return header;
	}
	
	public InvoiceLineVO setHeader(InvoiceVO header) {
		this.header = header;
		return this;
	}
	
	public Long getDrvId() {
		return drvId;
	}
	
	public InvoiceLineVO setDrvId(Long drvId) {
		this.drvId = drvId;
		return this;
	}
	
	public String getClient() {
		return client;
	}
	
	public InvoiceLineVO setClient(String client) {
		this.client = client;
		return this;
	}
	
	public boolean isRebillableLicenseFee() {
		return rebillableLicenseFee;
	}
	
	public InvoiceLineVO setRebillableLicenseFee(boolean rebillableLicenseFee) {
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
		return "InvoiceLineVO [assetType=" + assetType + ", client=" + client + ", drvId=" + drvId
				+ ", externalAssetType=" + externalAssetType + ", productCode=" + productCode
				+ ", rebillableClientFine=" + rebillableClientFine + ", rebillableLicenseFee=" + rebillableLicenseFee
				+ ", toString()=" + super.toString() + "]";
	}
	
}

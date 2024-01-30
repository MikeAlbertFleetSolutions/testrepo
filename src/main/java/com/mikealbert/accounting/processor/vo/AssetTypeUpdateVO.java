package com.mikealbert.accounting.processor.vo;

import com.mikealbert.constant.accounting.enumeration.AssetRevalueTypeUpdateEnum;

public class AssetTypeUpdateVO extends AssetBaseVO {
	
	private String newType;
	
	private String productCode;
	
	private AssetRevalueTypeUpdateEnum updateContext;
	
	private String department;
	
	private String businessUnit;
	
	public String getNewType() {
		return newType;
	}

	public AssetTypeUpdateVO setNewType(String newType) {
		this.newType = newType;
		return this;
	}

	public AssetTypeUpdateVO setNewType(String newType, AssetRevalueTypeUpdateEnum updateContext) {
		this.newType = newType;
		this.updateContext = updateContext;
		return this;
	}

	public AssetRevalueTypeUpdateEnum getUpdateContext() {
		return updateContext;
	}

	public AssetTypeUpdateVO setUpdateContext(AssetRevalueTypeUpdateEnum updateContext) {
		this.updateContext = updateContext;
		return this;
	}

	public String getProductCode() {
		return productCode;
	}

	public AssetTypeUpdateVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public AssetTypeUpdateVO setDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public AssetTypeUpdateVO setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
		return this;
	}

}

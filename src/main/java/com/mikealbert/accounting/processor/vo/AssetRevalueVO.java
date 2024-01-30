package com.mikealbert.accounting.processor.vo;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.constant.accounting.enumeration.AssetRevalueTypeUpdateEnum;
import com.mikealbert.util.data.DateUtil;

public class AssetRevalueVO extends AssetBaseVO {
	
	private AssetRevalueTypeUpdateEnum revalueContext;
	
	private String newAssetType;
	
	private String externalId;
	
	private String statusName;
	
	private String revaluationType;
	
	private String customForm;
	
	private Date effectiveDate;
	
	private Date effectiveTo;
		
	private String depreciationMethodName;
	
	private String department;
	
	private String businessUnit;
	
	private Long fmsId;
	
	private BigDecimal residualValueEstimate;
	
	private Long remainingUsefulLife;
	
	private Long revalueUsefulLife;

	private String productCode;
	
	private Date startDate; 
	

	public AssetRevalueTypeUpdateEnum getRevalueContext() {
		return revalueContext;
	}

	public AssetRevalueVO setRevalueContext(AssetRevalueTypeUpdateEnum revalueContext) {
		this.revalueContext = revalueContext;
		return this;
	}

	public String getNewAssetType() {
		return newAssetType;
	}

	public AssetRevalueVO setNewAssetType(String newAssetType) {
		this.newAssetType = newAssetType;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public AssetRevalueVO setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public String getStatusName() {
		return statusName;
	}

	public AssetRevalueVO setStatusName(String statusName) {
		this.statusName = statusName;
		return this;
	}

	public String getRevaluationType() {
		return revaluationType;
	}

	public AssetRevalueVO setRevaluationType(String revaluationType) {
		this.revaluationType = revaluationType;
		return this;
	}

	public String getCustomForm() {
		return customForm;
	}

	public AssetRevalueVO setCustomForm(String customForm) {
		this.customForm = customForm;
		return this;
	}

	public Date getEffectiveDate() {
		return DateUtil.clone(effectiveDate);
	}

	public AssetRevalueVO setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = DateUtil.clone(effectiveDate);
		return this;
	}

	public Date getEffectiveTo() {
		return DateUtil.clone(effectiveTo);
	}

	public AssetRevalueVO setEffectiveTo(Date effectiveTo) {
		this.effectiveTo = DateUtil.clone(effectiveTo);
		return this;
	}

	public String getDepreciationMethodName() {
		return depreciationMethodName;
	}

	public AssetRevalueVO setDepreciationMethodName(String depreciationMethodName) {
		this.depreciationMethodName = depreciationMethodName;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public AssetRevalueVO setDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public AssetRevalueVO setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
		return this;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public AssetRevalueVO setFmsId(Long fmsId) {
		this.fmsId = fmsId;
		return this;
	}

	public BigDecimal getResidualValueEstimate() {
		return residualValueEstimate;
	}

	public AssetRevalueVO setResidualValueEstimate(BigDecimal residualValueEstimate) {
		this.residualValueEstimate = residualValueEstimate;
		return this;
	}

	public Long getRemainingUsefulLife() {
		return remainingUsefulLife;
	}

	public AssetRevalueVO setRemainingUsefulLife(Long remainingUsefulLife) {
		this.remainingUsefulLife = remainingUsefulLife;
		return this;
	}
	
	public Long getRevalueUsefulLife() {
		return revalueUsefulLife;
	}

	public AssetRevalueVO setRevalueUsefulLife(Long revalueUsefulLife) {
		this.revalueUsefulLife = revalueUsefulLife;
		return this;
	}

	@Transient
	public AssetTypeUpdateVO getAssetTypeUpdateVO() {
		AssetTypeUpdateVO assetTypeUpdateVO = new AssetTypeUpdateVO();
		
		assetTypeUpdateVO.setAssetId(this.getAssetId());
		assetTypeUpdateVO
			.setNewType(this.getNewAssetType())
			.setUpdateContext(this.getRevalueContext())
			.setProductCode(this.productCode)
			.setBusinessUnit(this.businessUnit)
			.setDepartment(this.department);
		
		
		return assetTypeUpdateVO;
	}

	public String getProductCode() {
		return productCode;
	}

	public AssetRevalueVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public Date getStartDate() {
		return DateUtil.clone(startDate);
	}

	public AssetRevalueVO setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
		return this;
	}

}
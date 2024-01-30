package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class AssetPlaceInServiceVO extends AssetBaseVO {
	
	private String addOnSeq;
	
	private String type;
	
	private String vin;

	private Long fleetId;
	
	private String unitNo;
	
	private Date startDate;
	
	private Date endDate;

	private Long useFulLife;
	
	private String productCode;
	
	private String productType;
	
	private BigDecimal initialValue;

	private BigDecimal residualValue;
	
	private BigDecimal depreciationFactor;
	
	private String department;
	
	private String businessUnit;
	
	private Long parentAssetId;
	
	private Long qmdId;

	public String getAddOnSeq() {
		return addOnSeq;
	}

	public AssetPlaceInServiceVO setAddOnSeq(String addOnSeq) {
		this.addOnSeq = addOnSeq;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getInitialValue() {
		return initialValue;
	}

	public AssetPlaceInServiceVO setInitialValue(BigDecimal initialValue) {
		this.initialValue = initialValue;
		return this;
	}

	public BigDecimal getResidualValue() {
		return residualValue;
	}

	public AssetPlaceInServiceVO setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
		return this;
	}

	public String getVin() {
		return vin;
	}

	public AssetPlaceInServiceVO setVin(String vin) {
		this.vin = vin;
		return this;
	}

	public Long getFleetId() {
		return fleetId;
	}

	public AssetPlaceInServiceVO setFleetId(Long fleetId) {
		this.fleetId = fleetId;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public AssetPlaceInServiceVO setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public Date getStartDate() {
		return DateUtil.clone(startDate);
	}

	public AssetPlaceInServiceVO setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
		return this;
	}

	public Date getEndDate() {
		return DateUtil.clone(endDate);
	}

	public AssetPlaceInServiceVO setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
		return this;
	}

	public Long getUseFulLife() {
		return useFulLife;
	}

	public AssetPlaceInServiceVO setUseFulLife(Long useFulLife) {
		this.useFulLife = useFulLife;
		return this;
	}

	public String getProductCode() {
		return productCode;
	}

	public AssetPlaceInServiceVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getProductType() {
		return productType;
	}

	public AssetPlaceInServiceVO setProductType(String productType) {
		this.productType = productType;
		return this;
	}

	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}

	public AssetPlaceInServiceVO setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public AssetPlaceInServiceVO setDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public AssetPlaceInServiceVO setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
		return this;
	}

	public Long getParentAssetId() {
		return parentAssetId;
	}

	public AssetPlaceInServiceVO setParentAssetId(Long parentAssetId) {
		this.parentAssetId = parentAssetId;
		return this;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public AssetPlaceInServiceVO setQmdId(Long qmdId) {
		this.qmdId = qmdId;
		return this;
	}
	
}

package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class AssetCreateVO extends AssetBaseVO {
		
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
	
	private String depreciationMethodName;
	
	private String statusName;
	
	private Long qmdId;

	private Long invoiceApDocId;
	
	private Long invoiceArDocId;
	
	private Long invoiceArLineId;
	
	private String description;
	
	private String cId;
	
	private String updateControlCode;
	
	private String invoiceNo;
	
	public String getAddOnSeq() {
		return addOnSeq;
	}

	public AssetCreateVO setAddOnSeq(String addOnSeq) {
		this.addOnSeq = addOnSeq;
		return this;
	}

	public String getType() {
		return type;
	}

	public AssetCreateVO setType(String type) {
		this.type = type;
		return this;
	}

	public BigDecimal getInitialValue() {
		return initialValue;
	}

	public AssetCreateVO setInitialValue(BigDecimal initialValue) {
		this.initialValue = initialValue;
		return this;
	}

	public BigDecimal getResidualValue() {
		return residualValue;
	}

	public AssetCreateVO setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
		return this;
	}

	public String getVin() {
		return vin;
	}

	public AssetCreateVO setVin(String vin) {
		this.vin = vin;
		return this;
	}

	public Long getFleetId() {
		return fleetId;
	}

	public AssetCreateVO setFleetId(Long fleetId) {
		this.fleetId = fleetId;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public AssetCreateVO setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public Date getStartDate() {
		return DateUtil.clone(startDate);
	}

	public AssetCreateVO setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
		return this;
	}

	public Date getEndDate() {
		return DateUtil.clone(endDate);
	}

	public AssetCreateVO setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
		return this;
	}

	public Long getUseFulLife() {
		return useFulLife;
	}

	public AssetCreateVO setUseFulLife(Long useFulLife) {
		this.useFulLife = useFulLife;
		return this;
	}

	public String getProductCode() {
		return productCode;
	}

	public AssetCreateVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getProductType() {
		return productType;
	}

	public AssetCreateVO setProductType(String productType) {
		this.productType = productType;
		return this;
	}

	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}

	public AssetCreateVO setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
		return this;
	}

	public String getDepartment() {
		return department;
	}

	public AssetCreateVO setDepartment(String department) {
		this.department = department;
		return this;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public AssetCreateVO setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
		return this;
	}

	public Long getParentAssetId() {
		return parentAssetId;
	}

	public AssetCreateVO setParentAssetId(Long parentAssetId) {
		this.parentAssetId = parentAssetId;
		return this;
	}

	public String getDepreciationMethodName() {
		return depreciationMethodName;
	}

	public AssetCreateVO setDepreciationMethodName(String depreciationMethodName) {
		this.depreciationMethodName = depreciationMethodName;
		return this;
	}

	public String getStatusName() {
		return statusName;
	}

	public AssetCreateVO setStatusName(String statusName) {
		this.statusName = statusName;
		return this;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public AssetCreateVO setQmdId(Long qmdId) {
		this.qmdId = qmdId;
		return this;
	}

	public Long getInvoiceApDocId() {
		return invoiceApDocId;
	}

	public AssetCreateVO setInvoiceApDocId(Long invoiceApDocId) {
		this.invoiceApDocId = invoiceApDocId;
		return this;
	}

	public Long getInvoiceArDocId() {
		return invoiceArDocId;
	}

	public AssetCreateVO setInvoiceArDocId(Long invoiceArDocId) {
		this.invoiceArDocId = invoiceArDocId;
		return this;
	}

	public Long getInvoiceArLineId() {
		return invoiceArLineId;
	}

	public AssetCreateVO setInvoiceArLineId(Long invoiceArLineId) {
		this.invoiceArLineId = invoiceArLineId;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AssetCreateVO setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getcId() {
		return cId;
	}

	public AssetCreateVO setcId(String cId) {
		this.cId = cId;
		return this;
	}

	public String getUpdateControlCode() {
		return updateControlCode;
	}

	public AssetCreateVO setUpdateControlCode(String updateControlCode) {
		this.updateControlCode = updateControlCode;
		return this;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public AssetCreateVO setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
		return this;
	}

}

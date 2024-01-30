package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikealbert.util.data.DateUtil;

public class NgAssetVO {
		
	@JsonProperty(value = "ng_asset_id")
	private Long ngAssetId;
	
	@JsonProperty(value = "ng_asset_extid")
	private Long ngAssetExtid;
	
	@JsonProperty(value = "subsidiary_id")
	private Long subsidiaryId;
	
	@JsonProperty(value = "ng_asset_name")
	private String ngAssetName;
	
	@JsonProperty(value = "ng_asset_number")
	private String ngAssetNumber;

	@JsonProperty(value = "status_id")
	private Long statusId;
	
	@JsonProperty(value = "type_id")
	private Long typeId;
	
	@JsonProperty(value = "capitalized_asset_value_at_in")
	private BigDecimal capitalizedAssetValueAtIn;
	
	@JsonProperty(value = "accumulated_depreciation")
	private BigDecimal accumulatedDepreciation; 
	
	@JsonProperty(value = "residual_value_estimate")
	private BigDecimal residualValueEstimate; 
	
	@JsonProperty(value = "acquisition_date")
	private Date acquisitionDate;
	
	@JsonProperty(value = "inservice_date")
	private Date inserviceDate;
	
	@JsonProperty(value = "disposal_date")
	private Date disposalDate;
	
	@JsonProperty(value = "disposal_proceeds")
	private BigDecimal disposalProceeds;
	@JsonProperty(value = "source_transaction_id")
	private Long sourceTransactionId; 
	
	@JsonProperty(value = "unit_name")
	private String unitName;
	
	@JsonProperty(value = "useful_life_at_inservice")
	private Long usefulLifeAtInservice;

	@JsonProperty(value = "ng_asset_type_name")
	private String ngAssetTypeName;
	
	@JsonProperty(value = "invoice_no")
	private String invoiceNo;
	
	@JsonProperty(value = "main_vehicle")
	private String mainVehicle;
	
	private Boolean mainAsset;
	
	@JsonProperty(value = "update_control_code")
	private String updateControlCode;
	
	private Boolean newAsset;
	
	public Long getNgAssetId() {
		return ngAssetId;
	}

	public NgAssetVO setNgAssetId(Long ngAssetId) {
		this.ngAssetId = ngAssetId;
		return this;
	}

	public Long getNgAssetExtid() {
		return ngAssetExtid;
	}

	public NgAssetVO setNgAssetExtid(Long ngAssetExtid) {
		this.ngAssetExtid = ngAssetExtid;
		return this;
	}

	public Long getSubsidiaryId() {
		return subsidiaryId;
	}

	public NgAssetVO setSubsidiaryId(Long subsidiaryId) {
		this.subsidiaryId = subsidiaryId;
		return this;
	}

	public String getNgAssetName() {
		return ngAssetName;
	}

	public NgAssetVO setNgAssetName(String ngAssetName) {
		this.ngAssetName = ngAssetName;
		return this;
	}

	public String getNgAssetNumber() {
		return ngAssetNumber;
	}

	public void setNgAssetNumber(String ngAssetNumber) {
		this.ngAssetNumber = ngAssetNumber;
	}

	public Long getStatusId() {
		return statusId;
	}

	public NgAssetVO setStatusId(Long statusId) {
		this.statusId = statusId;
		return this;
	}

	public Long getTypeId() {
		return typeId;
	}

	public NgAssetVO setTypeId(Long typeId) {
		this.typeId = typeId;
		return this;
	}

	public BigDecimal getCapitalizedAssetValueAtIn() {
		return capitalizedAssetValueAtIn;
	}

	public NgAssetVO setCapitalizedAssetValueAtIn(BigDecimal capitalizedAssetValueAtIn) {
		this.capitalizedAssetValueAtIn = capitalizedAssetValueAtIn;
		return this;
	}

	public BigDecimal getAccumulatedDepreciation() {
		return accumulatedDepreciation;
	}

	public NgAssetVO setAccumulatedDepreciation(BigDecimal accumulatedDepreciation) {
		this.accumulatedDepreciation = accumulatedDepreciation;
		return this;
	}

	public BigDecimal getResidualValueEstimate() {
		return residualValueEstimate;
	}

	public NgAssetVO setResidualValueEstimate(BigDecimal residualValueEstimate) {
		this.residualValueEstimate = residualValueEstimate;
		return this;
	}

	public Date getAcquisitionDate() {
		return DateUtil.clone(acquisitionDate);
	}

	public NgAssetVO setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = DateUtil.clone(acquisitionDate);
		return this;
	}

	public Date getInserviceDate() {
		return DateUtil.clone(inserviceDate);
	}

	public NgAssetVO setInserviceDate(Date inserviceDate) {
		this.inserviceDate = DateUtil.clone(inserviceDate);
		return this;
	}

	public Date getDisposalDate() {
		return DateUtil.clone(disposalDate);
	}

	public NgAssetVO setDisposalDate(Date disposalDate) {
		this.disposalDate = DateUtil.clone(disposalDate);
		return this;
	}

	public BigDecimal getDisposalProceeds() {
		return disposalProceeds;
	}
	
	public NgAssetVO setDisposalProceeds(BigDecimal disposalProceeds) {
		this.disposalProceeds = disposalProceeds;
		return this;
	}
	
	public Long getSourceTransactionId() {
		return sourceTransactionId;
	}

	public NgAssetVO setSourceTransactionId(Long sourceTransactionId) {
		this.sourceTransactionId = sourceTransactionId;
		return this;
	}

	public String getUnitName() {
		return unitName;
	}

	public NgAssetVO setUnitName(String unitName) {
		this.unitName = unitName;
		return this;
	}

	public Long getUsefulLifeAtInservice() {
		return usefulLifeAtInservice;
	}

	public NgAssetVO setUsefulLifeAtInservice(Long usefulLifeAtInservice) {
		this.usefulLifeAtInservice = usefulLifeAtInservice;
		return this;
	}

	public String getNgAssetTypeName() {
		return ngAssetTypeName;
	}

	public NgAssetVO setNgAssetTypeName(String ngAssetTypeName) {
		this.ngAssetTypeName = ngAssetTypeName;
		return this;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public NgAssetVO setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
		return this;
	}

	public String getMainVehicle() {
		return mainVehicle;
	}

	public NgAssetVO setMainVehicle(String mainVehicle) {
		this.mainVehicle = mainVehicle;
		if ("T".equalsIgnoreCase(this.mainVehicle)) //mainVehicle T(rue) means it is a main asset
			this.mainAsset = Boolean.TRUE;
		else
			this.mainAsset = Boolean.FALSE;

		return this;
	}

	public Boolean isMainAsset() {
		return mainAsset;
	}

	public String getUpdateControlCode() {
		return updateControlCode;
	}

	public NgAssetVO setUpdateControlCode(String updateControlCode) {
		this.updateControlCode = updateControlCode;
		return this;
	}

	public Boolean isNewAsset() {
		return newAsset;
	}

	public void setNewAsset(Boolean newAsset) {
		this.newAsset = newAsset;
	}

	@Override
	public String toString() {
		return "NgAssetVO [ngAssetId=" + ngAssetId + ", ngAssetExtid=" + ngAssetExtid + ", subsidiaryId=" + subsidiaryId
				+ ", ngAssetName=" + ngAssetName + ", ngAssetNumber=" + ngAssetNumber + ", statusId=" + statusId
				+ ", typeId=" + typeId + ", capitalizedAssetValueAtIn=" + capitalizedAssetValueAtIn
				+ ", accumulatedDepreciation=" + accumulatedDepreciation + ", residualValueEstimate="
				+ residualValueEstimate + ", acquisitionDate=" + acquisitionDate + ", inserviceDate=" + inserviceDate
				+ ", disposalDate=" + disposalDate + ", sourceTransactionId=" + sourceTransactionId + ", unitName="
				+ unitName + ", usefulLifeAtInservice=" + usefulLifeAtInservice + ", ngAssetTypeName=" + ngAssetTypeName
				+ ", invoiceNo=" + invoiceNo + ", mainVehicle=" + mainVehicle + ", mainAsset=" + mainAsset
				+ ", updateControlCode=" + updateControlCode + ", newAsset=" + newAsset + "]";
	}
	
}

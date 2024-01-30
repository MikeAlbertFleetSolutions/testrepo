package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the ASSET_ITEM database table.
 * 
 */
@Entity
@Table(name="ASSET_ITEM")
public class AssetItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ASSET_ID")
	@SequenceGenerator(name="ASSET_ID", sequenceName="ASSET_ID", allocationSize=1)
	@Column(name="ASSET_ID")
	private Long assetId;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "assetItem", cascade = CascadeType.ALL)
	private List<AssetTypeHistory> assetTypeHistory;
	
	@Column(name = "FLEET_ID")
    @NotNull
	private Long fleetId;	
	
    @NotNull
	@Column(name="C_ID")
	private Long cId;
	
    @NotNull
	@Column(name = "CODE")
	private String code;
	
    @NotNull
	@Column(name = "add_on_seq")
	private String addOnSeq;
	
    @NotNull
	@Column(name = "DESCRIPTION")
	private String description;
	
    @NotNull
	@Column(name = "STATUS")
	private String status;
	
    @NotNull
	@Column(name = "ASSET_TYPE")
	private String assetType;
	
    @NotNull
	@Column(name = "CATEGORY_CODE")
	private String categoryCode;
	
    @NotNull
	@Column(name = "AGC_C_ID")
	private Long agcCId;
	
    @NotNull
	@Column(name = "DEP_CODE")
	private String depCode;
	
    @NotNull
	@Column(name = "LOCATION_CODE")
	private String locationCode;
	
    @NotNull
	@Column(name = "GROUP_CODE")
	private String groupCode;
	
    @NotNull
	@Column(name = "OP_CODE")
	private String opCode;
	
	@Column(name = "TP_SEQ_NO")
	private Long tpSeqNo;
	
	@Column(name = "DEPARTMENT_CODE")
	private String departmentCode;
	
	@Column(name="CURRENT_VALUE_BOOK")
	private BigDecimal currentValueBook;	

	@Column(name="CURRENT_VALUE_TAX")
	private BigDecimal currentValueTax;	

	@Column(name="ANNUAL_DEP_PERCENT_BOOK")
	private BigDecimal annualDepPercentBook;	

	@Column(name="ANNUAL_DEP_PERCENT_TAX")
	private BigDecimal annualDepPercentTax;	

	@Column(name="INITIAL_VALUE")
	private BigDecimal initialValue;	
	
	@Column(name="INITIAL_VALUE_TAX")
	private BigDecimal initialValueTax;	
	
	@Column(name="DEP_VAL_SOY_BOOK")
	private BigDecimal depValSoyBook;	

	@Column(name="DEP_VAL_SOY_TAX")
	private BigDecimal depValSoyTax;	

	@Column(name="RESIDUAL_VALUE")
	private BigDecimal residualValue;	
	
	@Column(name = "DATE_CAPITALISED")
	private Date dateCapitalised;
	
	@Column(name = "DATE_CAPITALISED_TAX")
	private Date dateCapitalisedTax;
	
	@Column(name = "LAST_DEP_DATE")
	private Date lastDepDate;
	
	@Column(name = "POSTED_DATE")
	private Date postedDate;
	
	@Column(name = "DISPOSAL_DATE")
	private Date disposalDate;
	
	@Column(name = "DISPOSAL_PROCEEDS")
	private BigDecimal disposalProceeds;
	
	@Column(name = "DISPOSAL_REASON")
	private String disposalReason;
	
	@Column(name = "PURCHASE_ORDER_NO")
	private String purchaseOrderNo;
	
	@Column(name = "MODEL_NO")
	private String modelNo; 
	
	@Column(name="INVOICE_NO")
	private String invoiceNo;
			
	@Column(name="INVOICE_DATE")
	private Date invoiceDate;
	
	@Column(name = "NO_OF_ITEMS")
	private Long noOfItems; 
			
	@Column(name="DAC_DAC_ID")
	private Long dacDacId;
		
	@Column(name = "COST_ADD_INT")
	private String costAddInt;
	
	@Column(name = "LIFE_BOOK")
	private Float lifeBook;
		
	@Column(name = "LIFE_TAX")
	private Float lifeTax;

	@Column(name = "ASSET_TYPE_EFF_DATE")
	private Date assetTypeEffDate;

	@Column(name = "SOURCE_CODE")
	private String sourceCode;
		
	@Column(name="NS_IN_SERVICE_DATE")
	private Date nsInServiceDate;

	public AssetItem() {}

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public Long getFleetId() {
		return fleetId;
	}

	public void setFleetId(Long fleetId) {
		this.fleetId = fleetId;
	}

	public BigDecimal getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(BigDecimal initialValue) {
		this.initialValue = initialValue;
	}

	public BigDecimal getInitialValueTax() {
		return initialValueTax;
	}

	public void setInitialValueTax(BigDecimal initialValueTax) {
		this.initialValueTax = initialValueTax;
	}

	public BigDecimal getDepValSoyBook() {
		return depValSoyBook;
	}

	public void setDepValSoyBook(BigDecimal depValSoyBook) {
		this.depValSoyBook = depValSoyBook;
	}

	public BigDecimal getDepValSoyTax() {
		return depValSoyTax;
	}

	public void setDepValSoyTax(BigDecimal depValSoyTax) {
		this.depValSoyTax = depValSoyTax;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public BigDecimal getCurrentValueBook() {
		return currentValueBook;
	}

	public void setCurrentValueBook(BigDecimal currentValueBook) {
		this.currentValueBook = currentValueBook;
	}

	public BigDecimal getCurrentValueTax() {
		return currentValueTax;
	}

	public void setCurrentValueTax(BigDecimal currentValueTax) {
		this.currentValueTax = currentValueTax;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAddOnSeq() {
		return addOnSeq;
	}

	public void setAddOnSeq(String addOnSeq) {
		this.addOnSeq = addOnSeq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public Long getAgcCId() {
		return agcCId;
	}

	public void setAgcCId(Long agcCId) {
		this.agcCId = agcCId;
	}

	public String getDepCode() {
		return depCode;
	}

	public void setDepCode(String depCode) {
		this.depCode = depCode;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Long getTpSeqNo() {
		return tpSeqNo;
	}

	public void setTpSeqNo(Long tpSeqNo) {
		this.tpSeqNo = tpSeqNo;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public BigDecimal getAnnualDepPercentBook() {
		return annualDepPercentBook;
	}

	public void setAnnualDepPercentBook(BigDecimal annualDepPercentBook) {
		this.annualDepPercentBook = annualDepPercentBook;
	}

	public BigDecimal getAnnualDepPercentTax() {
		return annualDepPercentTax;
	}

	public void setAnnualDepPercentTax(BigDecimal annualDepPercentTax) {
		this.annualDepPercentTax = annualDepPercentTax;
	}

	public BigDecimal getResidualValue() {
		return residualValue;
	}

	public void setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
	}

	public Date getDateCapitalised() {
		return DateUtil.clone(dateCapitalised);
	}

	public void setDateCapitalised(Date dateCapitalised) {
		this.dateCapitalised = DateUtil.clone(dateCapitalised);
	}

	public Date getDateCapitalisedTax() {
		return DateUtil.clone(dateCapitalisedTax);
	}

	public void setDateCapitalisedTax(Date dateCapitalisedTax) {
		this.dateCapitalisedTax = DateUtil.clone(dateCapitalisedTax);
	}

	public Date getLastDepDate() {
		return DateUtil.clone(lastDepDate);
	}

	public void setLastDepDate(Date lastDepDate) {
		this.lastDepDate = DateUtil.clone(lastDepDate);
	}

	public Date getPostedDate() {
		return DateUtil.clone(postedDate);
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = DateUtil.clone(postedDate);
	}

	public Date getDisposalDate() {
		return DateUtil.clone(disposalDate);
	}

	public void setDisposalDate(Date disposalDate) {
		this.disposalDate = DateUtil.clone(disposalDate);
	}

	public BigDecimal getDisposalProceeds() {
		return disposalProceeds;
	}

	public void setDisposalProceeds(BigDecimal disposalProceeds) {
		this.disposalProceeds = disposalProceeds;
	}

	public String getDisposalReason() {
		return disposalReason;
	}

	public void setDisposalReason(String disposalReason) {
		this.disposalReason = disposalReason;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getModelNo() {
		return modelNo;
	}

	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}

	public Date getInvoiceDate() {
		return DateUtil.clone(invoiceDate);
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = DateUtil.clone(invoiceDate);
	}

	public Long getNoOfItems() {
		return noOfItems;
	}

	public void setNoOfItems(Long noOfItems) {
		this.noOfItems = noOfItems;
	}

	public Long getDacDacId() {
		return dacDacId;
	}

	public void setDacDacId(Long dacDacId) {
		this.dacDacId = dacDacId;
	}

	public String getCostAddInt() {
		return costAddInt;
	}

	public void setCostAddInt(String costAddInt) {
		this.costAddInt = costAddInt;
	}

	public Float getLifeBook() {
		return lifeBook;
	}

	public void setLifeBook(Float lifeBook) {
		this.lifeBook = lifeBook;
	}

	public Float getLifeTax() {
		return lifeTax;
	}

	public void setLifeTax(Float lifeTax) {
		this.lifeTax = lifeTax;
	}

	public Date getAssetTypeEffDate() {
		return DateUtil.clone(assetTypeEffDate);
	}

	public void setAssetTypeEffDate(Date assetTypeEffDate) {
		this.assetTypeEffDate = DateUtil.clone(assetTypeEffDate);
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}
	
    public List<AssetTypeHistory> getAssetTypeHistory() {
		return assetTypeHistory;
	}

	public void setAssetTypeHistory(List<AssetTypeHistory> assetTypeHistory) {
		this.assetTypeHistory = assetTypeHistory;
	}

	public Date getNsInServiceDate() {
		return DateUtil.clone(nsInServiceDate);
	}

	public void setNsInServiceDate(Date nsInServiceDate) {
		this.nsInServiceDate = DateUtil.clone(nsInServiceDate);
	}
	
}
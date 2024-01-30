package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.validation.BillingReportTransactionValidator;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.mikealbert.util.data.DateUtil;

@BillingReportTransactionValidator
public class BillingReportTransactionVO implements Serializable {
	private String tranInternalId;
	private String tranExternalId;
	private TransactionTypeEnum type;
	private TransactionStatusEnum status;
	private boolean grouped;
	private String accountCode;	
	private String accountName;
	private BillingReportTypeEnum reportType;
	private Long docId;
	private String transactionNumber;
	private Date tranDate;
	private Date dueDate;
	private Long lineId;
	private Long lineNo;
	private String description;
	private String invoiceNote;
	private String expenseCategory;
	private String expenseSubCategory;
	private String analysisCodeDescription;
	private String unitInternalId;	
	private String unitExternalId;		
	private String unit;
	private String unitVin;
	private String unitYear;
	private String unitMake;
	private String unitModel;
	private Long driverId;
	private String driverName;
	private String driverCostCenterCode;
	private String driverCostCenterDescription;
	private String driverAddressState;
	private String rechargeCode;
	private String leaseInternalId;
	private String leaseExternalId;
	private String leaseName;
	private String leaseType;
	private Date leaseCommencementDate;
	private Date leaseEndDate;
	private String poTransactionNumber;
	private String supplierName;
	private Date maTransactionDate;
	private Integer odoReading;
	private Double qty;
	private String maintenanceCategoryCode;
	private String productCode;
	private String accountingPeriod;	
	private Date monthServiceDate;
	private BigDecimal linePaidAmount;
	private ApplicationEnum origin;	

	private BillingReportTransactionAmountVO baseNetAmount;
	private BillingReportTransactionAmountVO netAmount;
	private BillingReportTransactionAmountVO appliedAmount;
	private BillingReportTransactionAmountVO grossAmount;

	private Date searchFrom;
	private Date searchTo;
	private String fleetRefNo;

	public BillingReportTransactionVO() {}
		
	public String getTranInternalId() {
		return tranInternalId;
	}

	public BillingReportTransactionVO setTranInternalId(String tranInternalId) {
		this.tranInternalId = tranInternalId;
		return this;
	}

	public String getTranExternalId() {
		return tranExternalId;
	}

	public BillingReportTransactionVO setTranExternalId(String tranExternalId) {
		this.tranExternalId = tranExternalId;
		return this;
	}

	public TransactionTypeEnum getType() {
		return type;
	}

	public BillingReportTransactionVO setType(TransactionTypeEnum type) {
		this.type = type;
		return this;
	}
	
	public TransactionStatusEnum getStatus() {
		return status;
	}

	public BillingReportTransactionVO setStatus(TransactionStatusEnum status) {
		this.status = status;
		return this;
	}
	
	public boolean isGrouped() {
		return grouped;
	}

	public BillingReportTransactionVO setGrouped(boolean grouped) {
		this.grouped = grouped;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public BillingReportTransactionVO setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountName() {
		return accountName;
	}

	public BillingReportTransactionVO setAccountName(String accountName) {
		this.accountName = accountName;
		return this;
	}

	public BillingReportTypeEnum getReportType() {
		return reportType;
	}

	public BillingReportTransactionVO setReportType(BillingReportTypeEnum reportType) {
		this.reportType = reportType;
		return this;
	}
	
	public Long getDocId() {
		return docId;
	}

	public BillingReportTransactionVO setDocId(Long docId) {
		this.docId = docId;
		return this;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public BillingReportTransactionVO setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
		return this;
	}

	public Date getTranDate() {
		return DateUtil.clone(tranDate);
	}

	public BillingReportTransactionVO setTranDate(Date tranDate) {
		this.tranDate = DateUtil.clone(tranDate);
		return this;
	}

	public Date getDueDate() {
		return DateUtil.clone(dueDate);
	}

	public BillingReportTransactionVO setDueDate(Date dueDate) {
		this.dueDate = DateUtil.clone(dueDate);
		return this;
	}	

	public Long getLineId() {
		return lineId;
	}

	public BillingReportTransactionVO setLineId(Long lineId) {
		this.lineId = lineId;
		return this;
	}

	public Long getLineNo() {
		return lineNo;
	}

	public BillingReportTransactionVO setLineNo(Long lineNo) {
		this.lineNo = lineNo;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public BillingReportTransactionVO setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getInvoiceNote() {
		return invoiceNote;
	}

	public BillingReportTransactionVO setInvoiceNote(String invoiceNote) {
		this.invoiceNote = invoiceNote;
		return this;
	}
	
	public String getExpenseCategory() {
		return expenseCategory;
	}

	public BillingReportTransactionVO setExpenseCategory(String expenseCategory) {
		this.expenseCategory = expenseCategory;
		return this;
	}

	public String getExpenseSubCategory() {
		return expenseSubCategory;
	}

	public BillingReportTransactionVO setExpenseSubCategory(String expenseSubCategory) {
		this.expenseSubCategory = expenseSubCategory;
		return this;
	}

	public String getAnalysisCodeDescription() {
		return analysisCodeDescription;
	}

	public BillingReportTransactionVO setAnalysisCodeDescription(String analysisCodeDescription) {
		this.analysisCodeDescription = analysisCodeDescription;
		return this;
	}

	public String getUnitInternalId() {
		return unitInternalId;
	}

	public BillingReportTransactionVO setUnitInternalId(String unitInternalId) {
		this.unitInternalId = unitInternalId;
		return this;
	}

	public String getUnitExternalId() {
		return unitExternalId;
	}

	public BillingReportTransactionVO setUnitExternalId(String unitExternalId) {
		this.unitExternalId = unitExternalId;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public BillingReportTransactionVO setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public String getUnitVin() {
		return unitVin;
	}

	public BillingReportTransactionVO setUnitVin(String unitVin) {
		this.unitVin = unitVin;
		return this;
	}

	public String getUnitYear() {
		return unitYear;
	}

	public BillingReportTransactionVO setUnitYear(String unitYear) {
		this.unitYear = unitYear;
		return this;
	}

	public String getUnitMake() {
		return unitMake;
	}

	public BillingReportTransactionVO setUnitMake(String unitMake) {
		this.unitMake = unitMake;
		return this;
	}

	public String getUnitModel() {
		return unitModel;
	}

	public BillingReportTransactionVO setUnitModel(String unitModel) {
		this.unitModel = unitModel;
		return this;
	}

	public Long getDriverId() {
		return driverId;
	}

	public BillingReportTransactionVO setDriverId(Long driverId) {
		this.driverId = driverId;
		return this;
	}

	public String getDriverName() {
		return driverName;
	}

	public BillingReportTransactionVO setDriverName(String driverName) {
		this.driverName = driverName;
		return this;
	}
	
	public String getDriverCostCenterCode() {
		return driverCostCenterCode;
	}

	public BillingReportTransactionVO setDriverCostCenterCode(String driverCostCenterCode) {
		this.driverCostCenterCode = driverCostCenterCode;
		return this;
	}

	public String getDriverCostCenterDescription() {
		return driverCostCenterDescription;
	}

	public BillingReportTransactionVO setDriverCostCenterDescription(String driverCostCenterDescription) {
		this.driverCostCenterDescription = driverCostCenterDescription;
		return this;
	}

	public String getDriverAddressState() {
		return driverAddressState;
	}

	public BillingReportTransactionVO setDriverAddressState(String driverAddressState) {
		this.driverAddressState = driverAddressState;
		return this;
	}	

	public String getRechargeCode() {
		return rechargeCode;
	}

	public String getLeaseInternalId() {
		return leaseInternalId;
	}

	public BillingReportTransactionVO setLeaseInternalId(String leaseInternalId) {
		this.leaseInternalId = leaseInternalId;
		return this;
	}

	public String getLeaseExternalId() {
		return leaseExternalId;
	}

	public BillingReportTransactionVO setLeaseExternalId(String leaseExternalId) {
		this.leaseExternalId = leaseExternalId;
		return this;
	}

	public String getLeaseName() {
		return leaseName;
	}

	public BillingReportTransactionVO setLeaseName(String leaseName) {
		this.leaseName = leaseName;
		return this;
	}

	public BillingReportTransactionVO setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
		return this;
	}

	public String getLeaseType() {
		return leaseType;
	}

	public BillingReportTransactionVO setLeaseType(String leaseType) {
		this.leaseType = leaseType;
		return this;
	}

	public Date getLeaseCommencementDate() {
		return DateUtil.clone(leaseCommencementDate);
	}

	public BillingReportTransactionVO setLeaseCommencementDate(Date leaseCommencementDate) {
		this.leaseCommencementDate = DateUtil.clone(leaseCommencementDate);
		return this;
	}

	public Date getLeaseEndDate() {
		return DateUtil.clone(leaseEndDate);
	}

	public BillingReportTransactionVO setLeaseEndDate(Date leaseEndDate) {
		this.leaseEndDate = DateUtil.clone(leaseEndDate);
		return this;
	}

	public String getPoTransactionNumber() {
		return poTransactionNumber;
	}

	public BillingReportTransactionVO setPoTransactionNumber(String poTransactionNumber) {
		this.poTransactionNumber = poTransactionNumber;
		return this;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public BillingReportTransactionVO setSupplierName(String supplierName) {
		this.supplierName = supplierName;
		return this;
	}

	public Date getMaTransactionDate() {
		return DateUtil.clone(maTransactionDate);
	}

	public BillingReportTransactionVO setMaTransactionDate(Date maTransactionDate) {
		this.maTransactionDate = DateUtil.clone(maTransactionDate);
		return this;
	}

	public Integer getOdoReading() {
		return odoReading;
	}

	public BillingReportTransactionVO setOdoReading(Integer odoReading) {
		this.odoReading = odoReading;
		return this;
	}

	public Double getQty() {
		return qty;
	}

	public BillingReportTransactionVO setQty(Double qty) {
		this.qty = qty;
		return this;
	}

	public String getMaintenanceCategoryCode() {
		return maintenanceCategoryCode;
	}

	public BillingReportTransactionVO setMaintenanceCategoryCode(String maintenanceCategoryCode) {
		this.maintenanceCategoryCode = maintenanceCategoryCode;
		return this;
	}

	public String getProductCode() {
		return productCode;
	}

	public BillingReportTransactionVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getAccountingPeriod() {
		return accountingPeriod;
	}

	public BillingReportTransactionVO setAccountingPeriod(String accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
		return this;
	}
	
	public Date getMonthServiceDate() {
		return DateUtil.clone(monthServiceDate);
	}

	public BillingReportTransactionVO setMonthServiceDate(Date monthServiceDate) {
		this.monthServiceDate = DateUtil.clone(monthServiceDate);
		return this;
	}
	
	public BigDecimal getLinePaidAmount() {
		return linePaidAmount;
	}

	public BillingReportTransactionVO setLinePaidAmount(BigDecimal linePaidAmount) {
		this.linePaidAmount = linePaidAmount;
		return this;
	}

	public ApplicationEnum getOrigin() {
		return origin;
	}

	public BillingReportTransactionVO setOrigin(ApplicationEnum origin) {
		this.origin = origin;
		return this;
	}

	public BillingReportTransactionAmountVO getBaseNetAmount() {
		return baseNetAmount;
	}

	public BillingReportTransactionVO setBaseNetAmount(BillingReportTransactionAmountVO baseNetAmount) {
		this.baseNetAmount = baseNetAmount;
		return this;
	}

	public BillingReportTransactionAmountVO getNetAmount() {
		return netAmount;
	}

	public BillingReportTransactionVO setNetAmount(BillingReportTransactionAmountVO netAmount) {
		this.netAmount = netAmount;
		return this;
	}

	public BillingReportTransactionAmountVO getAppliedAmount() {
		return appliedAmount;
	}

	public BillingReportTransactionVO setAppliedAmount(BillingReportTransactionAmountVO appliedAmount) {
		this.appliedAmount = appliedAmount;
		return this;
	}

	public BillingReportTransactionAmountVO getGrossAmount() {
		return grossAmount;
	}

	public BillingReportTransactionVO setGrossAmount(BillingReportTransactionAmountVO grossAmount) {
		this.grossAmount = grossAmount;
		return this;
	}

	public Date getSearchFrom() {
		return DateUtil.clone(searchFrom);
	}

	public BillingReportTransactionVO setSearchFrom(Date searchFrom) {
		this.searchFrom = DateUtil.clone(searchFrom);
		return this;
	}

	public Date getSearchTo() {
		return searchTo;
	}

	public BillingReportTransactionVO setSearchTo(Date searchTo) {
		this.searchTo = DateUtil.clone(searchTo);
		return this;
	}

	public String getFleetRefNo() {
		return fleetRefNo;
	}

	public BillingReportTransactionVO setFleetRefNo(String fleetRefNo) {
		this.fleetRefNo = fleetRefNo;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tranInternalId == null) ? 0 : tranInternalId.hashCode());
		result = prime * result + ((tranExternalId == null) ? 0 : tranExternalId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (grouped ? 1231 : 1237);
		result = prime * result + ((accountCode == null) ? 0 : accountCode.hashCode());
		result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
		result = prime * result + ((reportType == null) ? 0 : reportType.hashCode());
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
		result = prime * result + ((transactionNumber == null) ? 0 : transactionNumber.hashCode());
		result = prime * result + ((tranDate == null) ? 0 : tranDate.hashCode());
		result = prime * result + ((dueDate == null) ? 0 : dueDate.hashCode());
		result = prime * result + ((lineId == null) ? 0 : lineId.hashCode());
		result = prime * result + ((lineNo == null) ? 0 : lineNo.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((invoiceNote == null) ? 0 : invoiceNote.hashCode());
		result = prime * result + ((expenseCategory == null) ? 0 : expenseCategory.hashCode());
		result = prime * result + ((expenseSubCategory == null) ? 0 : expenseSubCategory.hashCode());
		result = prime * result + ((analysisCodeDescription == null) ? 0 : analysisCodeDescription.hashCode());
		result = prime * result + ((unitInternalId == null) ? 0 : unitInternalId.hashCode());
		result = prime * result + ((unitExternalId == null) ? 0 : unitExternalId.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((unitVin == null) ? 0 : unitVin.hashCode());
		result = prime * result + ((unitYear == null) ? 0 : unitYear.hashCode());
		result = prime * result + ((unitMake == null) ? 0 : unitMake.hashCode());
		result = prime * result + ((unitModel == null) ? 0 : unitModel.hashCode());
		result = prime * result + ((driverId == null) ? 0 : driverId.hashCode());
		result = prime * result + ((driverName == null) ? 0 : driverName.hashCode());
		result = prime * result + ((driverCostCenterCode == null) ? 0 : driverCostCenterCode.hashCode());
		result = prime * result + ((driverCostCenterDescription == null) ? 0 : driverCostCenterDescription.hashCode());
		result = prime * result + ((driverAddressState == null) ? 0 : driverAddressState.hashCode());
		result = prime * result + ((rechargeCode == null) ? 0 : rechargeCode.hashCode());
		result = prime * result + ((leaseInternalId == null) ? 0 : leaseInternalId.hashCode());
		result = prime * result + ((leaseExternalId == null) ? 0 : leaseExternalId.hashCode());
		result = prime * result + ((leaseName == null) ? 0 : leaseName.hashCode());
		result = prime * result + ((leaseType == null) ? 0 : leaseType.hashCode());
		result = prime * result + ((leaseCommencementDate == null) ? 0 : leaseCommencementDate.hashCode());
		result = prime * result + ((leaseEndDate == null) ? 0 : leaseEndDate.hashCode());
		result = prime * result + ((poTransactionNumber == null) ? 0 : poTransactionNumber.hashCode());
		result = prime * result + ((supplierName == null) ? 0 : supplierName.hashCode());
		result = prime * result + ((maTransactionDate == null) ? 0 : maTransactionDate.hashCode());
		result = prime * result + ((odoReading == null) ? 0 : odoReading.hashCode());
		result = prime * result + ((qty == null) ? 0 : qty.hashCode());
		result = prime * result + ((maintenanceCategoryCode == null) ? 0 : maintenanceCategoryCode.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((accountingPeriod == null) ? 0 : accountingPeriod.hashCode());
		result = prime * result + ((monthServiceDate == null) ? 0 : monthServiceDate.hashCode());
		result = prime * result + ((linePaidAmount == null) ? 0 : linePaidAmount.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((baseNetAmount == null) ? 0 : baseNetAmount.hashCode());
		result = prime * result + ((netAmount == null) ? 0 : netAmount.hashCode());
		result = prime * result + ((appliedAmount == null) ? 0 : appliedAmount.hashCode());
		result = prime * result + ((grossAmount == null) ? 0 : grossAmount.hashCode());
		result = prime * result + ((searchFrom == null) ? 0 : searchFrom.hashCode());
		result = prime * result + ((searchTo == null) ? 0 : searchTo.hashCode());
		result = prime * result + ((fleetRefNo == null) ? 0 : fleetRefNo.hashCode());
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
		BillingReportTransactionVO other = (BillingReportTransactionVO) obj;
		if (tranInternalId == null) {
			if (other.tranInternalId != null)
				return false;
		} else if (!tranInternalId.equals(other.tranInternalId))
			return false;
		if (tranExternalId == null) {
			if (other.tranExternalId != null)
				return false;
		} else if (!tranExternalId.equals(other.tranExternalId))
			return false;
		if (type != other.type)
			return false;
		if (status != other.status)
			return false;
		if (grouped != other.grouped)
			return false;
		if (accountCode == null) {
			if (other.accountCode != null)
				return false;
		} else if (!accountCode.equals(other.accountCode))
			return false;
		if (accountName == null) {
			if (other.accountName != null)
				return false;
		} else if (!accountName.equals(other.accountName))
			return false;
		if (reportType != other.reportType)
			return false;
		if (docId == null) {
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		if (transactionNumber == null) {
			if (other.transactionNumber != null)
				return false;
		} else if (!transactionNumber.equals(other.transactionNumber))
			return false;
		if (tranDate == null) {
			if (other.tranDate != null)
				return false;
		} else if (!tranDate.equals(other.tranDate))
			return false;
		if (dueDate == null) {
			if (other.dueDate != null)
				return false;
		} else if (!dueDate.equals(other.dueDate))
			return false;
		if (lineId == null) {
			if (other.lineId != null)
				return false;
		} else if (!lineId.equals(other.lineId))
			return false;
		if (lineNo == null) {
			if (other.lineNo != null)
				return false;
		} else if (!lineNo.equals(other.lineNo))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (invoiceNote == null) {
			if (other.invoiceNote != null)
				return false;
		} else if (!invoiceNote.equals(other.invoiceNote))
			return false;
		if (expenseCategory == null) {
			if (other.expenseCategory != null)
				return false;
		} else if (!expenseCategory.equals(other.expenseCategory))
			return false;
		if (expenseSubCategory == null) {
			if (other.expenseSubCategory != null)
				return false;
		} else if (!expenseSubCategory.equals(other.expenseSubCategory))
			return false;
		if (analysisCodeDescription == null) {
			if (other.analysisCodeDescription != null)
				return false;
		} else if (!analysisCodeDescription.equals(other.analysisCodeDescription))
			return false;
		if (unitInternalId == null) {
			if (other.unitInternalId != null)
				return false;
		} else if (!unitInternalId.equals(other.unitInternalId))
			return false;
		if (unitExternalId == null) {
			if (other.unitExternalId != null)
				return false;
		} else if (!unitExternalId.equals(other.unitExternalId))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		if (unitVin == null) {
			if (other.unitVin != null)
				return false;
		} else if (!unitVin.equals(other.unitVin))
			return false;
		if (unitYear == null) {
			if (other.unitYear != null)
				return false;
		} else if (!unitYear.equals(other.unitYear))
			return false;
		if (unitMake == null) {
			if (other.unitMake != null)
				return false;
		} else if (!unitMake.equals(other.unitMake))
			return false;
		if (unitModel == null) {
			if (other.unitModel != null)
				return false;
		} else if (!unitModel.equals(other.unitModel))
			return false;
		if (driverId == null) {
			if (other.driverId != null)
				return false;
		} else if (!driverId.equals(other.driverId))
			return false;
		if (driverName == null) {
			if (other.driverName != null)
				return false;
		} else if (!driverName.equals(other.driverName))
			return false;
		if (driverCostCenterCode == null) {
			if (other.driverCostCenterCode != null)
				return false;
		} else if (!driverCostCenterCode.equals(other.driverCostCenterCode))
			return false;
		if (driverCostCenterDescription == null) {
			if (other.driverCostCenterDescription != null)
				return false;
		} else if (!driverCostCenterDescription.equals(other.driverCostCenterDescription))
			return false;
		if (driverAddressState == null) {
			if (other.driverAddressState != null)
				return false;
		} else if (!driverAddressState.equals(other.driverAddressState))
			return false;
		if (rechargeCode == null) {
			if (other.rechargeCode != null)
				return false;
		} else if (!rechargeCode.equals(other.rechargeCode))
			return false;
		if (leaseInternalId == null) {
			if (other.leaseInternalId != null)
				return false;
		} else if (!leaseInternalId.equals(other.leaseInternalId))
			return false;
		if (leaseExternalId == null) {
			if (other.leaseExternalId != null)
				return false;
		} else if (!leaseExternalId.equals(other.leaseExternalId))
			return false;
		if (leaseName == null) {
			if (other.leaseName != null)
				return false;
		} else if (!leaseName.equals(other.leaseName))
			return false;
		if (leaseType == null) {
			if (other.leaseType != null)
				return false;
		} else if (!leaseType.equals(other.leaseType))
			return false;
		if (leaseCommencementDate == null) {
			if (other.leaseCommencementDate != null)
				return false;
		} else if (!leaseCommencementDate.equals(other.leaseCommencementDate))
			return false;
		if (leaseEndDate == null) {
			if (other.leaseEndDate != null)
				return false;
		} else if (!leaseEndDate.equals(other.leaseEndDate))
			return false;
		if (poTransactionNumber == null) {
			if (other.poTransactionNumber != null)
				return false;
		} else if (!poTransactionNumber.equals(other.poTransactionNumber))
			return false;
		if (supplierName == null) {
			if (other.supplierName != null)
				return false;
		} else if (!supplierName.equals(other.supplierName))
			return false;
		if (maTransactionDate == null) {
			if (other.maTransactionDate != null)
				return false;
		} else if (!maTransactionDate.equals(other.maTransactionDate))
			return false;
		if (odoReading == null) {
			if (other.odoReading != null)
				return false;
		} else if (!odoReading.equals(other.odoReading))
			return false;
		if (qty == null) {
			if (other.qty != null)
				return false;
		} else if (!qty.equals(other.qty))
			return false;
		if (maintenanceCategoryCode == null) {
			if (other.maintenanceCategoryCode != null)
				return false;
		} else if (!maintenanceCategoryCode.equals(other.maintenanceCategoryCode))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (accountingPeriod == null) {
			if (other.accountingPeriod != null)
				return false;
		} else if (!accountingPeriod.equals(other.accountingPeriod))
			return false;
		if (monthServiceDate == null) {
			if (other.monthServiceDate != null)
				return false;
		} else if (!monthServiceDate.equals(other.monthServiceDate))
			return false;
		if (linePaidAmount == null) {
			if (other.linePaidAmount != null)
				return false;
		} else if (!linePaidAmount.equals(other.linePaidAmount))
			return false;
		if (origin != other.origin)
			return false;
		if (baseNetAmount == null) {
			if (other.baseNetAmount != null)
				return false;
		} else if (!baseNetAmount.equals(other.baseNetAmount))
			return false;
		if (netAmount == null) {
			if (other.netAmount != null)
				return false;
		} else if (!netAmount.equals(other.netAmount))
			return false;
		if (appliedAmount == null) {
			if (other.appliedAmount != null)
				return false;
		} else if (!appliedAmount.equals(other.appliedAmount))
			return false;
		if (grossAmount == null) {
			if (other.grossAmount != null)
				return false;
		} else if (!grossAmount.equals(other.grossAmount))
			return false;
		if (searchFrom == null) {
			if (other.searchFrom != null)
				return false;
		} else if (!searchFrom.equals(other.searchFrom))
			return false;
		if (searchTo == null) {
			if (other.searchTo != null)
				return false;
		} else if (!searchTo.equals(other.searchTo))
			return false;
		if (fleetRefNo == null) {
			if (other.fleetRefNo != null)
				return false;
		} else if (!fleetRefNo.equals(other.fleetRefNo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BillingReportTransactionVO [tranInternalId=" + tranInternalId + ", tranExternalId=" + tranExternalId
				+ ", type=" + type + ", status=" + status + ", grouped=" + grouped + ", accountCode=" + accountCode
				+ ", accountName=" + accountName + ", reportType=" + reportType + ", docId=" + docId
				+ ", transactionNumber=" + transactionNumber + ", tranDate=" + tranDate + ", dueDate=" + dueDate
				+ ", lineId=" + lineId + ", lineNo=" + lineNo + ", description=" + description + ", invoiceNote="
				+ invoiceNote + ", expenseCategory=" + expenseCategory + ", expenseSubCategory=" + expenseSubCategory
				+ ", analysisCodeDescription=" + analysisCodeDescription + ", unitInternalId=" + unitInternalId
				+ ", unitExternalId=" + unitExternalId + ", unit=" + unit + ", unitVin=" + unitVin + ", unitYear="
				+ unitYear + ", unitMake=" + unitMake + ", unitModel=" + unitModel + ", driverId=" + driverId
				+ ", driverName=" + driverName + ", driverCostCenterCode=" + driverCostCenterCode
				+ ", driverCostCenterDescription=" + driverCostCenterDescription + ", driverAddressState="
				+ driverAddressState + ", rechargeCode=" + rechargeCode + ", leaseInternalId=" + leaseInternalId
				+ ", leaseExternalId=" + leaseExternalId + ", leaseName=" + leaseName + ", leaseType=" + leaseType
				+ ", leaseCommencementDate=" + leaseCommencementDate + ", leaseEndDate=" + leaseEndDate
				+ ", poTransactionNumber=" + poTransactionNumber + ", supplierName=" + supplierName
				+ ", maTransactionDate=" + maTransactionDate + ", odoReading=" + odoReading + ", qty=" + qty
				+ ", maintenanceCategoryCode=" + maintenanceCategoryCode + ", productCode=" + productCode
				+ ", accountingPeriod=" + accountingPeriod + ", monthServiceDate=" + monthServiceDate
				+ ", linePaidAmount=" + linePaidAmount + ", origin=" + origin + ", baseNetAmount=" + baseNetAmount
				+ ", netAmount=" + netAmount + ", appliedAmount=" + appliedAmount + ", grossAmount=" + grossAmount
				+ ", searchFrom=" + searchFrom + ", searchTo=" + searchTo + ", fleetRefNo=" + fleetRefNo + "]";
	}		
}

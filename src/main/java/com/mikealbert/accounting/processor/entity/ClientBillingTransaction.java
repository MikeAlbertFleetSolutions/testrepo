package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.mikealbert.util.data.DateUtil;

/**
 * The persistent class for the CLIENT_BILLING_TRANSACTIONS database table.
 * 
 */
@Entity
@Table(name="CLIENT_BILLING_TRANSACTIONS")
public class ClientBillingTransaction implements Serializable{
	private static final long serialVersionUID = 1L;
 
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CBT_SEQ")    
    @SequenceGenerator(name="CBT_SEQ", sequenceName="CBT_SEQ", allocationSize=1)	
	@Column(name="CBT_ID")
	private Long cbtId;

	@Column(name="TRAN_INT_ID")
	private String tranIntId;

	@Column(name="TRAN_EXT_ID")
	private String tranExtId;

	@Column(name="ACCOUNT_NAME")
	private String acountName;

	@Column(name="INVOICE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date invoiceDate;

	@Column(name="INVOICE_DUE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date invoiceDueDate;	

	@Column(name="INVOICE_TYPE")
	private String invoiceType;	

	@Column(name="REPORT_NAME")
	private String reportName;	

	@Column(name="ANALYSIS_CODE_DESC")
	private String analysisCodeDesc;

	@Column(name="REPORT_CATEGORY")
	private String reportCategory;	
	
	@Column(name="REPORT_SUB_CATEGORY")
	private String reportSubCategory;		

	@Column(name="INVOICE_NO")
	private String invoiceNo;		

	@Column(name="COST_CENTRE")
	private String costcenter;	

	@Column(name="COST_CENTRE_DESC")
	private String costcenterDescription;	
	
	@Column(name="LINE_ID")
	private Long lineId;	

	@Column(name="LINE_NO")
	private Long lineNo;	

	@Column(name="UNIT_NO")
	private String unitNo;	

	@Column(name="FLEET_REF")
	private String fleetRef;	

	@Column(name="DRIVER_NAME")
	private String driverName;	

	@Column(name="RECHARGE_CODE")
	private String rechargeCode;	

	@Column(name="MODEL_YEAR")
	private String modelYear;

	@Column(name="MAKE")
	private String make;

	@Column(name="MODEL")
	private String model;
		
	@Column(name="PO_NUMBER")
	private String poNumber;

	@Column(name="LICENSE_NO")
	private String licenseNO;

	@Column(name="SERVICE_CENTRE")
	private String serviceCenter;
	
	@Column(name="SERVICE_DATE")
	private Date serviceDate;
	
	@Column(name="MILEAGE")
	private Long mileage;
	
	@Column(name="MAINT_TASK_QTY")
	private Double maintTaskQty;
	
	@Column(name="MAINT_CAT_CODE")
	private String maintCatCode;
	
	@Column(name="MAINT_CODE")
	private String maintCode;
	
	@Column(name="LINE_DESC")
	private String lineDescription;

	@Column(name="ACCOUNTING_PERIOD")
	private String accountingPeriod;

	@Column(name="BILLING_PERIOD")
	private Date billingPeriod;
	
	@Column(name="LINE_AMOUNT")
	private BigDecimal lineAmount;
	
	@Column(name="LINE_TAX")
	private BigDecimal lineTax;
	
	@Column(name="LINE_TOTAL")
	private BigDecimal lineTotal;

	@Column(name="ALLOC_AMT_NET")
	private BigDecimal allocAmtNet;
	
	@Column(name="ALLOC_AMT_TAX")
	private BigDecimal allocAmtTax;
	
	@Column(name="ALLOC_AMT_GROSS")
	private BigDecimal allocAmtGross;
	
	@Column(name="TOT_AMT_NET")
	private BigDecimal totAmtNet;
	
	@Column(name="TOT_AMT_TAX")
	private BigDecimal totAmtTax;
	
	@Column(name="TOT_AMT_GROSS")
	private BigDecimal totAmtGross;	
	
	@Column(name="FROM_DATE")
	private Date fromDate;

	@Column(name="TO_DATE")
	private Date toDate;
	
	@Column(name="FMS_ID")
	private Long fmsId;
	
	@Column(name="DRV_ID")
	private Long drvId;
	
	@Column(name="ACCOUNT_CODE")
	private String accountCode;
	
	@Column(name="DOC_ID")
	private Long docId;	

	@Column(name="CLIENT_PO_NUMBER")
	private String clientPoNumber;	
	
	@Column(name="VIN")
	private String vin;	
	
	@Column(name="LINE_NARRATIVE")
	private String lineNarrative;

	@Column(name="ORIGIN")
	private String origin;	
	
	@Column(name="LOCK_YN")
	private String lockYN = "N";

	@Version
	@Column(name="VERSIONTS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date versionts;	
	
	@Column(name="DRIVER_STATE")
	private String driverState;

	public ClientBillingTransaction() {}

	public Long getCbtId() {
		return cbtId;
	}
	
	public String getTranIntId() {
		return tranIntId;
	}

	public ClientBillingTransaction setTranIntId(String tranIntId) {
		this.tranIntId = tranIntId;
		return this;
	}

	public String getTranExtId() {
		return tranExtId;
	}

	public ClientBillingTransaction setTranExtId(String tranExtId) {
		this.tranExtId = tranExtId;
		return this;
	}

	public String getAcountName() {
		return acountName;
	}

	public ClientBillingTransaction setAcountName(String acountName) {
		this.acountName = acountName;
		return this;
	}

	public Date getInvoiceDate() {
		return DateUtil.clone(invoiceDate);
	}

	public ClientBillingTransaction setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = DateUtil.clone(invoiceDate);
		return this;
	}

	public Date getInvoiceDueDate() {
		return DateUtil.clone(invoiceDueDate);
	}

	public ClientBillingTransaction setInvoiceDueDate(Date invoiceDueDate) {
		this.invoiceDueDate = DateUtil.clone(invoiceDueDate);
		return this;
	}

	public String getInvoiceType() {
		return invoiceType;
	}
	
	public String getReportName() {
		return reportName;
	}

	public ClientBillingTransaction setReportName(String reportName) {
		this.reportName = reportName;
		return this;
	}
	
	public String getAnalysisCodeDesc() {
		return analysisCodeDesc;
	}

	public ClientBillingTransaction setAnalysisCodeDesc(String analysisCodeDesc) {
		this.analysisCodeDesc = analysisCodeDesc;
		return this;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public ClientBillingTransaction setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
		return this;
	}

	public String getReportSubCategory() {
		return reportSubCategory;
	}

	public ClientBillingTransaction setReportSubCategory(String reportSubCategory) {
		this.reportSubCategory = reportSubCategory;
		return this;
	}

	public ClientBillingTransaction setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public ClientBillingTransaction setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
		return this;
	}

	public String getCostcenter() {
		return costcenter;
	}

	public ClientBillingTransaction setCostcenter(String costcenter) {
		this.costcenter = costcenter;
		return this;
	}

	public String getCostcenterDescription() {
		return costcenterDescription;
	}

	public ClientBillingTransaction setCostcenterDescription(String costcenterDescription) {
		this.costcenterDescription = costcenterDescription;
		return this;
	}

	public Long getLineId() {
		return lineId;
	}

	public ClientBillingTransaction setLineId(Long lineId) {
		this.lineId = lineId;
		return this;
	}
	
	public Long getLineNo() {
		return lineNo;
	}

	public ClientBillingTransaction setLineNo(Long lineNo) {
		this.lineNo = lineNo;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public ClientBillingTransaction setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public String getFleetRef() {
		return fleetRef;
	}

	public ClientBillingTransaction setFleetRef(String fleetRef) {
		this.fleetRef = fleetRef;
		return this;
	}

	public String getDriverName() {
		return driverName;
	}

	public ClientBillingTransaction setDriverName(String driverName) {
		this.driverName = driverName;
		return this;
	}

	public String getRechargeCode() {
		return rechargeCode;
	}

	public ClientBillingTransaction setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
		return this;
	}

	public String getModelYear() {
		return modelYear;
	}

	public ClientBillingTransaction setModelYear(String modelYear) {
		this.modelYear = modelYear;
		return this;
	}

	public String getMake() {
		return make;
	}

	public ClientBillingTransaction setMake(String make) {
		this.make = make;
		return this;
	}

	public String getModel() {
		return model;
	}

	public ClientBillingTransaction setModel(String model) {
		this.model = model;
		return this;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public ClientBillingTransaction setPoNumber(String poNumber) {
		this.poNumber = poNumber;
		return this;
	}

	public String getLicenseNO() {
		return licenseNO;
	}

	public ClientBillingTransaction setLicenseNO(String licenseNO) {
		this.licenseNO = licenseNO;
		return this;
	}

	public String getServiceCenter() {
		return serviceCenter;
	}

	public ClientBillingTransaction setServiceCenter(String serviceCenter) {
		this.serviceCenter = serviceCenter;
		return this;
	}

	public Date getServiceDate() {
		return serviceDate;
	}

	public ClientBillingTransaction setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
		return this;
	}

	public Long getMileage() {
		return mileage;
	}

	public ClientBillingTransaction setMileage(Long mileage) {
		this.mileage = mileage;
		return this;
	}

	public Double getMaintTaskQty() {
		return maintTaskQty;
	}

	public ClientBillingTransaction setMaintTaskQty(Double maintTaskQty) {
		this.maintTaskQty = maintTaskQty;
		return this;
	}

	public String getMaintCatCode() {
		return maintCatCode;
	}

	public ClientBillingTransaction setMaintCatCode(String maintCatCode) {
		this.maintCatCode = maintCatCode;
		return this;
	}

	public String getMaintCode() {
		return maintCode;
	}

	public ClientBillingTransaction setMaintCode(String maintCode) {
		this.maintCode = maintCode;
		return this;
	}

	public String getLineDescription() {
		return lineDescription;
	}

	public ClientBillingTransaction setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
		return this;
	}

	public String getAccountingPeriod() {
		return accountingPeriod;
	}

	public ClientBillingTransaction setAccountingPeriod(String accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
		return this;
	}

	public Date getBillingPeriod() {
		return DateUtil.clone(billingPeriod);
	}

	public ClientBillingTransaction setBillingPeriod(Date billingPeriod) {
		this.billingPeriod = DateUtil.clone(billingPeriod);
		return this;
	}

	public BigDecimal getLineAmount() {
		return lineAmount;
	}

	public ClientBillingTransaction setLineAmount(BigDecimal lineAmount) {
		this.lineAmount = lineAmount;
		return this;
	}

	public BigDecimal getLineTax() {
		return lineTax;
	}

	public ClientBillingTransaction setLineTax(BigDecimal lineTax) {
		this.lineTax = lineTax;
		return this;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public ClientBillingTransaction setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
		return this;
	}

	public BigDecimal getAllocAmtNet() {
		return allocAmtNet;
	}

	public ClientBillingTransaction setAllocAmtNet(BigDecimal allocAmtNet) {
		this.allocAmtNet = allocAmtNet;
		return this;
	}

	public BigDecimal getAllocAmtTax() {
		return allocAmtTax;
	}

	public ClientBillingTransaction setAllocAmtTax(BigDecimal allocAmtTax) {
		this.allocAmtTax = allocAmtTax;
		return this;
	}

	public BigDecimal getAllocAmtGross() {
		return allocAmtGross;
	}

	public ClientBillingTransaction setAllocAmtGross(BigDecimal allocAmtGross) {
		this.allocAmtGross = allocAmtGross;
		return this;
	}

	public BigDecimal getTotAmtNet() {
		return totAmtNet;
	}

	public ClientBillingTransaction setTotAmtNet(BigDecimal totAmtNet) {
		this.totAmtNet = totAmtNet;
		return this;
	}

	public BigDecimal getTotAmtTax() {
		return totAmtTax;
	}

	public ClientBillingTransaction setTotAmtTax(BigDecimal totAmtTax) {
		this.totAmtTax = totAmtTax;
		return this;
	}

	public BigDecimal getTotAmtGross() {
		return totAmtGross;
	}

	public ClientBillingTransaction setTotAmtGross(BigDecimal totAmtGross) {
		this.totAmtGross = totAmtGross;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public ClientBillingTransaction setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}

	public ClientBillingTransaction setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public ClientBillingTransaction setFmsId(Long fmsId) {
		this.fmsId = fmsId;
		return this;
	}

	public Long getDrvId() {
		return drvId;
	}

	public ClientBillingTransaction setDrvId(Long drvId) {
		this.drvId = drvId;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public ClientBillingTransaction setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public Long getDocId() {
		return docId;
	}

	public ClientBillingTransaction setDocId(Long docId) {
		this.docId = docId;
		return this;
	}

	public String getClientPoNumber() {
		return clientPoNumber;
	}

	public ClientBillingTransaction setClientPoNumber(String clientPoNumber) {
		this.clientPoNumber = clientPoNumber;
		return this;
	}

	public String getVin() {
		return vin;
	}

	public ClientBillingTransaction setVin(String vin) {
		this.vin = vin;
		return this;
	}

	public String getLineNarrative() {
		return lineNarrative;
	}

	public ClientBillingTransaction setLineNarrative(String lineNarrative) {
		this.lineNarrative = lineNarrative;
		return this;
	}	

	public String getOrigin() {
		return origin;
	}

	public ClientBillingTransaction setOrigin(String origin) {
		this.origin = origin;
		return this;
	}

	public String getLockYN() {
		return lockYN;
	}

	public ClientBillingTransaction setLockYN(String lockYN) {
		this.lockYN = lockYN;
		return this;
	}

	public Date getVersionts() {
		return versionts;
	}
	
	

	public String getDriverState() {
		return driverState;
	}

	public ClientBillingTransaction setDriverState(String driverState) {
		this.driverState = driverState;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountCode == null) ? 0 : accountCode.hashCode());
		result = prime * result + ((accountingPeriod == null) ? 0 : accountingPeriod.hashCode());
		result = prime * result + ((acountName == null) ? 0 : acountName.hashCode());
		result = prime * result + ((allocAmtGross == null) ? 0 : allocAmtGross.hashCode());
		result = prime * result + ((allocAmtNet == null) ? 0 : allocAmtNet.hashCode());
		result = prime * result + ((allocAmtTax == null) ? 0 : allocAmtTax.hashCode());
		result = prime * result + ((analysisCodeDesc == null) ? 0 : analysisCodeDesc.hashCode());
		result = prime * result + ((billingPeriod == null) ? 0 : billingPeriod.hashCode());
		result = prime * result + ((cbtId == null) ? 0 : cbtId.hashCode());
		result = prime * result + ((clientPoNumber == null) ? 0 : clientPoNumber.hashCode());
		result = prime * result + ((costcenter == null) ? 0 : costcenter.hashCode());
		result = prime * result + ((costcenterDescription == null) ? 0 : costcenterDescription.hashCode());
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
		result = prime * result + ((driverName == null) ? 0 : driverName.hashCode());
		result = prime * result + ((drvId == null) ? 0 : drvId.hashCode());
		result = prime * result + ((fleetRef == null) ? 0 : fleetRef.hashCode());
		result = prime * result + ((fmsId == null) ? 0 : fmsId.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((invoiceDate == null) ? 0 : invoiceDate.hashCode());
		result = prime * result + ((invoiceDueDate == null) ? 0 : invoiceDueDate.hashCode());
		result = prime * result + ((invoiceNo == null) ? 0 : invoiceNo.hashCode());
		result = prime * result + ((invoiceType == null) ? 0 : invoiceType.hashCode());
		result = prime * result + ((licenseNO == null) ? 0 : licenseNO.hashCode());
		result = prime * result + ((lineAmount == null) ? 0 : lineAmount.hashCode());
		result = prime * result + ((lineDescription == null) ? 0 : lineDescription.hashCode());
		result = prime * result + ((lineId == null) ? 0 : lineId.hashCode());
		result = prime * result + ((lineNarrative == null) ? 0 : lineNarrative.hashCode());
		result = prime * result + ((lineNo == null) ? 0 : lineNo.hashCode());
		result = prime * result + ((lineTax == null) ? 0 : lineTax.hashCode());
		result = prime * result + ((lineTotal == null) ? 0 : lineTotal.hashCode());
		result = prime * result + ((lockYN == null) ? 0 : lockYN.hashCode());
		result = prime * result + ((maintCatCode == null) ? 0 : maintCatCode.hashCode());
		result = prime * result + ((maintCode == null) ? 0 : maintCode.hashCode());
		result = prime * result + ((maintTaskQty == null) ? 0 : maintTaskQty.hashCode());
		result = prime * result + ((make == null) ? 0 : make.hashCode());
		result = prime * result + ((mileage == null) ? 0 : mileage.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modelYear == null) ? 0 : modelYear.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((poNumber == null) ? 0 : poNumber.hashCode());
		result = prime * result + ((rechargeCode == null) ? 0 : rechargeCode.hashCode());
		result = prime * result + ((reportCategory == null) ? 0 : reportCategory.hashCode());
		result = prime * result + ((reportName == null) ? 0 : reportName.hashCode());
		result = prime * result + ((reportSubCategory == null) ? 0 : reportSubCategory.hashCode());
		result = prime * result + ((serviceCenter == null) ? 0 : serviceCenter.hashCode());
		result = prime * result + ((serviceDate == null) ? 0 : serviceDate.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
		result = prime * result + ((totAmtGross == null) ? 0 : totAmtGross.hashCode());
		result = prime * result + ((totAmtNet == null) ? 0 : totAmtNet.hashCode());
		result = prime * result + ((totAmtTax == null) ? 0 : totAmtTax.hashCode());
		result = prime * result + ((tranExtId == null) ? 0 : tranExtId.hashCode());
		result = prime * result + ((tranIntId == null) ? 0 : tranIntId.hashCode());
		result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
		result = prime * result + ((versionts == null) ? 0 : versionts.hashCode());
		result = prime * result + ((vin == null) ? 0 : vin.hashCode());
		result = prime * result + ((driverState == null) ? 0 : driverState.hashCode());
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
		ClientBillingTransaction other = (ClientBillingTransaction) obj;
		if (accountCode == null) {
			if (other.accountCode != null)
				return false;
		} else if (!accountCode.equals(other.accountCode))
			return false;
		if (accountingPeriod == null) {
			if (other.accountingPeriod != null)
				return false;
		} else if (!accountingPeriod.equals(other.accountingPeriod))
			return false;
		if (acountName == null) {
			if (other.acountName != null)
				return false;
		} else if (!acountName.equals(other.acountName))
			return false;
		if (allocAmtGross == null) {
			if (other.allocAmtGross != null)
				return false;
		} else if (!allocAmtGross.equals(other.allocAmtGross))
			return false;
		if (allocAmtNet == null) {
			if (other.allocAmtNet != null)
				return false;
		} else if (!allocAmtNet.equals(other.allocAmtNet))
			return false;
		if (allocAmtTax == null) {
			if (other.allocAmtTax != null)
				return false;
		} else if (!allocAmtTax.equals(other.allocAmtTax))
			return false;
		if (analysisCodeDesc == null) {
			if (other.analysisCodeDesc != null)
				return false;
		} else if (!analysisCodeDesc.equals(other.analysisCodeDesc))
			return false;
		if (billingPeriod == null) {
			if (other.billingPeriod != null)
				return false;
		} else if (!billingPeriod.equals(other.billingPeriod))
			return false;
		if (cbtId == null) {
			if (other.cbtId != null)
				return false;
		} else if (!cbtId.equals(other.cbtId))
			return false;
		if (clientPoNumber == null) {
			if (other.clientPoNumber != null)
				return false;
		} else if (!clientPoNumber.equals(other.clientPoNumber))
			return false;
		if (costcenter == null) {
			if (other.costcenter != null)
				return false;
		} else if (!costcenter.equals(other.costcenter))
			return false;
		if (costcenterDescription == null) {
			if (other.costcenterDescription != null)
				return false;
		} else if (!costcenterDescription.equals(other.costcenterDescription))
			return false;
		if (docId == null) {
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		if (driverName == null) {
			if (other.driverName != null)
				return false;
		} else if (!driverName.equals(other.driverName))
			return false;
		if (drvId == null) {
			if (other.drvId != null)
				return false;
		} else if (!drvId.equals(other.drvId))
			return false;
		if (fleetRef == null) {
			if (other.fleetRef != null)
				return false;
		} else if (!fleetRef.equals(other.fleetRef))
			return false;
		if (fmsId == null) {
			if (other.fmsId != null)
				return false;
		} else if (!fmsId.equals(other.fmsId))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (invoiceDate == null) {
			if (other.invoiceDate != null)
				return false;
		} else if (!invoiceDate.equals(other.invoiceDate))
			return false;
		if (invoiceDueDate == null) {
			if (other.invoiceDueDate != null)
				return false;
		} else if (!invoiceDueDate.equals(other.invoiceDueDate))
			return false;
		if (invoiceNo == null) {
			if (other.invoiceNo != null)
				return false;
		} else if (!invoiceNo.equals(other.invoiceNo))
			return false;
		if (invoiceType == null) {
			if (other.invoiceType != null)
				return false;
		} else if (!invoiceType.equals(other.invoiceType))
			return false;
		if (licenseNO == null) {
			if (other.licenseNO != null)
				return false;
		} else if (!licenseNO.equals(other.licenseNO))
			return false;
		if (lineAmount == null) {
			if (other.lineAmount != null)
				return false;
		} else if (!lineAmount.equals(other.lineAmount))
			return false;
		if (lineDescription == null) {
			if (other.lineDescription != null)
				return false;
		} else if (!lineDescription.equals(other.lineDescription))
			return false;
		if (lineId == null) {
			if (other.lineId != null)
				return false;
		} else if (!lineId.equals(other.lineId))
			return false;
		if (lineNarrative == null) {
			if (other.lineNarrative != null)
				return false;
		} else if (!lineNarrative.equals(other.lineNarrative))
			return false;
		if (lineNo == null) {
			if (other.lineNo != null)
				return false;
		} else if (!lineNo.equals(other.lineNo))
			return false;
		if (lineTax == null) {
			if (other.lineTax != null)
				return false;
		} else if (!lineTax.equals(other.lineTax))
			return false;
		if (lineTotal == null) {
			if (other.lineTotal != null)
				return false;
		} else if (!lineTotal.equals(other.lineTotal))
			return false;
		if (lockYN == null) {
			if (other.lockYN != null)
				return false;
		} else if (!lockYN.equals(other.lockYN))
			return false;
		if (maintCatCode == null) {
			if (other.maintCatCode != null)
				return false;
		} else if (!maintCatCode.equals(other.maintCatCode))
			return false;
		if (maintCode == null) {
			if (other.maintCode != null)
				return false;
		} else if (!maintCode.equals(other.maintCode))
			return false;
		if (maintTaskQty == null) {
			if (other.maintTaskQty != null)
				return false;
		} else if (!maintTaskQty.equals(other.maintTaskQty))
			return false;
		if (make == null) {
			if (other.make != null)
				return false;
		} else if (!make.equals(other.make))
			return false;
		if (mileage == null) {
			if (other.mileage != null)
				return false;
		} else if (!mileage.equals(other.mileage))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (modelYear == null) {
			if (other.modelYear != null)
				return false;
		} else if (!modelYear.equals(other.modelYear))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (poNumber == null) {
			if (other.poNumber != null)
				return false;
		} else if (!poNumber.equals(other.poNumber))
			return false;
		if (rechargeCode == null) {
			if (other.rechargeCode != null)
				return false;
		} else if (!rechargeCode.equals(other.rechargeCode))
			return false;
		if (reportCategory == null) {
			if (other.reportCategory != null)
				return false;
		} else if (!reportCategory.equals(other.reportCategory))
			return false;
		if (reportName == null) {
			if (other.reportName != null)
				return false;
		} else if (!reportName.equals(other.reportName))
			return false;
		if (reportSubCategory == null) {
			if (other.reportSubCategory != null)
				return false;
		} else if (!reportSubCategory.equals(other.reportSubCategory))
			return false;
		if (serviceCenter == null) {
			if (other.serviceCenter != null)
				return false;
		} else if (!serviceCenter.equals(other.serviceCenter))
			return false;
		if (serviceDate == null) {
			if (other.serviceDate != null)
				return false;
		} else if (!serviceDate.equals(other.serviceDate))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		if (totAmtGross == null) {
			if (other.totAmtGross != null)
				return false;
		} else if (!totAmtGross.equals(other.totAmtGross))
			return false;
		if (totAmtNet == null) {
			if (other.totAmtNet != null)
				return false;
		} else if (!totAmtNet.equals(other.totAmtNet))
			return false;
		if (totAmtTax == null) {
			if (other.totAmtTax != null)
				return false;
		} else if (!totAmtTax.equals(other.totAmtTax))
			return false;
		if (tranExtId == null) {
			if (other.tranExtId != null)
				return false;
		} else if (!tranExtId.equals(other.tranExtId))
			return false;
		if (tranIntId == null) {
			if (other.tranIntId != null)
				return false;
		} else if (!tranIntId.equals(other.tranIntId))
			return false;
		if (unitNo == null) {
			if (other.unitNo != null)
				return false;
		} else if (!unitNo.equals(other.unitNo))
			return false;
		if (versionts == null) {
			if (other.versionts != null)
				return false;
		} else if (!versionts.equals(other.versionts))
			return false;
		if (vin == null) {
			if (other.vin != null)
				return false;
		} else if (!vin.equals(other.vin))
			return false;
		if(driverState == null) {
			if(other.driverState != null)
				return false;
		}else if(!driverState.equals(other.driverState))
				return false;
			
		return true;
	}

	@Override
	public String toString() {
		return "ClientBillingTransaction [accountCode=" + accountCode + ", accountingPeriod=" + accountingPeriod
				+ ", acountName=" + acountName + ", allocAmtGross=" + allocAmtGross + ", allocAmtNet=" + allocAmtNet
				+ ", allocAmtTax=" + allocAmtTax + ", analysisCodeDesc=" + analysisCodeDesc + ", billingPeriod="
				+ billingPeriod + ", cbtId=" + cbtId + ", clientPoNumber=" + clientPoNumber + ", costcenter="
				+ costcenter + ", costcenterDescription=" + costcenterDescription + ", docId=" + docId + ", driverName="
				+ driverName + ", drvId=" + drvId + ", fleetRef=" + fleetRef + ", fmsId=" + fmsId + ", fromDate="
				+ fromDate + ", invoiceDate=" + invoiceDate + ", invoiceDueDate=" + invoiceDueDate + ", invoiceNo="
				+ invoiceNo + ", invoiceType=" + invoiceType + ", licenseNO=" + licenseNO + ", lineAmount=" + lineAmount
				+ ", lineDescription=" + lineDescription + ", lineId=" + lineId + ", lineNarrative=" + lineNarrative
				+ ", lineNo=" + lineNo + ", lineTax=" + lineTax + ", lineTotal=" + lineTotal + ", lockYN=" + lockYN
				+ ", maintCatCode=" + maintCatCode + ", maintCode=" + maintCode + ", maintTaskQty=" + maintTaskQty
				+ ", make=" + make + ", mileage=" + mileage + ", model=" + model + ", modelYear=" + modelYear
				+ ", origin=" + origin + ", poNumber=" + poNumber + ", rechargeCode=" + rechargeCode
				+ ", reportCategory=" + reportCategory + ", reportName=" + reportName + ", reportSubCategory="
				+ reportSubCategory + ", serviceCenter=" + serviceCenter + ", serviceDate=" + serviceDate + ", toDate="
				+ toDate + ", totAmtGross=" + totAmtGross + ", totAmtNet=" + totAmtNet + ", totAmtTax=" + totAmtTax
				+ ", tranExtId=" + tranExtId + ", tranIntId=" + tranIntId + ", unitNo=" + unitNo + ", versionts="
				+ versionts + ", vin=" + vin +", driverState="+ driverState+ "]";
	}

	
}

package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.validation.LeaseValidator;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;

@LeaseValidator
public class LeaseVO implements Serializable {	
	private String internalId;

	private String externalId;
	
	private Long quoId;
			
	private String parentExternalId;

	private String name;
		
	private String productCode;
	
	private String internalProductType;

	private String externalProductType;
	
	private String classification;
	
	private String subsidiary;
		
	private String currency;
	
	private String status;
	
	private Date commencementDate;
	
	private Date endDate;
	
	private Date inServiceDate;
	
	private Date effectiveDate;
	
	private BigDecimal term;
	
	private BigDecimal leaseAssetFairValue;
		
	private BigDecimal leaseAssetCostCarrying;
	
	private BigDecimal capitalContribution;	
		
	private String variablePayment;
		
	private String variableRateIndex;
	
	private String unitNo;
	
	private BigDecimal residualValueGuaranteeBy3rdParty;
	
	private int residualValueGuarantee;
	
	private BigDecimal residualValueGuranteeByLesee;
	
	private BigDecimal residualValueEstimate;
	
	private String collectibilityProbable;
	
	private String leaseTransferOwnership;
	
	private String purchaseOptionReasonablyCertain;
	
	private String underlyingAssestSpecialized;
	
	private String businessUnit;

	private String className;
	
	private String interestType; 

	private BigDecimal rechargeDealerAccessoryAmount;
	
	private BigDecimal rechargeModelAccessoryAmount;
		
	private BigDecimal depositAmount;
	
	private Date tempInserviceDate;
	
	private Long fmsId;
	
	private boolean autoRenewal;
	
	private int autoRenewalTerm;
	
	private BigDecimal prePayment;
	
	private Boolean amendmentBeforeRevision;

	private BigDecimal clientCapitalCost;

	private String clientExternalId;

	private String clientInternalId;
	
	private Double interestRate;

	private boolean cbvImpact;
	
	private Long parentClnId;

	private boolean amendment;

	private Double depreciationFactor;

	private Date actualEndDate;

	private List<LeaseAccountingScheduleVO> leaseAccountingSchedule;
	
	public LeaseVO() {
		this.status = "Pending";
		this.subsidiary = "2";
		this.currency = "USD";
		this.residualValueGuaranteeBy3rdParty = BigDecimal.ZERO;
		this.residualValueGuarantee = 0;
		this.setCollectibilityProbable("Yes");
		this.setLeaseTransferOwnership("No");
		this.setPurchaseOptionReasonablyCertain("No");
		this.setUnderlyingAssestSpecialized("No");
		this.setBusinessUnit("Fleet Solutions");
		this.setClassName("Fleet Lease Revenue");
		this.setDepositAmount(BigDecimal.ZERO);
		this.setAutoRenewal(true);
		this.leaseAccountingSchedule = new ArrayList<>();
		this.amendmentBeforeRevision = Boolean.FALSE;
	}

	public String getInternalId() {
		return internalId;
	}

	public LeaseVO setInternalId(String internalId) {
		this.internalId = internalId;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public LeaseVO setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public Long getQuoId() {
		return quoId;
	}

	public LeaseVO setQuoId(Long quoId) {
		this.quoId = quoId;
		return this;
	}

	public String getParentExternalId() {
		return parentExternalId;
	}

	public LeaseVO setParentExternalId(String parentExternalId) {
		this.parentExternalId = parentExternalId;
		return this;
	}

	public String getName() {
		return name;
	}

	public LeaseVO setName(String name) {
		this.name = name;
		return this;
	}

	public String getProductCode() {
		return productCode;
	}

	public LeaseVO setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getInternalProductType() {
		return internalProductType;
	}

	public LeaseVO setInternalProductType(String internalProductType) {
		this.internalProductType = internalProductType;
		return this;
	}

	public String getExternalProductType() {
		return externalProductType;
	}

	public LeaseVO setExternalProductType(String externalProductType) {
		this.externalProductType = externalProductType;
		return this;
	}

	public String getClassification() {
		return classification;
	}

	public LeaseVO setClassification(String classification) {
		this.classification = classification;
		return this;
	}

	public String getSubsidiary() {
		return subsidiary;
	}

	public LeaseVO setSubsidiary(String subsidiary) {
		this.subsidiary = subsidiary;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public LeaseVO setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public LeaseVO setStatus(String status) {
		this.status = status;
		return this;
	}

	public Date getCommencementDate() {
		return DateUtil.clone(commencementDate);
	}

	public LeaseVO setCommencementDate(Date commencementDate) {
		this.commencementDate = DateUtil.clone(commencementDate);
		return this;
	}

	public Date getEndDate() {
		return DateUtil.clone(endDate);
	}

	public LeaseVO setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
		return this;
	}

	public Date getInServiceDate() {
		return DateUtil.clone(inServiceDate);
	}

	public LeaseVO setInServiceDate(Date inServiceDate) {
		this.inServiceDate = DateUtil.clone(inServiceDate);
		return this;
	}

	public Date getEffectiveDate() {
		return DateUtil.clone(effectiveDate);
	}

	public LeaseVO setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = DateUtil.clone(effectiveDate);
		return this;
	}

	public BigDecimal getTerm() {
		return term;
	}

	public LeaseVO setTerm(BigDecimal term) {
		this.term = term;
		return this;
	}

	public BigDecimal getLeaseAssetFairValue() {
		return DataUtil.nvl(leaseAssetFairValue, BigDecimal.ZERO);
	}

	public LeaseVO setLeaseAssetFairValue(BigDecimal leaseAssetFairValue) {
		this.leaseAssetFairValue = DataUtil.nvl(leaseAssetFairValue, BigDecimal.ZERO);
		return this;
	}

	public BigDecimal getLeaseAssetCostCarrying() {
		return DataUtil.nvl(leaseAssetCostCarrying, BigDecimal.ZERO);
	}

	public LeaseVO setLeaseAssetCostCarrying(BigDecimal leaseAssetCostCarrying) {
		this.leaseAssetCostCarrying = DataUtil.nvl(leaseAssetCostCarrying, BigDecimal.ZERO); 
		return this;
	}

	public BigDecimal getCapitalContribution() {
		return DataUtil.nvl(capitalContribution, BigDecimal.ZERO);
	}

	public LeaseVO setCapitalContribution(BigDecimal capitalContribution) {
		this.capitalContribution = DataUtil.nvl(capitalContribution, BigDecimal.ZERO);
		return this;
	}

	public BigDecimal getResidualValueGuranteeByLesee() {
		return DataUtil.nvl(residualValueGuranteeByLesee, BigDecimal.ZERO);
	}

	public LeaseVO setResidualValueGuranteeByLesee(BigDecimal residualValueGuranteeByLesee) {
		this.residualValueGuranteeByLesee = DataUtil.nvl(residualValueGuranteeByLesee, BigDecimal.ZERO);
		return this;
	}
	
	public BigDecimal getResidualValueEstimate() {
		return DataUtil.nvl(residualValueEstimate, BigDecimal.ZERO);
	}

	public LeaseVO setResidualValueEstimate(BigDecimal residualValueEstimate) {
		this.residualValueEstimate = DataUtil.nvl(residualValueEstimate, BigDecimal.ZERO);
		return this;
	}

	public String isVariablePayment() {
		return variablePayment;
	}

	public LeaseVO setVariablePayment(String variablePayment) {
		this.variablePayment = variablePayment;
		return this;
	}

	public String getVariableRateIndex() {
		return variableRateIndex;
	}

	public LeaseVO setVariableRateIndex(String variableRateIndex) {
		this.variableRateIndex = variableRateIndex;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public LeaseVO setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public BigDecimal getResidualValueGuaranteeBy3rdParty() {
		return residualValueGuaranteeBy3rdParty;
	}

	public LeaseVO setResidualValueGuaranteeBy3rdParty(BigDecimal residualValueGuaranteeBy3rdParty) {
		this.residualValueGuaranteeBy3rdParty = residualValueGuaranteeBy3rdParty;
		return this;
	}

	@Deprecated(forRemoval = true)
	public int getResidualValueGuarantee() {
		return residualValueGuarantee;
	}

	@Deprecated(forRemoval = true)
	public LeaseVO setResidualValueGuarantee(int residualValueGuarantee) {
		this.residualValueGuarantee = residualValueGuarantee;
		return this;
	}

	public String getVariablePayment() {
		return variablePayment;
	}

	public String getCollectibilityProbable() {
		return collectibilityProbable;
	}

	public LeaseVO setCollectibilityProbable(String collectibilityProbable) {
		this.collectibilityProbable = collectibilityProbable;
		return this;
	}

	public String getLeaseTransferOwnership() {
		return leaseTransferOwnership;
	}

	public LeaseVO setLeaseTransferOwnership(String leaseTransferOwnership) {
		this.leaseTransferOwnership = leaseTransferOwnership;
		return this;
	}

	public String getPurchaseOptionReasonablyCertain() {
		return purchaseOptionReasonablyCertain;
	}

	public LeaseVO setPurchaseOptionReasonablyCertain(String purchaseOptionReasonablyCertain) {
		this.purchaseOptionReasonablyCertain = purchaseOptionReasonablyCertain;
		return this;
	}

	public String getUnderlyingAssestSpecialized() {
		return underlyingAssestSpecialized;
	}

	public LeaseVO setUnderlyingAssestSpecialized(String underlyingAssestSpecialized) {
		this.underlyingAssestSpecialized = underlyingAssestSpecialized;
		return this;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public LeaseVO setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public LeaseVO setClassName(String className) {
		this.className = className;
		return this;
	}

	public String getInterestType() {
		return interestType;
	}

	public LeaseVO setInterestType(String interestType) {
		this.interestType = interestType;
		return this;
	}

	public BigDecimal getRechargeDealerAccessoryAmount() {
		return rechargeDealerAccessoryAmount;
	}

	public LeaseVO setRechargeDealerAccessoryAmount(BigDecimal rechargeDealerAccessoryAmount) {
		this.rechargeDealerAccessoryAmount = rechargeDealerAccessoryAmount;
		return this;
	}

	public BigDecimal getRechargeModelAccessoryAmount() {
		return rechargeModelAccessoryAmount;
	}

	public LeaseVO setRechargeModelAccessoryAmount(BigDecimal rechargeModelAccessoryAmount) {
		this.rechargeModelAccessoryAmount = rechargeModelAccessoryAmount;
		return this;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public LeaseVO setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
		return this;
	}

	public Date getTempInserviceDate() {
		return DateUtil.clone(tempInserviceDate);
	}

	public LeaseVO setTempInserviceDate(Date tempInserviceDate) {
		this.tempInserviceDate = DateUtil.clone(tempInserviceDate);
		return this;
	}

	public List<LeaseAccountingScheduleVO> getLeaseAccountingSchedule() {
		return leaseAccountingSchedule;
	}

	public LeaseVO setLeaseAccountingSchedule(List<LeaseAccountingScheduleVO> leaseAccountingSchedule) {
		this.leaseAccountingSchedule = leaseAccountingSchedule;
		return this;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public LeaseVO setFmsId(Long fmsId) {
		this.fmsId = fmsId;
		return this;
	}

	public boolean isAutoRenewal() {
		return autoRenewal;
	}

	public LeaseVO setAutoRenewal(boolean autoRenewal) {
		this.autoRenewal = autoRenewal;
		return this;
	}

	public int getAutoRenewalTerm() {
		return autoRenewalTerm;
	}

	public LeaseVO setAutoRenewalTerm(int autoRenewalTerm) {
		this.autoRenewalTerm = autoRenewalTerm;
		return this;
	}

	public BigDecimal getPrePayment() {
		return prePayment;
	}

	public LeaseVO setPrePayment(BigDecimal prePayment) {
		this.prePayment = prePayment;
		return this;
	}

	public Boolean isAmendmentBeforeRevision() {
		return amendmentBeforeRevision;
	}

	public LeaseVO setAmendmentBeforeRevision(Boolean amendmentBeforeRevision) {
		this.amendmentBeforeRevision = amendmentBeforeRevision;
		return this;
	}

    public BigDecimal getClientCapitalCost() {
		return clientCapitalCost;
	}

	public LeaseVO setClientCapitalCost(BigDecimal clientCapitalCost) {
		this.clientCapitalCost = clientCapitalCost;
		return this;
	}

	public String getClientExternalId() {
		return clientExternalId;
	}

	public LeaseVO setClientExternalId(String clientExternalId) {
		this.clientExternalId = clientExternalId;
		return this;
	}

	public String getClientInternalId() {
		return clientInternalId;
	}

	public LeaseVO setClientInternalId(String clientInternalId) {
		this.clientInternalId = clientInternalId;
		return this;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public LeaseVO setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
		return this;
	}

	public boolean isCbvImpact() {
		return cbvImpact;
	}

	public LeaseVO setCbvImpact(boolean cbvImpact) {
		this.cbvImpact = cbvImpact;
		return this;
	}

	public Long getParentClnId() {
		return parentClnId;
	}

	public LeaseVO setParentClnId(Long parentClnId) {
		this.parentClnId = parentClnId;
		return this;
	}

	public boolean isAmendment() {
		return amendment;
	}

	public LeaseVO setAmendment(boolean amendment) {
		this.amendment = amendment;
		return this;
	}

	public Double getDepreciationFactor() {
		return depreciationFactor;
	}

	public LeaseVO setDepreciationFactor(Double depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
		return this;
	}

	public Date getActualEndDate() {
		return DateUtil.clone(actualEndDate);
	}

	public LeaseVO setActualEndDate(Date actualEndDate) {
		this.actualEndDate = DateUtil.clone(actualEndDate);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((internalId == null) ? 0 : internalId.hashCode());
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((quoId == null) ? 0 : quoId.hashCode());
		result = prime * result + ((parentExternalId == null) ? 0 : parentExternalId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((internalProductType == null) ? 0 : internalProductType.hashCode());
		result = prime * result + ((externalProductType == null) ? 0 : externalProductType.hashCode());
		result = prime * result + ((classification == null) ? 0 : classification.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((commencementDate == null) ? 0 : commencementDate.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((inServiceDate == null) ? 0 : inServiceDate.hashCode());
		result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((leaseAssetFairValue == null) ? 0 : leaseAssetFairValue.hashCode());
		result = prime * result + ((leaseAssetCostCarrying == null) ? 0 : leaseAssetCostCarrying.hashCode());
		result = prime * result + ((capitalContribution == null) ? 0 : capitalContribution.hashCode());
		result = prime * result + ((variablePayment == null) ? 0 : variablePayment.hashCode());
		result = prime * result + ((variableRateIndex == null) ? 0 : variableRateIndex.hashCode());
		result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
		result = prime * result
				+ ((residualValueGuaranteeBy3rdParty == null) ? 0 : residualValueGuaranteeBy3rdParty.hashCode());
		result = prime * result + residualValueGuarantee;
		result = prime * result
				+ ((residualValueGuranteeByLesee == null) ? 0 : residualValueGuranteeByLesee.hashCode());
		result = prime * result + ((residualValueEstimate == null) ? 0 : residualValueEstimate.hashCode());
		result = prime * result + ((collectibilityProbable == null) ? 0 : collectibilityProbable.hashCode());
		result = prime * result + ((leaseTransferOwnership == null) ? 0 : leaseTransferOwnership.hashCode());
		result = prime * result
				+ ((purchaseOptionReasonablyCertain == null) ? 0 : purchaseOptionReasonablyCertain.hashCode());
		result = prime * result + ((underlyingAssestSpecialized == null) ? 0 : underlyingAssestSpecialized.hashCode());
		result = prime * result + ((businessUnit == null) ? 0 : businessUnit.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((interestType == null) ? 0 : interestType.hashCode());
		result = prime * result
				+ ((rechargeDealerAccessoryAmount == null) ? 0 : rechargeDealerAccessoryAmount.hashCode());
		result = prime * result
				+ ((rechargeModelAccessoryAmount == null) ? 0 : rechargeModelAccessoryAmount.hashCode());
		result = prime * result + ((depositAmount == null) ? 0 : depositAmount.hashCode());
		result = prime * result + ((tempInserviceDate == null) ? 0 : tempInserviceDate.hashCode());
		result = prime * result + ((fmsId == null) ? 0 : fmsId.hashCode());
		result = prime * result + (autoRenewal ? 1231 : 1237);
		result = prime * result + autoRenewalTerm;
		result = prime * result + ((prePayment == null) ? 0 : prePayment.hashCode());
		result = prime * result + ((amendmentBeforeRevision == null) ? 0 : amendmentBeforeRevision.hashCode());
		result = prime * result + ((clientCapitalCost == null) ? 0 : clientCapitalCost.hashCode());
		result = prime * result + ((clientExternalId == null) ? 0 : clientExternalId.hashCode());
		result = prime * result + ((clientInternalId == null) ? 0 : clientInternalId.hashCode());
		result = prime * result + ((interestRate == null) ? 0 : interestRate.hashCode());
		result = prime * result + (cbvImpact ? 1231 : 1237);
		result = prime * result + ((parentClnId == null) ? 0 : parentClnId.hashCode());
		result = prime * result + (amendment ? 1231 : 1237);
		result = prime * result + ((depreciationFactor == null) ? 0 : depreciationFactor.hashCode());
		result = prime * result + ((actualEndDate == null) ? 0 : actualEndDate.hashCode());
		result = prime * result + ((leaseAccountingSchedule == null) ? 0 : leaseAccountingSchedule.hashCode());
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
		LeaseVO other = (LeaseVO) obj;
		if (internalId == null) {
			if (other.internalId != null)
				return false;
		} else if (!internalId.equals(other.internalId))
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (quoId == null) {
			if (other.quoId != null)
				return false;
		} else if (!quoId.equals(other.quoId))
			return false;
		if (parentExternalId == null) {
			if (other.parentExternalId != null)
				return false;
		} else if (!parentExternalId.equals(other.parentExternalId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (internalProductType == null) {
			if (other.internalProductType != null)
				return false;
		} else if (!internalProductType.equals(other.internalProductType))
			return false;
		if (externalProductType == null) {
			if (other.externalProductType != null)
				return false;
		} else if (!externalProductType.equals(other.externalProductType))
			return false;
		if (classification == null) {
			if (other.classification != null)
				return false;
		} else if (!classification.equals(other.classification))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (commencementDate == null) {
			if (other.commencementDate != null)
				return false;
		} else if (!commencementDate.equals(other.commencementDate))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (inServiceDate == null) {
			if (other.inServiceDate != null)
				return false;
		} else if (!inServiceDate.equals(other.inServiceDate))
			return false;
		if (effectiveDate == null) {
			if (other.effectiveDate != null)
				return false;
		} else if (!effectiveDate.equals(other.effectiveDate))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		if (leaseAssetFairValue == null) {
			if (other.leaseAssetFairValue != null)
				return false;
		} else if (!leaseAssetFairValue.equals(other.leaseAssetFairValue))
			return false;
		if (leaseAssetCostCarrying == null) {
			if (other.leaseAssetCostCarrying != null)
				return false;
		} else if (!leaseAssetCostCarrying.equals(other.leaseAssetCostCarrying))
			return false;
		if (capitalContribution == null) {
			if (other.capitalContribution != null)
				return false;
		} else if (!capitalContribution.equals(other.capitalContribution))
			return false;
		if (variablePayment == null) {
			if (other.variablePayment != null)
				return false;
		} else if (!variablePayment.equals(other.variablePayment))
			return false;
		if (variableRateIndex == null) {
			if (other.variableRateIndex != null)
				return false;
		} else if (!variableRateIndex.equals(other.variableRateIndex))
			return false;
		if (unitNo == null) {
			if (other.unitNo != null)
				return false;
		} else if (!unitNo.equals(other.unitNo))
			return false;
		if (residualValueGuaranteeBy3rdParty == null) {
			if (other.residualValueGuaranteeBy3rdParty != null)
				return false;
		} else if (!residualValueGuaranteeBy3rdParty.equals(other.residualValueGuaranteeBy3rdParty))
			return false;
		if (residualValueGuarantee != other.residualValueGuarantee)
			return false;
		if (residualValueGuranteeByLesee == null) {
			if (other.residualValueGuranteeByLesee != null)
				return false;
		} else if (!residualValueGuranteeByLesee.equals(other.residualValueGuranteeByLesee))
			return false;
		if (residualValueEstimate == null) {
			if (other.residualValueEstimate != null)
				return false;
		} else if (!residualValueEstimate.equals(other.residualValueEstimate))
			return false;
		if (collectibilityProbable == null) {
			if (other.collectibilityProbable != null)
				return false;
		} else if (!collectibilityProbable.equals(other.collectibilityProbable))
			return false;
		if (leaseTransferOwnership == null) {
			if (other.leaseTransferOwnership != null)
				return false;
		} else if (!leaseTransferOwnership.equals(other.leaseTransferOwnership))
			return false;
		if (purchaseOptionReasonablyCertain == null) {
			if (other.purchaseOptionReasonablyCertain != null)
				return false;
		} else if (!purchaseOptionReasonablyCertain.equals(other.purchaseOptionReasonablyCertain))
			return false;
		if (underlyingAssestSpecialized == null) {
			if (other.underlyingAssestSpecialized != null)
				return false;
		} else if (!underlyingAssestSpecialized.equals(other.underlyingAssestSpecialized))
			return false;
		if (businessUnit == null) {
			if (other.businessUnit != null)
				return false;
		} else if (!businessUnit.equals(other.businessUnit))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (interestType == null) {
			if (other.interestType != null)
				return false;
		} else if (!interestType.equals(other.interestType))
			return false;
		if (rechargeDealerAccessoryAmount == null) {
			if (other.rechargeDealerAccessoryAmount != null)
				return false;
		} else if (!rechargeDealerAccessoryAmount.equals(other.rechargeDealerAccessoryAmount))
			return false;
		if (rechargeModelAccessoryAmount == null) {
			if (other.rechargeModelAccessoryAmount != null)
				return false;
		} else if (!rechargeModelAccessoryAmount.equals(other.rechargeModelAccessoryAmount))
			return false;
		if (depositAmount == null) {
			if (other.depositAmount != null)
				return false;
		} else if (!depositAmount.equals(other.depositAmount))
			return false;
		if (tempInserviceDate == null) {
			if (other.tempInserviceDate != null)
				return false;
		} else if (!tempInserviceDate.equals(other.tempInserviceDate))
			return false;
		if (fmsId == null) {
			if (other.fmsId != null)
				return false;
		} else if (!fmsId.equals(other.fmsId))
			return false;
		if (autoRenewal != other.autoRenewal)
			return false;
		if (autoRenewalTerm != other.autoRenewalTerm)
			return false;
		if (prePayment == null) {
			if (other.prePayment != null)
				return false;
		} else if (!prePayment.equals(other.prePayment))
			return false;
		if (amendmentBeforeRevision == null) {
			if (other.amendmentBeforeRevision != null)
				return false;
		} else if (!amendmentBeforeRevision.equals(other.amendmentBeforeRevision))
			return false;
		if (clientCapitalCost == null) {
			if (other.clientCapitalCost != null)
				return false;
		} else if (!clientCapitalCost.equals(other.clientCapitalCost))
			return false;
		if (clientExternalId == null) {
			if (other.clientExternalId != null)
				return false;
		} else if (!clientExternalId.equals(other.clientExternalId))
			return false;
		if (clientInternalId == null) {
			if (other.clientInternalId != null)
				return false;
		} else if (!clientInternalId.equals(other.clientInternalId))
			return false;
		if (interestRate == null) {
			if (other.interestRate != null)
				return false;
		} else if (!interestRate.equals(other.interestRate))
			return false;
		if (cbvImpact != other.cbvImpact)
			return false;
		if (parentClnId == null) {
			if (other.parentClnId != null)
				return false;
		} else if (!parentClnId.equals(other.parentClnId))
			return false;
		if (amendment != other.amendment)
			return false;
		if (depreciationFactor == null) {
			if (other.depreciationFactor != null)
				return false;
		} else if (!depreciationFactor.equals(other.depreciationFactor))
			return false;
		if (actualEndDate == null) {
			if (other.actualEndDate != null)
				return false;
		} else if (!actualEndDate.equals(other.actualEndDate))
			return false;
		if (leaseAccountingSchedule == null) {
			if (other.leaseAccountingSchedule != null)
				return false;
		} else if (!leaseAccountingSchedule.equals(other.leaseAccountingSchedule))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LeaseVO [internalId=" + internalId + ", externalId=" + externalId + ", quoId=" + quoId
				+ ", parentExternalId=" + parentExternalId + ", name=" + name + ", productCode=" + productCode
				+ ", internalProductType=" + internalProductType + ", externalProductType=" + externalProductType
				+ ", classification=" + classification + ", subsidiary=" + subsidiary + ", currency=" + currency
				+ ", status=" + status + ", commencementDate=" + commencementDate + ", endDate=" + endDate
				+ ", inServiceDate=" + inServiceDate + ", effectiveDate=" + effectiveDate + ", term=" + term
				+ ", leaseAssetFairValue=" + leaseAssetFairValue + ", leaseAssetCostCarrying=" + leaseAssetCostCarrying
				+ ", capitalContribution=" + capitalContribution + ", variablePayment=" + variablePayment
				+ ", variableRateIndex=" + variableRateIndex + ", unitNo=" + unitNo
				+ ", residualValueGuaranteeBy3rdParty=" + residualValueGuaranteeBy3rdParty + ", residualValueGuarantee="
				+ residualValueGuarantee + ", residualValueGuranteeByLesee=" + residualValueGuranteeByLesee
				+ ", residualValueEstimate=" + residualValueEstimate + ", collectibilityProbable="
				+ collectibilityProbable + ", leaseTransferOwnership=" + leaseTransferOwnership
				+ ", purchaseOptionReasonablyCertain=" + purchaseOptionReasonablyCertain
				+ ", underlyingAssestSpecialized=" + underlyingAssestSpecialized + ", businessUnit=" + businessUnit
				+ ", className=" + className + ", interestType=" + interestType + ", rechargeDealerAccessoryAmount="
				+ rechargeDealerAccessoryAmount + ", rechargeModelAccessoryAmount=" + rechargeModelAccessoryAmount
				+ ", depositAmount=" + depositAmount + ", tempInserviceDate=" + tempInserviceDate + ", fmsId=" + fmsId
				+ ", autoRenewal=" + autoRenewal + ", autoRenewalTerm=" + autoRenewalTerm + ", prePayment=" + prePayment
				+ ", amendmentBeforeRevision=" + amendmentBeforeRevision + ", clientCapitalCost=" + clientCapitalCost
				+ ", clientExternalId=" + clientExternalId + ", clientInternalId=" + clientInternalId
				+ ", interestRate=" + interestRate + ", cbvImpact=" + cbvImpact + ", parentClnId=" + parentClnId
				+ ", amendment=" + amendment + ", depreciationFactor=" + depreciationFactor + ", actualEndDate="
				+ actualEndDate + ", leaseAccountingSchedule=" + leaseAccountingSchedule + "]";
	}
	
}

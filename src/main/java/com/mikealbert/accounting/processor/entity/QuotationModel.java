package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the QUOTATION_MODELS database table.
 * 
 */
@Entity
@Table(name="QUOTATION_MODELS")
public class QuotationModel extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMD_SEQ")
	@SequenceGenerator(name="QMD_SEQ", sequenceName="QMD_SEQ", allocationSize=1)
	@Column(name="QMD_ID")
	private Long qmdId;	

	@Column(name="FMS_FMS_ID")
	private Long fmsFmsId;		
		
	@Column(name="UNIT_NO")
	private String unitNo;

	@Column(name="CAPITAL_CONTRIBUTION")
	private BigDecimal capitalContribution;
			
	@Column(name="RESIDUAL_VALUE")
	private BigDecimal residualValue;
			
	@Column(name="QUOTE_STATUS")
	private int quoteStatus;

	@Column(name="CONTRACT_PERIOD")
	private BigDecimal contractPeriod;

	@Column(name="REVISION_NO")
	private int revisionNo;
	
	@Column(name="AMENDMENT_EFF_DATE")
	private Date amendmentEffDate;
	
	@Column(name="CONTRACT_CHANGE_EVENT_PERIOD")
	private BigDecimal contractChangeEventPeriod;

	@Column(name="INTEREST_RATE", precision = 9, scale =  3)
	private BigDecimal interestRate;

	@Column(name="CLN_CLN_ID ")
	private Long clnClnId ;

	@Column(name="DEPRECIATION_FACTOR", precision = 30, scale =  7)
	private BigDecimal depreciationfactor;	

	@JoinColumn(name = "QUO_QUO_ID", referencedColumnName = "QUO_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private Quotation quotation;



	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public Long getFmsFmsId() {
		return fmsFmsId;
	}

	public void setFmsFmsId(Long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public Quotation getQuotation() {
		return quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}

	public BigDecimal getCapitalContribution() {
		return capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
		this.capitalContribution = capitalContribution;
	}

	public BigDecimal getResidualValue() {
		return residualValue;
	}

	public void setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
	}

	public int getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(int quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public BigDecimal getContractPeriod() {
		return contractPeriod;
	}

	public void setContractPeriod(BigDecimal contractPeriod) {
		this.contractPeriod = contractPeriod;
	}

	public int getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(int revisionNo) {
		this.revisionNo = revisionNo;
	}

	public Date getAmendmentEffDate() {
		return DateUtil.clone(amendmentEffDate);
	}

	public void setAmendmentEffDate(Date amendmentEffDate) {
		this.amendmentEffDate = DateUtil.clone(amendmentEffDate);
	}

	public BigDecimal getContractChangeEventPeriod() {
		return contractChangeEventPeriod;
	}

	public void setContractChangeEventPeriod(BigDecimal contractChangeEventPeriod) {
		this.contractChangeEventPeriod = contractChangeEventPeriod;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public Long getClnClnId() { return clnClnId; }

	public void setClnClnId(Long clnClnId) {
		this.clnClnId = clnClnId;
	}

	public BigDecimal getDepreciationfactor() {
		return depreciationfactor;
	}

	public void setDepreciationfactor(BigDecimal depreciationfactor) {
		this.depreciationfactor = depreciationfactor;
	}
	
}
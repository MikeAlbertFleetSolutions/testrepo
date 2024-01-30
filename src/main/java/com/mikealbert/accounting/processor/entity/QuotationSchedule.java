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
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="QUOTATION_SCHEDULES")
public class QuotationSchedule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QSC_SEQ")
	@SequenceGenerator(name="QSC_SEQ", sequenceName="QSC_SEQ", allocationSize=1)
	
	@Column(name="QSC_ID")
	private Long qscId;	
	
    @JoinColumn(name = "QEL_QEL_ID", referencedColumnName = "QEL_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationElement quotationElement;

	@Column(name="AMOUNT")
	private BigDecimal amount;	
	
	@Column(name="PAYMENT_IND")
	private String paymentInd;	
	
	@Column(name="TRANS_DATE")
	private Date transDate;	
	
	@Column(name="NO_OF_UNITS")
	private Integer noOfUnit;
	
	public Long getQscId() {
		return qscId;
	}

	public void setQscId(Long qscId) {
		this.qscId = qscId;
	}

	public QuotationElement getQuotationElement() {
		return quotationElement;
	}

	public void setQuotationElement(QuotationElement quotationElement) {
		this.quotationElement = quotationElement;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentInd() {
		return paymentInd;
	}

	public void setPaymentInd(String paymentInd) {
		this.paymentInd = paymentInd;
	}

	public Date getTransDate() {
		return DateUtil.clone(transDate);
	}

	public void setTransDate(Date transDate) {
		this.transDate = DateUtil.clone(transDate);
	}

	public Integer getNoOfUnit() {
		return noOfUnit;
	}

	public void setNoOfUnit(Integer noOfUnit) {
		this.noOfUnit = noOfUnit;
	}
	
}
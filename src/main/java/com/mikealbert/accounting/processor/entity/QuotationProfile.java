package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTATIONS database table.
 * 
 */
@Entity
@Table(name="QUOTATION_PROFILES")
public class QuotationProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QPR_SEQ")
	@SequenceGenerator(name="QPR_SEQ", sequenceName="QPR_SEQ", allocationSize=1)
	
	@Column(name="QPR_ID")
	private long qprId;
	
	@Column(name="PRD_PRODUCT_CODE")
	private String prdProductCode;
	
	@Column(name="ITC_INTEREST_TYPE")
	private String itcInterestType;
	
	@Column(name="VARIABLE_RATE")
	private String variableRate;
	
	public long getQprId() {
		return qprId;
	}

	public void setQprId(long qprId) {
		this.qprId = qprId;
	}

	public String getPrdProductCode() {
		return prdProductCode;
	}

	public void setPrdProductCode(String prdProductCode) {
		this.prdProductCode = prdProductCode;
	}

	public String getItcInterestType() {
		return itcInterestType;
	}

	public void setItcInterestType(String itcInterestType) {
		this.itcInterestType = itcInterestType;
	}

	public String getVariableRate() {
		return variableRate;
	}

	public void setVariableRate(String variableRate) {
		this.variableRate = variableRate;
	}

}
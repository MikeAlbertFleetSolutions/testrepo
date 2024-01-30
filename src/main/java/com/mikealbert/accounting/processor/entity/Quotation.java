package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

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


/**
 * The persistent class for the QUOTATIONS database table.
 * 
 */
@Entity
@Table(name="QUOTATIONS")
public class Quotation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QUO_SEQ")
	@SequenceGenerator(name="QUO_SEQ", sequenceName="QUO_SEQ", allocationSize=1)
	
	@Column(name="QUO_ID")
	private long quoId;

	@Column(name="DRV_DRV_ID")
	private Long drvDrvId;	

	@Column(name="ACCOUNT_CODE")
	private String accountCode;
	
	@JoinColumn(name = "QPR_QPR_ID", referencedColumnName = "QPR_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationProfile quotationProfile;

	public long getQuoId() {
		return quoId;
	}

	public void setQuoId(long quoId) {
		this.quoId = quoId;
	}

	public Long getDrvDrvId() {
		return drvDrvId;
	}

	public void setDrvDrvId(Long drvDrvId) {
		this.drvDrvId = drvDrvId;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public QuotationProfile getQuotationProfile() {
		return quotationProfile;
	}

	public void setQuotationProfile(QuotationProfile quotationProfile) {
		this.quotationProfile = quotationProfile;
	}	


}
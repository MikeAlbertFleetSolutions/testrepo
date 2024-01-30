package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the EXT_ACC_BANK_ACCS database table.
 * 
 */
@Entity
@Table(name="EXT_ACC_BANK_ACCS")
public class ExtAccBankAcc implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EABA_SEQ")    
    @SequenceGenerator(name="EABA_SEQ", sequenceName="EABA_SEQ", allocationSize=1)		
	@Column(name="EABA_ID")
	private long eabaId;

	@Column(name="BANK_ACCOUNT_NAME")
	private String bankAccountName;

	@Column(name="BANK_ACCOUNT_NUMBER")
	private String bankAccountNumber;

	@Column(name="BANK_NAME")
	private String bankName;

	@Column(name="BANK_SORT_CODE")
	private String bankSortCode;

	@Column(name="DEFAULT_IND")
	private String defaultInd;

	@Column(name="EABA_BA_ID")
	private Long eabaBaId;
	
	@JoinColumns({
		@JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
		@JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
		@JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
	@ManyToOne(fetch = FetchType.LAZY)
	private ExternalAccount externalAccount;	
    
	public ExtAccBankAcc() {}

	public long getEabaId() {
		return this.eabaId;
	}

	public void setEabaId(long eabaId) {
		this.eabaId = eabaId;
	}

	public String getBankAccountName() {
		return this.bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNumber() {
		return this.bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankSortCode() {
		return this.bankSortCode;
	}

	public void setBankSortCode(String bankSortCode) {
		this.bankSortCode = bankSortCode;
	}

	public String getDefaultInd() {
		return this.defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public Long getEabaBaId() {
		return this.eabaBaId;
	}

	public void setEabaBaId(Long eabaBaId) {
		this.eabaBaId = eabaBaId;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	
}
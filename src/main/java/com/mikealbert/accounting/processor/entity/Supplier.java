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

@Entity
@Table(name = "SUPPLIERS")
public class Supplier implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUP_SEQ")    
    @SequenceGenerator(name="SUP_SEQ", sequenceName="SUP_SEQ", allocationSize=1)	
	@Column(name = "SUP_ID")
	private long supId;// Add SequenceGenerator and other field mapping as needed


	@Column(name = "INACTIVE_IND")
	private String inactiveInd;
	
	@JoinColumns({
		@JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
		@JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
		@JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
	@ManyToOne(fetch = FetchType.LAZY)
	private ExternalAccount externalAccount;
	
	public long getSupId() {
		return supId;
	}

	public void setSupId(long supId) {
		this.supId = supId;
	}

	public String getInactiveInd() {
		return inactiveInd;
	}

	public void setInactiveInd(String inactiveInd) {
		this.inactiveInd = inactiveInd;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}
	
}
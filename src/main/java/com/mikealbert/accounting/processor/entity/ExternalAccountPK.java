package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The primary key class for the EXTERNAL_ACCOUNTS database table.
 * 
 */
@Embeddable
public class ExternalAccountPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID", insertable=false, updatable=false)
	private long cId;

	@Column(name="ACCOUNT_TYPE")
	private String accountType;

	@Column(name="ACCOUNT_CODE")
	private String accountCode;

	public ExternalAccountPK() {}
		
	public ExternalAccountPK(long cId, String accountType, String accountCode) {
		super();
		this.cId = cId;
		this.accountType = accountType;
		this.accountCode = accountCode;
	}

	public long getCId() {
		return this.cId;
	}
	public void setCId(long cId) {
		this.cId = cId;
	}
	public String getAccountType() {
		return this.accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountCode() {
		return this.accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ExternalAccountPK)) {
			return false;
		}
		ExternalAccountPK castOther = (ExternalAccountPK)other;
		return 
			(this.cId == castOther.cId)
			&& this.accountType.equals(castOther.accountType)
			&& this.accountCode.equals(castOther.accountCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.accountType.hashCode();
		hash = hash * prime + this.accountCode.hashCode();
		
		return hash;
	}
}
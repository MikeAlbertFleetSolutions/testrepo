package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The primary key class for the COST_CENTRE_CODES database table.
 * 
 */
@Embeddable
public class CostCenterCodePK implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="COST_CENTRE_CODE")
	private String costCenterCode;
	
	@Column(name="EA_C_ID", insertable=false, updatable=false)
	private long cId;

	@Column(name="EA_ACCOUNT_TYPE")
	private String accountType;

	@Column(name="EA_ACCOUNT_CODE")
	private String accountCode;

	public CostCenterCodePK() {}

	@Override
	public String toString() {
		return "CostCenterCodePK [costCenterCode=" + costCenterCode + ", cId=" + cId + ", accountType=" + accountType
				+ ", accountCode=" + accountCode + "]";
	}
			
	
}
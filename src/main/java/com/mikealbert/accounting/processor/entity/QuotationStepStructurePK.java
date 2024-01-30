package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the QUOTATION_STEP_STRUCTURE database table.
 * 
 */
@Embeddable
public class QuotationStepStructurePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="QMD_QMD_ID")
	private BigDecimal qmdQmdId;

	@Column(name="FROM_PERIOD")
	private Long fromPeriod;

	public QuotationStepStructurePK() {
	}

	public BigDecimal getQmdQmdId() {
		return qmdQmdId;
	}

	public void setQmdQmdId(BigDecimal qmdQmdId) {
		this.qmdQmdId = qmdQmdId;
	}

	public Long getFromPeriod() {
		return this.fromPeriod;
	}
	
	public void setFromPeriod(Long fromPeriod) {
		this.fromPeriod = fromPeriod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fromPeriod ^ (fromPeriod >>> 32));
		result = prime * result + ((qmdQmdId == null) ? 0 : qmdQmdId.hashCode());
		return result;
	}
}
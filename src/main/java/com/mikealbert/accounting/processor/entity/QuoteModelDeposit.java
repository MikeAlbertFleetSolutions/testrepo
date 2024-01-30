package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the QUOTE_MODEL_DEPOSITS database table.
 * 
 */
@Entity
@Table(name="QUOTE_MODEL_DEPOSITS")
public class QuoteModelDeposit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="QDP_ID")
	private long qdpId;

    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationModel quotationModel;	

	@Column(name="VALUE")
	private BigDecimal value;

	public QuoteModelDeposit() {
	}

	public long getQdpId() {
		return this.qdpId;
	}

	public void setQdpId(long qdpId) {
		this.qdpId = qdpId;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
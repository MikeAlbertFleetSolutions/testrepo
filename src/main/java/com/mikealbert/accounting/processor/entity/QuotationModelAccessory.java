package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="QUOTATION_MODEL_ACCESSORIES")
public class QuotationModelAccessory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMA_SEQ")
	@SequenceGenerator(name="QMA_SEQ", sequenceName="QMA_SEQ", allocationSize=1)
	
	@Column(name="QMA_ID")
	private Long qmaId;	
	
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationModel quotationModel;	
	
	@Column(name="RECHARGE_AMOUNT")
	private BigDecimal rechargeAmount;

	@Column(name="DRIVER_RECHARGE_YN")
	private String driverRechargeYn;

	public Long getQmaId() {
		return qmaId;
	}

	public void setQmaId(Long qmaId) {
		this.qmaId = qmaId;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getDriverRechargeYn() {
		return driverRechargeYn;
	}

	public void setDriverRechargeYn(String driverRechargeYn) {
		this.driverRechargeYn = driverRechargeYn;
	}
	
}
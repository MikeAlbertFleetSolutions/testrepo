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
@Table(name="QUOTATION_DEALER_ACCESSORIES")
public class QuotationDealerAccessory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QDA_SEQ")
	@SequenceGenerator(name="QDA_SEQ", sequenceName="QDA_SEQ", allocationSize=1)
	
	@Column(name="QDA_ID")
	private Long qdaId;	
	
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationModel quotationModel;	
	
	@Column(name="RECHARGE_AMOUNT")
	private BigDecimal rechargeAmount;
	
	@Column(name="DAC_DAC_ID")
	private Long dacDacId;
	
	@Column(name="DRIVER_RECHARGE_YN")
	private String driverRechargeYn;
	
	@Column(name="TOTAL_PRICE")
	private BigDecimal totalPrice;
	
	public Long getQdaId() {
		return qdaId;
	}

	public void setQdaId(Long qdaId) {
		this.qdaId = qdaId;
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

	public Long getDacDacId() {
		return dacDacId;
	}

	public void setDacDacId(Long dacDacId) {
		this.dacDacId = dacDacId;
	}

	public String getDriverRechargeYn() {
		return driverRechargeYn;
	}

	public void setDriverRechargeYn(String driverRechargeYn) {
		this.driverRechargeYn = driverRechargeYn;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}
package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the DISPOSAL_REQUESTS database table.
 * 
 */
@Entity
@Table(name="DISPOSAL_REQUESTS")
public class DisposalRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DRQ_ID")
	private Long drqId;

	@Column(name="DRQ_STATUS")
	private String drqStatus;

	@Column(name="FMS_ID")
	private Long fmsId;

	@Column(name="SALE_DATE")
	private Date saleDate;

	@Column(name="SELL_PRICE_ACTUAL")
	private BigDecimal sellPriceActual;

	@Column(name="HAMMER_PRICE")
	private BigDecimal hammerPrice;
	
	@Column(name="DISPOSAL_METHOD")
	private String disposalMethod;

	public DisposalRequest() {
	}

	public Long getDrqId() {
		return this.drqId;
	}

	public void setDrqId(Long drqId) {
		this.drqId = drqId;
	}

	public String getDrqStatus() {
		return drqStatus;
	}

	public void setDrqStatus(String drqStatus) {
		this.drqStatus = drqStatus;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Date getSaleDate() {
		return DateUtil.clone(saleDate);
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = DateUtil.clone(saleDate);
	}

	public BigDecimal getSellPriceActual() {
		return sellPriceActual;
	}

	public void setSellPriceActual(BigDecimal sellPriceActual) {
		this.sellPriceActual = sellPriceActual;
	}

	public BigDecimal getHammerPrice() {
		return hammerPrice;
	}

	public void setHammerPrice(BigDecimal hammerPrice) {
		this.hammerPrice = hammerPrice;
	}

	public String getDisposalMethod() {
		return disposalMethod;
	}

	public void setDisposalMethod(String disposalMethod) {
		this.disposalMethod = disposalMethod;
	}

}
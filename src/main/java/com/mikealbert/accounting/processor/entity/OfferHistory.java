package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;

@Entity()
@Table(name = "OFFER_HISTORIES")
public class OfferHistory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "OFH_ID")
	private Long ofhId; 

	@Column(name = "DRQ_DRQ_ID")
	private Long drqDrqId; 

	@Column(name = "OFFER_HISTORY_TYPE")
	private String offerHistoryType; 

	@Column(name = "OFFER_DATE")
	private Date offerDate; 

	@Column(name = "OFFER_AMOUNT")
	private BigDecimal offerAmount; 
	
	@Column(name = "ACCEPT_IND")
	private String acceptInd;

	public Long getOfhId() {
		return ofhId;
	}

	public void setOfhId(Long ofhId) {
		this.ofhId = ofhId;
	}

	public Long getDrqDrqId() {
		return drqDrqId;
	}

	public void setDrqDrqId(Long drqDrqId) {
		this.drqDrqId = drqDrqId;
	}

	public String getOfferHistoryType() {
		return offerHistoryType;
	}

	public void setOfferHistoryType(String offerHistoryType) {
		this.offerHistoryType = offerHistoryType;
	}

	public Date getOfferDate() {
		return DateUtil.clone(offerDate);
	}

	public void setOfferDate(Date offerDate) {
		this.offerDate = DateUtil.clone(offerDate);
	}

	public BigDecimal getOfferAmount() {
		return offerAmount;
	}

	public void setOfferAmount(BigDecimal offerAmount) {
		this.offerAmount = offerAmount;
	}

	public String getAcceptInd() {
		return acceptInd;
	}

	public void setAcceptInd(String acceptInd) {
		this.acceptInd = acceptInd;
	} 
	
}

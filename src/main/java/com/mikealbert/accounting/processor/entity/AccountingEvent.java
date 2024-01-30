package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the ACCOUNTING_EVENTS database table.
 * 
 */
@Entity
@Table(name="ACCOUNTING_EVENTS")
public class AccountingEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AET_SEQ")    
    @SequenceGenerator(name="AET_SEQ", sequenceName="AET_SEQ", allocationSize=1)	
	@Column(name="AET_ID")
	private Long aetId;

	@Column(name="ENTITY_ID")
	private String entityId;
	
	@Column(name="ENTITY")
	private String entity;

	@Column(name="EVENT")
	private String event;
	
	@Column(name="OP_CODE")
	private String opCode;
	
	@Column(name="CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name="IS_PROCESSED")
	private String isProcessed;
	
	public AccountingEvent() {}
	
	public Long getAetId() {
		return aetId;
	}

	public void setAetId(Long aetId) {
		this.aetId = aetId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public Date getCreateDate() {
		return DateUtil.clone(this.createDate);
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = DateUtil.clone(createDate);
	}

	public String getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(String isProcessed) {
		this.isProcessed = isProcessed;
	}

	@Override
	public String toString() {
		return "AccountingEvent [aetId=" + aetId + ", entityId=" + entityId + ", entity=" + entity + ", event=" + event
				+ ", opCode=" + opCode + ", createDate=" + createDate + ", isProcessed=" + isProcessed + "]";
	}

}
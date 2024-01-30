package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the POLLING_LOG database table.
 * 
 */
@Entity
@Table(name="APP_LOG")
public class AppLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PLG_SEQ")    
    @SequenceGenerator(name="PLG_SEQ", sequenceName="PLG_SEQ", allocationSize=1)	
	@Column(name="PLG_ID")
	private Long plgId;
	
	@Column(name="CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name="NAME")
	private String name;

	@Column(name="PAYLOAD")
	@Lob
	private String payload;

	public AppLog() {}
	
	

	public AppLog(String name, String payload, Date createDate) {
		super();
		this.name = name;		
		this.payload = payload;		
		this.createDate = DateUtil.clone(createDate);
	}

	public Date getCreateDate() {
		return DateUtil.clone(this.createDate);
	}

	public void setCreateDate(Date createDate) {
		this.createDate = DateUtil.clone(createDate);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayload() {
		return this.payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Long getPlgId() {
		return this.plgId;
	}

	public void setPlgId(Long plgId) {
		this.plgId = plgId;
	}

}
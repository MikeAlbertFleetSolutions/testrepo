package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

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

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="CONTRACT_LINES")
public class ContractLine implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CLN_SEQ")
	@SequenceGenerator(name="CLN_SEQ", sequenceName="CLN_SEQ", allocationSize=1)
	
	@Column(name="CLN_ID")
	private Long clnId;
	
	@Column(name="CON_CON_ID")
	private Long conConId;

	@Column(name="START_DATE")
	private Date startDate;
	
	@Column(name="END_DATE")
	private Date endDate;
	
	@Column(name="IN_SERV_DATE")
	private Date inServiceDate;
	
	@Column(name="ACTUAL_END_DATE")
	private Date actualEndDate;
	
	@JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private FleetMaster fleetMaster;	

	@JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationModel quotationModel;	

	public Long getClnId() {
		return clnId;
	}

	public void setClnId(Long clnId) {
		this.clnId = clnId;
	}

	public Long getConConId() {
		return conConId;
	}

	public void setConConId(Long conConId) {
		this.conConId = conConId;
	}

	public Date getStartDate() {
		return DateUtil.clone(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
	}

	public Date getEndDate() {
		return DateUtil.clone(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
	}

	public Date getInServiceDate() {
		return DateUtil.clone(inServiceDate);
	}

	public void setInServiceDate(Date inServiceDate) {
		this.inServiceDate = DateUtil.clone(inServiceDate);
	}

	public Date getActualEndDate() {
		return DateUtil.clone(actualEndDate);
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = DateUtil.clone(actualEndDate);
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}
	
}
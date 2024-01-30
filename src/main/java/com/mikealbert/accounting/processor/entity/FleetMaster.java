package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the FLEET_MASTERS database table.
 * 
 */
@Entity
@Table(name="FLEET_MASTERS")
public class FleetMaster extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FMS_SEQ")    
    @SequenceGenerator(name="FMS_SEQ", sequenceName="FMS_SEQ", allocationSize=1)      
    @Column(name = "FMS_ID")
    private Long fmsId;

	@Column(name="UNIT_NO")
	private String unitNo;

	@Column(name="VIN")
	private String vin;
	
    @JoinColumn(name = "MDL_MDL_ID", referencedColumnName = "MDL_ID", insertable=false, updatable=false)
    @OneToOne(fetch = FetchType.EAGER)
    private Model model;	

	public FleetMaster() {}

	public long getFmsId() {
		return this.fmsId;
	}

	public void setFmsId(long fmsId) {
		this.fmsId = fmsId;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
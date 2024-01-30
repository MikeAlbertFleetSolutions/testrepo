package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * Mapped to MAKE_MODEL_RANGES table
 */
@Entity
@Table(name="MAKE_MODEL_RANGES")
public class MakeModelRange implements Serializable {
	private static final long serialVersionUID = 530460326784234543L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MRG_SEQ")    
    @SequenceGenerator(name="MRG_SEQ", sequenceName="MRG_SEQ", allocationSize=1)	
	@Column(name="MRG_ID")
	private long mrgId;
	
	@Column(name="MAKE_MODEL_DESC")
	private String description;
	
	@OneToMany(mappedBy="makeModelRange")
	private List<Model> models;
	
	public MakeModelRange() {}

	public long getMrgId() {
		return mrgId;
	}

	public void setMrgId(long mrgId) {
		this.mrgId = mrgId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}		

	
	
}
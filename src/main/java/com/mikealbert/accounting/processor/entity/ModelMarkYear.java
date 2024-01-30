package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Mapped to MODEL_MARK_YEARS table
 * @author sibley
 */
@Entity
@Table(name="MODEL_MARK_YEARS")
@NamedQuery(name = "ModelMarkYear.findAll", query = "SELECT m FROM ModelMarkYear m")
public class ModelMarkYear implements Serializable {
	private static final long serialVersionUID = -3754479134608322688L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MMY_SEQ")    
    @SequenceGenerator(name="MMY_SEQ", sequenceName="MMY_SEQ", allocationSize=1)	
	@Column(name="MMY_ID")
	private Long mmyId;
		
	@Column(name = "MODEL_MARK_YEAR_DESC")
	private String modelMarkYearDesc;
		
	@OneToMany(mappedBy="modelMarkYear")
	private List<Model> models;	
		
	public ModelMarkYear(){}

	public Long getMmyId() {
		return mmyId;
	}

	public void setMmyId(Long mmyId) {
		this.mmyId = mmyId;
	}

	public String getModelMarkYearDesc() {
		return modelMarkYearDesc;
	}

	public void setModelMarkYearDesc(String modelMarkYearDesc) {
		this.modelMarkYearDesc = modelMarkYearDesc;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}	
		
}
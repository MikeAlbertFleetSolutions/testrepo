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
 * Mapped to MAKES table
 */
@Entity
@Table(name="MAKES")
public class Make implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MAK_SEQ")    
    @SequenceGenerator(name="MAK_SEQ", sequenceName="MAK_SEQ", allocationSize=1)	
	@Column(name="MAK_ID")
	private Long makId;

	@Column(name="MAKE_DESC")
	private String makeDesc;
	
	@OneToMany(mappedBy="make")
	private List<Model> models;

	public Make() {}
	
	public Long getMakId() {
		return makId;
	}

	public void setMakId(Long makId) {
		this.makId = makId;
	}

	public String getMakeDesc() {
		return makeDesc;
	}

	public void setMakeDesc(String makeDesc) {
		this.makeDesc = makeDesc;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}


	
}
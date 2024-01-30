package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the COST_CENTRE_CODES database table.
 * 
 */
@Entity
@Table(name="COST_CENTRE_CODES")
public class CostCenterCode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CostCenterCodePK id;
    
    @Column(name = "DESCRIPTION")
    private String description;
     
    
	public CostCenterCode() {}

	public CostCenterCodePK getId() {
		return id;
	}

	public void setId(CostCenterCodePK id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
}
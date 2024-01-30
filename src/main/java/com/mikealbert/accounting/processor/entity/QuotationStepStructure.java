package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="QUOTATION_STEP_STRUCTURE")
public class QuotationStepStructure extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuotationStepStructurePK id;

	@Column(name="TO_PERIOD")
	private Long toPeriod;

	public QuotationStepStructure() {
		
	}

	public QuotationStepStructurePK getId() {
		return id;
	}

	public void setId(QuotationStepStructurePK id) {
		this.id = id;
	}

	public Long getToPeriod() {
		return toPeriod;
	}

	public void setToPeriod(Long toPeriod) {
		this.toPeriod = toPeriod;
	}
}

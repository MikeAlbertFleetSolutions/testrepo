package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="LEASE_ELEMENTS")
public class LeaseElement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LEL_SEQ")
	@SequenceGenerator(name="LEL_SEQ", sequenceName="LEL_SEQ", allocationSize=1)
	
	@Column(name="LEL_ID")
	private BigDecimal lelId;
	
	@Column(name="ELEMENT_TYPE")
	private String elementType;

	public BigDecimal getLelId() {
		return lelId;
	}

	public void setLelId(BigDecimal lelId) {
		this.lelId = lelId;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
}
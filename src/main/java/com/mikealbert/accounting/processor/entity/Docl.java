package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="DOCL")
public class Docl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private DoclPK id;	
	
	@Column(name="PRODUCT_CODE")
	private String productCode;	
	
    @JoinColumn(name = "DOC_ID", referencedColumnName = "DOC_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private Doc doc;	
		
	public Docl() {}

	public DoclPK getId() {
		return id;
	}

	public void setId(DoclPK id) {
		this.id = id;
	}

	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	@Override
	public String toString() {
		return "Docl [id=" + id + ", productCode=" + productCode + "]";
	}
		
}
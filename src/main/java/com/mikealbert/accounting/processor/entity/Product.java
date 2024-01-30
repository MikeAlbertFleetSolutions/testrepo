package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="PRODUCTS")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PRODUCT_CODE")
	private String productCode;
	
	@Column(name="PRODUCT_TYPE")
	private String productType;

	@Column(name="DESCRIPTION")
	private String description;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
}
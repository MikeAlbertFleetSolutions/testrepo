package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

import com.mikealbert.accounting.processor.validation.VendorValidator;

@VendorValidator
public class CostCenterVO implements Serializable{	
	private static final long serialVersionUID = 845572892269959857L;

	private String code;
	
	private String description;

	public CostCenterVO() {}
	
	public CostCenterVO(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public CostCenterVO setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public CostCenterVO setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public String toString() {
		return "CostCenterVO [code=" + code + ", description=" + description + "]";
	}		
}

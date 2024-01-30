package com.mikealbert.accounting.processor.enumeration;

import com.mikealbert.constant.enumeration.ProductTypeEnum;

public class LeaseAutoRenewal {

	private Integer term;
	private Boolean renewalFlag;

	static LeaseAutoRenewal oe;
	static LeaseAutoRenewal ce;
	
	private LeaseAutoRenewal(Integer term, Boolean renewalFlag) {
		this.term = term;
		this.renewalFlag = renewalFlag; 
	}

	private LeaseAutoRenewal() {

	}

	static public LeaseAutoRenewal getLeaseAutoRenewalInstance(String productType) {
		if(ProductTypeEnum.CE.toString().equals(productType)) {
			if(ce == null) {
				ce = new LeaseAutoRenewal(12, Boolean.TRUE);
			}
			return ce;
		}
		else if (ProductTypeEnum.OE.toString().equals(productType)) {
			if(oe == null) {
				oe = new LeaseAutoRenewal(1, Boolean.TRUE);
			}
			return oe;
		}
		else
			return null;		
	}

	public Integer getTerm() {
		return term;
	}

	public Boolean isRenewalFlag() {
		return renewalFlag;
	}	

}
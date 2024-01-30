package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.constant.enumeration.ProductEnum;

public class ClientPaymentLineVO extends ReceivableTransactionLineVO<ClientPaymentLineVO> {			
	@JsonBackReference 
	private ClientPaymentVO header;

	private String internalId;

	private String externalId;

	private boolean apply;
		
	public ClientPaymentLineVO() {}
	public ClientPaymentLineVO(ClientPaymentVO payment) {
		this.header = payment;
	}
		
	@Override
	public ClientPaymentVO getHeader() {
		return header;
	}
	
	public ClientPaymentLineVO setHeader(ClientPaymentVO header) {
		this.header = header;
		return this;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public boolean isApply() {
		return apply;
	}

	public void setApply(boolean apply) {
		this.apply = apply;
	}
	
	@Override
	public ProductEnum getProductCode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String toString() {
		return "ClientPaymentLineVO [apply=" + apply + ", externalId=" + externalId + ", internalId=" + internalId
				+ ", toString()=" + super.toString() + "]";
	}	

	
		
}

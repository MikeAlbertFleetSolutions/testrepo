package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.constant.enumeration.ProductEnum;

public class ClientInvoiceLineVO extends ReceivableTransactionLineVO<ClientInvoiceLineVO> {			
		
	@JsonBackReference 
	private ClientInvoiceVO header;
		
	public ClientInvoiceLineVO() {}
	public ClientInvoiceLineVO(ClientInvoiceVO invoice) {
		this.header = invoice;
	}
		
	@Override
	public ClientInvoiceVO getHeader() {
		return header;
	}
	
	public ClientInvoiceLineVO setHeader(ClientInvoiceVO header) {
		this.header = header;
		return this;
	}
	@Override
	public ProductEnum getProductCode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return "ClientInvoiceLineVO [toString()=" + super.toString() + "]";
	}	
	
}

package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.constant.enumeration.ProductEnum;

public class ClientCreditMemoLineVO extends ReceivableTransactionLineVO<ClientCreditMemoLineVO> {			
		
	@JsonBackReference 
	private ClientCreditMemoVO header;
		
	public ClientCreditMemoLineVO() {}
	public ClientCreditMemoLineVO(ClientCreditMemoVO creditMemo) {
		this.header = creditMemo;
	}
		
	@Override
	public ClientCreditMemoVO getHeader() {
		return header;
	}
	
	public ClientCreditMemoLineVO setHeader(ClientCreditMemoVO header) {
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
		return "ClientCreditMemoLineVO [toString()=" + super.toString() + "]";
	}	
	
}

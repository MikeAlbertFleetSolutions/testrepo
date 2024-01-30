package com.mikealbert.accounting.processor.vo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mikealbert.constant.enumeration.ProductEnum;

public class ClientDepositApplicationLineVO extends ReceivableTransactionLineVO<ClientDepositApplicationLineVO> {			
		
	@JsonBackReference 
	private ClientDepositApplicationVO header;
		
	public ClientDepositApplicationLineVO() {}
	public ClientDepositApplicationLineVO(ClientDepositApplicationVO clientDepositApplicationVO) {
		this.header = clientDepositApplicationVO;
	}
		
	@Override
	public ClientDepositApplicationVO getHeader() {
		return header;
	}
	
	public ClientDepositApplicationLineVO setHeader(ClientDepositApplicationVO header) {
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
		return "ClientDepositApplicationLineVO [header=" + header + ", toString()=" + super.toString() + "]";
	}
	
}

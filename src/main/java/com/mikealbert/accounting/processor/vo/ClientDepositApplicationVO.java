package com.mikealbert.accounting.processor.vo;

import java.util.ArrayList;
import java.util.List;

public class ClientDepositApplicationVO extends ReceivableTransactionVO<ClientDepositApplicationVO, ClientDepositApplicationLineVO> {
		
	private List<ClientDepositApplicationLineVO> lines;	
	
	public ClientDepositApplicationVO() {
		this.lines = new ArrayList<>();
	}

	public ClientDepositApplicationVO(String internalId, String externalId) {
		this.lines = new ArrayList<>();
		this.setInternalId(internalId);
		this.setExternalId(externalId);
	}	
						
	@Override
	public List<ClientDepositApplicationLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientDepositApplicationVO setLines(List<? extends TransactionLineVO<ClientDepositApplicationLineVO>> lines) {
		this.lines = (List<ClientDepositApplicationLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "ClientDepositApplicationVO [lines=" + lines + ", toString()=" + super.toString() + "]";
	}
		
}

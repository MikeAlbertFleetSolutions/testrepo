package com.mikealbert.accounting.processor.vo;

import java.util.ArrayList;
import java.util.List;

public class ClientCreditMemoVO extends ReceivableTransactionVO<ClientCreditMemoVO, ClientCreditMemoLineVO> {
	private List<ClientCreditMemoLineVO> lines;	
	
	public ClientCreditMemoVO(String internalId, String externalId) {
		this.lines = new ArrayList<>();
		this.setInternalId(internalId);
		this.setExternalId(externalId);
	}

	public ClientCreditMemoVO() {
		this.lines = new ArrayList<>();
	}
						
	@Override
	public List<ClientCreditMemoLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientCreditMemoVO setLines(List<? extends TransactionLineVO<ClientCreditMemoLineVO>> lines) {
		this.lines = (List<ClientCreditMemoLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "ClientCreditMemoVO [lines=" + lines + ", toString()=" + super.toString() + "]";
	}
	
}

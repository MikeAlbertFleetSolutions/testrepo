package com.mikealbert.accounting.processor.vo;

import java.util.ArrayList;
import java.util.List;

public class ClientInvoiceVO extends ReceivableTransactionVO<ClientInvoiceVO, ClientInvoiceLineVO> {
	
	private List<ClientInvoiceLineVO> lines;	
	
	public ClientInvoiceVO(String internalId, String externalId) {
		this.lines = new ArrayList<>();
		this.setInternalId(internalId);
		this.setExternalId(externalId);
	}

	public ClientInvoiceVO() {
		this.lines = new ArrayList<>();
	}
		
	@Override
	public List<ClientInvoiceLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientInvoiceVO setLines(List<? extends TransactionLineVO<ClientInvoiceLineVO>> lines) {
		this.lines = (List<ClientInvoiceLineVO>)lines;
		return this;
	}
	
	@Override
	public String toString() {
		return "ClientInvoiceVO [lines=" + lines  + ", toString()=" + super.toString() + "]";
	}	
	
}

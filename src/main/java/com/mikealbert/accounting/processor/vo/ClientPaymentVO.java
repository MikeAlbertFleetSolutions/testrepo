package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClientPaymentVO extends ReceivableTransactionVO<ClientPaymentVO, ClientPaymentLineVO> {	
		
	private String reference;

	private BigDecimal amount;

	private BigDecimal appliedAmount;	

	private BigDecimal unAppliedAmount;	

	private String paymentMethod;

	private List<ClientPaymentLineVO> lines;	
	
	public ClientPaymentVO() {
		this.lines = new ArrayList<>();		
	}

	public ClientPaymentVO(String internalId, String externalId) {
		this.lines = new ArrayList<>();
		this.setInternalId(internalId);
		this.setExternalId(externalId);
	}
		
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}		
	
	public BigDecimal getAppliedAmount() {
		return appliedAmount;
	}

	public void setAppliedAmount(BigDecimal appliedAmount) {
		this.appliedAmount = appliedAmount;
	}

	public BigDecimal getUnAppliedAmount() {
		return unAppliedAmount;
	}

	public void setUnAppliedAmount(BigDecimal unAppliedAmount) {
		this.unAppliedAmount = unAppliedAmount;
	}
	
	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Override
	public List<ClientPaymentLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ClientPaymentVO setLines(List<? extends TransactionLineVO<ClientPaymentLineVO>> lines) {
		this.lines = (List<ClientPaymentLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "ClientPaymentVO [amount=" + amount + ", appliedAmount=" + appliedAmount + ", lines=" + lines
				+ ", paymentMethod=" + paymentMethod + ", reference=" + reference + ", unAppliedAmount="
				+ unAppliedAmount + ", toString()=" + super.toString() + "]";
	}	
	
}

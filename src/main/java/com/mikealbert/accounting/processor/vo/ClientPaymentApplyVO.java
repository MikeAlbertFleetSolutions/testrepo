package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class ClientPaymentApplyVO implements Serializable {	
		
	private String internalId;

	private String externalId;
		
	private String tranId;	
	
	private Date tranDate;	
		
	private String memo;
			
	private String status;
		
	private BigDecimal appliedAmount;

	private String invoiceInternalId;

	private String invoiceTranId;
	
	public ClientPaymentApplyVO() {}

	public String getInternalId() {
		return internalId;
	}

	public ClientPaymentApplyVO setInternalId(String internalId) {
		this.internalId = internalId;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}

	public ClientPaymentApplyVO setExternalId(String externalId) {
		this.externalId = externalId;
		return this;		
	}

	public String getTranId() {
		return tranId;
	}

	public ClientPaymentApplyVO setTranId(String tranId) {
		this.tranId = tranId;
		return this;		
	}

	public Date getTranDate() {
		return DateUtil.clone(tranDate);
	}

	public ClientPaymentApplyVO setTranDate(Date tranDate) {
		this.tranDate = DateUtil.clone(tranDate);
		return this;		
	}

	public String getMemo() {
		return memo;
	}

	public ClientPaymentApplyVO setMemo(String memo) {
		this.memo = memo;
		return this;		
	}

	public String getStatus() {
		return status;
	}

	public ClientPaymentApplyVO setStatus(String status) {
		this.status = status;
		return this;
	}

	public BigDecimal getAppliedAmount() {
		return appliedAmount;
	}

	public ClientPaymentApplyVO setAppliedAmount(BigDecimal appliedAmount) {
		this.appliedAmount = appliedAmount;
		return this;
	}

	public String getInvoiceInternalId() {
		return invoiceInternalId;
	}

	public ClientPaymentApplyVO setInvoiceInternalId(String invoiceInternalId) {
		this.invoiceInternalId = invoiceInternalId;
		return this;
	}

	public String getInvoiceTranId() {
		return invoiceTranId;
	}

	public ClientPaymentApplyVO setInvoiceTranId(String invoiceTranId) {
		this.invoiceTranId = invoiceTranId;
		return this;
	}

	@Override
	public String toString() {
		return "ClientPaymentApplyVO [appliedAmount=" + appliedAmount + ", externalId=" + externalId + ", internalId="
				+ internalId + ", invoiceInternalId=" + invoiceInternalId + ", invoiceTranId=" + invoiceTranId
				+ ", memo=" + memo + ", status=" + status + ", tranDate=" + tranDate + ", tranId=" + tranId + "]";
	}
}

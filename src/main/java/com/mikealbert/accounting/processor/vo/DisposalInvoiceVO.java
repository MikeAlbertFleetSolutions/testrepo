package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.accounting.processor.validation.DisposalInvoiceValidator;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;
import com.mikealbert.util.data.DateUtil;

@DisposalInvoiceValidator
public class DisposalInvoiceVO implements Serializable{

	private static final long serialVersionUID = -8369223842044518726L;
	
	private String transactionId;
	
	private String transactionExtId;
	
	private Date paidInFullDate;
	
	private TransactionStatusEnum status;
	
	private Long docId;
		
	public String getTransactionId() {
		return transactionId;
	}

	public DisposalInvoiceVO setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public String getTransactionExtId() {
		return transactionExtId;
	}

	public DisposalInvoiceVO setTransactionExtId(String transactionExtId) {
		this.transactionExtId = transactionExtId;
		return this;
	}

	public Date getPaidInFullDate() {
		return DateUtil.clone(paidInFullDate);
	}

	public DisposalInvoiceVO setPaidInFullDate(Date paidInFullDate) {
		this.paidInFullDate = DateUtil.clone(paidInFullDate);
		return this;
	}

	public TransactionStatusEnum getStatus() {
		return status;
	}

	public DisposalInvoiceVO setStatus(TransactionStatusEnum status) {
		this.status = status;
		return this;
	}

	
	public Long getDocId() {
		return docId;
	}

	public DisposalInvoiceVO setDocId(Long docId) {
		this.docId = docId;
		return this;
	}

	@Override
	public String toString() {
		return "DisposalInvoiceVO [transactionId=" + transactionId + ", transactionExtId=" + transactionExtId
				+ ", paidInFullDate=" + paidInFullDate + ", status=" + status + ", docId=" + docId + "]";
	}

}

package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.util.data.DateUtil;

@SuppressWarnings("unchecked")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({
	@Type(value = CreditVO.class, name = "credit"),	
	@Type(value = InvoiceVO.class, name = "invoice"), 
	@Type(value = PurchaseOrderVO.class, name = "purchaseOrder"),
	@Type(value = ClientInvoiceVO.class, name = "clientInvoice"),
	@Type(value = ClientPaymentVO.class, name = "clientPayment"),
	@Type(value = ClientCreditMemoVO.class, name = "clientCreditMemo"),
	@Type(value = ClientDepositApplicationVO.class, name = "clientDepositApplication")
})
public abstract class TransactionVO<T, P> implements Serializable {
	private static final long serialVersionUID = -1335733457515359611L; 

	private String internalId;

	private String externalId;
	
	private ControlCodeEnum controlCode;
	
	private String tranId;	

	private String transactionNumber;	
	
	private Date tranDate;
		
	private Long subsidiary;
	
	private String memo;
	
	private Long disposalFiscalYear;
	
	private Long currentFiscalYear;
		
	private String opCode;

	private String status;

	private TransactionTypeEnum type;

    private Date asOfDate;	

	private String skipApproval;

	private boolean taxDetailOverride;
	
	public String getInternalId() {
		return internalId;
	}

	public T setInternalId(String internalId) {
		this.internalId = internalId;
		return (T) this;
	}	

	public String getExternalId() {
		return externalId;
	}

	public T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T)this;
	}
		
	public ControlCodeEnum getControlCode() {
		return controlCode;
	}

	public T setControlCode(ControlCodeEnum controlCode) {
		this.controlCode = controlCode;
		return (T)this;
	}

	public String getTranId() {
		return tranId;
	}

	public T setTranId(String tranId) {
		this.tranId = tranId;
		return (T)this;
	}
		
	public String getTransactionNumber() {
		return transactionNumber;
	}

	public T setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
		return (T)this;
	}

	public Date getTranDate() {
		return DateUtil.clone(tranDate);
	}

	public T setTranDate(Date tranDate) {
		this.tranDate = DateUtil.clone(tranDate);
		return (T)this;
	}	

	public Long getSubsidiary() {
		return subsidiary;
	}

	public T setSubsidiary(Long subsidiary) {
		this.subsidiary = subsidiary;
		return (T)this;
	}

	public String getMemo() {
		return memo;
	}

	public T setMemo(String memo) {
		this.memo = memo;
		return (T)this;
	}

	public Long getDisposalFiscalYear() {
		return disposalFiscalYear;
	}

	public T setDisposalFiscalYear(Long disposalFiscalYear) {
		this.disposalFiscalYear = disposalFiscalYear;
		return (T)this;
	}

	public Long getCurrentFiscalYear() {
		return currentFiscalYear;
	}

	public T setCurrentFiscalYear(Long currentFiscalYear) {
		this.currentFiscalYear = currentFiscalYear;
		return (T)this;
	}
	
	public String getOpCode() {
		return opCode;
	}

	public T setOpCode(String opCode) {
		this.opCode = opCode;
		return (T)this;
	}
	
	public TransactionTypeEnum getType() {
		return type;
	}

	public T setType(TransactionTypeEnum type) {
		this.type = type;
		return (T)this;
	}

	public String getStatus() {
		return status;
	}

	public T setStatus(String status) {
		this.status = status;
		return (T)this;
	}

	public Date getAsOfDate() {
		return DateUtil.clone(asOfDate);
	}

	public T setAsOfDate(Date asOfDate) {
		this.asOfDate = DateUtil.clone(asOfDate);
		return (T)this;
	}

	public String getSkipApproval() {
		return skipApproval;
	}

	public T setSkipApproval(String skipApproval) {
		this.skipApproval = skipApproval;
		return (T)this;
	}	

	public boolean isTaxDetailOverride() {
		return taxDetailOverride;
	}

	public T setTaxDetailOverride(boolean taxDetailOverride) {
		this.taxDetailOverride = taxDetailOverride;
		return (T)this;
	}

	public abstract List<? extends TransactionLineVO<P>> getLines();

	public abstract T setLines(List<? extends TransactionLineVO<P>> lines);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((internalId == null) ? 0 : internalId.hashCode());
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((controlCode == null) ? 0 : controlCode.hashCode());
		result = prime * result + ((tranId == null) ? 0 : tranId.hashCode());
		result = prime * result + ((transactionNumber == null) ? 0 : transactionNumber.hashCode());
		result = prime * result + ((tranDate == null) ? 0 : tranDate.hashCode());
		result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((disposalFiscalYear == null) ? 0 : disposalFiscalYear.hashCode());
		result = prime * result + ((currentFiscalYear == null) ? 0 : currentFiscalYear.hashCode());
		result = prime * result + ((opCode == null) ? 0 : opCode.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((asOfDate == null) ? 0 : asOfDate.hashCode());
		result = prime * result + ((skipApproval == null) ? 0 : skipApproval.hashCode());
		result = prime * result + (taxDetailOverride ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionVO<T, P> other = (TransactionVO<T, P>) obj;
		if (internalId == null) {
			if (other.internalId != null)
				return false;
		} else if (!internalId.equals(other.internalId))
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (controlCode != other.controlCode)
			return false;
		if (tranId == null) {
			if (other.tranId != null)
				return false;
		} else if (!tranId.equals(other.tranId))
			return false;
		if (transactionNumber == null) {
			if (other.transactionNumber != null)
				return false;
		} else if (!transactionNumber.equals(other.transactionNumber))
			return false;
		if (tranDate == null) {
			if (other.tranDate != null)
				return false;
		} else if (!tranDate.equals(other.tranDate))
			return false;
		if (subsidiary == null) {
			if (other.subsidiary != null)
				return false;
		} else if (!subsidiary.equals(other.subsidiary))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (disposalFiscalYear == null) {
			if (other.disposalFiscalYear != null)
				return false;
		} else if (!disposalFiscalYear.equals(other.disposalFiscalYear))
			return false;
		if (currentFiscalYear == null) {
			if (other.currentFiscalYear != null)
				return false;
		} else if (!currentFiscalYear.equals(other.currentFiscalYear))
			return false;
		if (opCode == null) {
			if (other.opCode != null)
				return false;
		} else if (!opCode.equals(other.opCode))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (type != other.type)
			return false;
		if (asOfDate == null) {
			if (other.asOfDate != null)
				return false;
		} else if (!asOfDate.equals(other.asOfDate))
			return false;
		if (skipApproval == null) {
			if (other.skipApproval != null)
				return false;
		} else if (!skipApproval.equals(other.skipApproval))
			return false;
		if (taxDetailOverride != other.taxDetailOverride)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransactionVO [internalId=" + internalId + ", externalId=" + externalId + ", controlCode=" + controlCode
				+ ", tranId=" + tranId + ", transactionNumber=" + transactionNumber + ", tranDate=" + tranDate
				+ ", subsidiary=" + subsidiary + ", memo=" + memo + ", disposalFiscalYear=" + disposalFiscalYear
				+ ", currentFiscalYear=" + currentFiscalYear + ", opCode=" + opCode + ", status=" + status + ", type="
				+ type + ", asOfDate=" + asOfDate + ", skipApproval=" + skipApproval + ", taxDetailOverride="
				+ taxDetailOverride + "]";
	}
			
}

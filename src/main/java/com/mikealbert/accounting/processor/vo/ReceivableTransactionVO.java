package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.mikealbert.util.data.DateUtil;

@SuppressWarnings("unchecked")
public abstract class ReceivableTransactionVO<T, P> extends TransactionVO<T, P> {
    private String clientInternalId;
    
	private String clientExternalId; 
	
	private String payableAccount;

	private Long docId;  

	private Long docLineId;  //TODO Move docLineId down to the credit memo header. It's the only place where it is being used on the header.

	private boolean grouped;

	private String groupNumber;    

    private Date dueDate;

    private Long daysOverdue;

    private BigDecimal total;

    private BigDecimal applied;

    private BigDecimal balance;

	private String maType; 
    
    private boolean autoApprove;
    
    private ApplicationEnum origin;

    public String getClientInternalId() {
        return clientInternalId;
    }

    public T setClientInternalId(String clientInternalId) {
        this.clientInternalId = clientInternalId;
        return (T)this;
    }

    public String getClientExternalId() {
        return clientExternalId;
    }

    public T setClientExternalId(String clientExternalId) {
        this.clientExternalId = clientExternalId;
        return (T)this;
    }

    public String getPayableAccount() {
        return payableAccount;
    }

    public T setPayableAccount(String payableAccount) {
        this.payableAccount = payableAccount;
        return (T)this;
    }

	public Long getDocId() {
		return docId;
	}

	public T setDocId(Long docId) {
		this.docId = docId;
		return (T)this;
	}

	public Long getDocLineId() {
		return docLineId;
	}

	public T setDocLineId(Long docLineId) {
		this.docLineId = docLineId;
		return (T)this;
	}    

    public boolean isGrouped() {
		return grouped;
	}

	public T setGrouped(boolean grouped) {
		this.grouped = grouped;
		return (T)this;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public T setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
		return (T)this;
	}
    
    public Date getDueDate() {
        return DateUtil.clone(dueDate);
    }

    public T setDueDate(Date dueDate) {
        this.dueDate = DateUtil.clone(dueDate);
        return (T)this;
    }

    public Long getDaysOverdue() {
        return daysOverdue;
    }

    public T setDaysOverdue(Long daysOverdue) {
        this.daysOverdue = daysOverdue;
        return (T)this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public T setTotal(BigDecimal total) {
        this.total = total;
        return (T)this;
    }

    public BigDecimal getApplied() {
        return applied;
    }

    public T setApplied(BigDecimal applied) {
        this.applied = applied;
        return (T)this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public T setBalance(BigDecimal balance) {
        this.balance = balance;
        return (T)this;
    }

    public String getMaType() {
        return maType;
    }

    public T setMaType(String maType) {
        this.maType = maType;
        return (T)this;
    }

    public boolean isAutoApprove() {
        return autoApprove;
    }

    public T setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
        return (T)this;
    }

    public ApplicationEnum getOrigin() {
        return origin;
    }

    public T setOrigin(ApplicationEnum origin) {
        this.origin = origin;
        return (T)this;
    }

    @Override
    public String toString() {
        return "ReceivableTransactionVO [clientInternalId=" + clientInternalId + ", clientExternalId="
                + clientExternalId + ", payableAccount=" + payableAccount + ", docId=" + docId + ", docLineId="
                + docLineId + ", grouped=" + grouped + ", groupNumber=" + groupNumber + ", dueDate=" + dueDate
                + ", daysOverdue=" + daysOverdue + ", total=" + total + ", applied=" + applied + ", balance=" + balance
                + ", maType=" + maType + ", autoApprove=" + autoApprove + ", origin=" + origin + ", toString()=" + super.toString() + "]";
    }     
         
}

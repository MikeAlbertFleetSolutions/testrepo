package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class ClientTransactionGroupVO implements Serializable {
    private String accountingPeriodId;

    private Date accountingPeriodDate;

    private String clientInternalId;

    private String clientExternalId;

    private String clientAccountCode;

    private String groupNumber;

    public ClientTransactionGroupVO(){}
    
    public String getAccountingPeriodId() {
        return accountingPeriodId;
    }

    public ClientTransactionGroupVO setAccountingPeriodId(String accountingPeriodId) {
        this.accountingPeriodId = accountingPeriodId;
        return this;
    }

    public Date getAccountingPeriodDate() {
        return DateUtil.clone(accountingPeriodDate);
    }

    public ClientTransactionGroupVO setAccountingPeriodDate(Date accountingPeriodDate) {
        this.accountingPeriodDate = DateUtil.clone(accountingPeriodDate);
        return this;
    }

    public String getClientInternalId() {
        return clientInternalId;
    }

    public ClientTransactionGroupVO setClientInternalId(String clientInternalId) {
        this.clientInternalId = clientInternalId;
        return this;
    }

    public String getClientExternalId() {
        return clientExternalId;
    }

    public ClientTransactionGroupVO setClientExternalId(String clientExternalId) {
        this.clientExternalId = clientExternalId;
        return this;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public ClientTransactionGroupVO setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
        return this;
    }

    public String getClientAccountCode() {
        return clientAccountCode;
    }

    public ClientTransactionGroupVO setClientAccountCode(String clientAccountCode) {
        this.clientAccountCode = clientAccountCode;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountingPeriodDate == null) ? 0 : accountingPeriodDate.hashCode());
        result = prime * result + ((accountingPeriodId == null) ? 0 : accountingPeriodId.hashCode());
        result = prime * result + ((clientAccountCode == null) ? 0 : clientAccountCode.hashCode());
        result = prime * result + ((clientExternalId == null) ? 0 : clientExternalId.hashCode());
        result = prime * result + ((clientInternalId == null) ? 0 : clientInternalId.hashCode());
        result = prime * result + ((groupNumber == null) ? 0 : groupNumber.hashCode());
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
        ClientTransactionGroupVO other = (ClientTransactionGroupVO) obj;
        if (accountingPeriodDate == null) {
            if (other.accountingPeriodDate != null)
                return false;
        } else if (!accountingPeriodDate.equals(other.accountingPeriodDate))
            return false;
        if (accountingPeriodId == null) {
            if (other.accountingPeriodId != null)
                return false;
        } else if (!accountingPeriodId.equals(other.accountingPeriodId))
            return false;
        if (clientAccountCode == null) {
            if (other.clientAccountCode != null)
                return false;
        } else if (!clientAccountCode.equals(other.clientAccountCode))
            return false;
        if (clientExternalId == null) {
            if (other.clientExternalId != null)
                return false;
        } else if (!clientExternalId.equals(other.clientExternalId))
            return false;
        if (clientInternalId == null) {
            if (other.clientInternalId != null)
                return false;
        } else if (!clientInternalId.equals(other.clientInternalId))
            return false;
        if (groupNumber == null) {
            if (other.groupNumber != null)
                return false;
        } else if (!groupNumber.equals(other.groupNumber))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClientTransactionGroupVO [accountingPeriodDate=" + accountingPeriodDate + ", accountingPeriodId="
                + accountingPeriodId + ", clientAccountCode=" + clientAccountCode + ", clientExternalId="
                + clientExternalId + ", clientInternalId=" + clientInternalId + ", groupNumber=" + groupNumber + "]";
    }    
}

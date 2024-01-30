package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

public class ClientInvoiceDepositVO implements Serializable{

    private String depositApplicationInternalId;

    private String depositApplicationTranId;

    private String invoiceInternalId;

    private String invoiceTranId;

    @NotNull(message = "docId is required")
    private Long docId;

    @NotNull(message = "docLinedId is required")
    private Long docLineId;

    @NotNull(message = "amountApplied is required")
    private BigDecimal amountApplied;

    public ClientInvoiceDepositVO() {}
    public ClientInvoiceDepositVO(Long docId, Long docLineId, BigDecimal amountApplied) {
        this.docId = docId;
        this.docLineId = docLineId;
        this.amountApplied = amountApplied;
    }  

    public String getDepositApplicationInternalId() {
        return depositApplicationInternalId;
    }

    public ClientInvoiceDepositVO setDepositApplicationInternalId(String depositApplicationInternalId) {
        this.depositApplicationInternalId = depositApplicationInternalId;
        return this;
    }

    public String getDepositApplicationTranId() {
        return depositApplicationTranId;
    }

    public ClientInvoiceDepositVO setDepositApplicationTranId(String depositApplicationTranId) {
        this.depositApplicationTranId = depositApplicationTranId;
        return this;
    }

    public String getInvoiceInternalId() {
        return invoiceInternalId;
    }

    public ClientInvoiceDepositVO setInvoiceInternalId(String invoiceInternalId) {
        this.invoiceInternalId = invoiceInternalId;
        return this;
    }

    public String getInvoiceTranId() {
        return invoiceTranId;
    }

    public ClientInvoiceDepositVO setInvoiceTranId(String invoiceTranId) {
        this.invoiceTranId = invoiceTranId;
        return this;
    }

    public Long getDocId() {
        return docId;
    }

    public ClientInvoiceDepositVO setDocId(Long docId) {
        this.docId = docId;
        return this;
    }

    public Long getDocLineId() {
        return docLineId;
    }

    public ClientInvoiceDepositVO setDocLineId(Long docLineId) {
        this.docLineId = docLineId;
        return this;
    }

    public BigDecimal getAmountApplied() {
        return amountApplied;
    }

    public ClientInvoiceDepositVO setAmountApplied(BigDecimal amountApplied) {
        this.amountApplied = amountApplied;
        return this;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amountApplied == null) ? 0 : amountApplied.hashCode());
        result = prime * result
                + ((depositApplicationInternalId == null) ? 0 : depositApplicationInternalId.hashCode());
        result = prime * result + ((depositApplicationTranId == null) ? 0 : depositApplicationTranId.hashCode());
        result = prime * result + ((docId == null) ? 0 : docId.hashCode());
        result = prime * result + ((docLineId == null) ? 0 : docLineId.hashCode());
        result = prime * result + ((invoiceInternalId == null) ? 0 : invoiceInternalId.hashCode());
        result = prime * result + ((invoiceTranId == null) ? 0 : invoiceTranId.hashCode());
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
        ClientInvoiceDepositVO other = (ClientInvoiceDepositVO) obj;
        if (amountApplied == null) {
            if (other.amountApplied != null)
                return false;
        } else if (!amountApplied.equals(other.amountApplied))
            return false;
        if (depositApplicationInternalId == null) {
            if (other.depositApplicationInternalId != null)
                return false;
        } else if (!depositApplicationInternalId.equals(other.depositApplicationInternalId))
            return false;
        if (depositApplicationTranId == null) {
            if (other.depositApplicationTranId != null)
                return false;
        } else if (!depositApplicationTranId.equals(other.depositApplicationTranId))
            return false;
        if (docId == null) {
            if (other.docId != null)
                return false;
        } else if (!docId.equals(other.docId))
            return false;
        if (docLineId == null) {
            if (other.docLineId != null)
                return false;
        } else if (!docLineId.equals(other.docLineId))
            return false;
        if (invoiceInternalId == null) {
            if (other.invoiceInternalId != null)
                return false;
        } else if (!invoiceInternalId.equals(other.invoiceInternalId))
            return false;
        if (invoiceTranId == null) {
            if (other.invoiceTranId != null)
                return false;
        } else if (!invoiceTranId.equals(other.invoiceTranId))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "ClientInvoiceDepositVO [amountApplied=" + amountApplied + ", depositApplicationInternalId="
                + depositApplicationInternalId + ", depositApplicationTranId=" + depositApplicationTranId + ", docId="
                + docId + ", docLineId=" + docLineId + ", invoiceInternalId=" + invoiceInternalId + ", invoiceTranId="
                + invoiceTranId + "]";
    }
    
}

package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class BillingReportTransactionAmountVO implements Serializable {
	private BigDecimal amount;
	private BigDecimal tax;
	private BigDecimal gross;
    
    public BillingReportTransactionAmountVO(BigDecimal amount, BigDecimal tax, BigDecimal gross) {
        this.amount = amount;
        this.tax = tax;
        this.gross = gross;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getTax() {
        return tax;
    }
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
    public BigDecimal getGross() {
        return gross;
    }
    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((gross == null) ? 0 : gross.hashCode());
        result = prime * result + ((tax == null) ? 0 : tax.hashCode());
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
        BillingReportTransactionAmountVO other = (BillingReportTransactionAmountVO) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (gross == null) {
            if (other.gross != null)
                return false;
        } else if (!gross.equals(other.gross))
            return false;
        if (tax == null) {
            if (other.tax != null)
                return false;
        } else if (!tax.equals(other.tax))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BillingReportTransactionAmountVO [amount=" + amount + ", gross=" + gross + ", tax=" + tax + "]";
    }
        
}

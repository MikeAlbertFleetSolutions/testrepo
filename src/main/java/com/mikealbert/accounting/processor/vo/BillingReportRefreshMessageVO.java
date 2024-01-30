package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

public class BillingReportRefreshMessageVO implements Serializable{
	private String accountCode;

	private String startPeriod;

	private String endPeriod;

	private String reportName;

    private boolean force;
   
    public BillingReportRefreshMessageVO() {}

    public String getAccountCode() {
        return accountCode;
    }

    public BillingReportRefreshMessageVO setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public BillingReportRefreshMessageVO setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
        return this;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public BillingReportRefreshMessageVO setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
        return this;
    }

    public String getReportName() {
        return reportName;
    }

    public BillingReportRefreshMessageVO setReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }

    public boolean isForce() {
        return force;
    }

    public BillingReportRefreshMessageVO setForce(boolean force) {
        this.force = force;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountCode == null) ? 0 : accountCode.hashCode());
        result = prime * result + ((startPeriod == null) ? 0 : startPeriod.hashCode());
        result = prime * result + ((endPeriod == null) ? 0 : endPeriod.hashCode());
        result = prime * result + ((reportName == null) ? 0 : reportName.hashCode());
        result = prime * result + (force ? 1231 : 1237);
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
        BillingReportRefreshMessageVO other = (BillingReportRefreshMessageVO) obj;
        if (accountCode == null) {
            if (other.accountCode != null)
                return false;
        } else if (!accountCode.equals(other.accountCode))
            return false;
        if (startPeriod == null) {
            if (other.startPeriod != null)
                return false;
        } else if (!startPeriod.equals(other.startPeriod))
            return false;
        if (endPeriod == null) {
            if (other.endPeriod != null)
                return false;
        } else if (!endPeriod.equals(other.endPeriod))
            return false;
        if (reportName == null) {
            if (other.reportName != null)
                return false;
        } else if (!reportName.equals(other.reportName))
            return false;
        if (force != other.force)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BillingReportRefreshMessageVO [accountCode=" + accountCode + ", endPeriod=" + endPeriod + ", force="
                + force + ", reportName=" + reportName + ", startPeriod=" + startPeriod + "]";
    }
    
}

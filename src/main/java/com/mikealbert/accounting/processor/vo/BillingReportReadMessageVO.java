package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

public class BillingReportReadMessageVO implements Serializable{
	private String clientExternalId;

	private String startingAccountingPeriod;

	private String endingAccountingPeriod;

	private String reportName;

   
    public BillingReportReadMessageVO() {}

    public String getClientExternalId() {
        return clientExternalId;
    }

    public void setClientExternalId(String clientExternalId) {
        this.clientExternalId = clientExternalId;
    }

    public String getStartingAccountingPeriod() {
        return startingAccountingPeriod;
    }

    public void setStartingAccountingPeriod(String startingAccountingPeriod) {
        this.startingAccountingPeriod = startingAccountingPeriod;
    }

    public String getEndingAccountingPeriod() {
        return endingAccountingPeriod;
    }

    public void setEndingAccountingPeriod(String endingAccountingPeriod) {
        this.endingAccountingPeriod = endingAccountingPeriod;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientExternalId == null) ? 0 : clientExternalId.hashCode());
        result = prime * result + ((startingAccountingPeriod == null) ? 0 : startingAccountingPeriod.hashCode());
        result = prime * result + ((endingAccountingPeriod == null) ? 0 : endingAccountingPeriod.hashCode());
        result = prime * result + ((reportName == null) ? 0 : reportName.hashCode());
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
        BillingReportReadMessageVO other = (BillingReportReadMessageVO) obj;
        if (clientExternalId == null) {
            if (other.clientExternalId != null)
                return false;
        } else if (!clientExternalId.equals(other.clientExternalId))
            return false;
        if (startingAccountingPeriod == null) {
            if (other.startingAccountingPeriod != null)
                return false;
        } else if (!startingAccountingPeriod.equals(other.startingAccountingPeriod))
            return false;
        if (endingAccountingPeriod == null) {
            if (other.endingAccountingPeriod != null)
                return false;
        } else if (!endingAccountingPeriod.equals(other.endingAccountingPeriod))
            return false;
        if (reportName == null) {
            if (other.reportName != null)
                return false;
        } else if (!reportName.equals(other.reportName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BillingReportReadMessageVO [clientExternalId=" + clientExternalId + ", startingAccountingPeriod="
                + startingAccountingPeriod + ", endingAccountingPeriod=" + endingAccountingPeriod + ", reportName="
                + reportName + "]";
    }    
}

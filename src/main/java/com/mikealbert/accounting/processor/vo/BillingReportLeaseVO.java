package com.mikealbert.accounting.processor.vo;

import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class BillingReportLeaseVO {
    private String internalId;

    private String externalId;

    private String name;

    private Date commencementDate;

    private Date endDate;

    private String leaseType;

    private String unitInternalId;

    private String unitExternalId;

    private String unitNo;

    private String altname;

    public BillingReportLeaseVO() {}

    public String getInternalId() {
        return internalId;
    }

    public BillingReportLeaseVO setInternalId(String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public BillingReportLeaseVO setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getName() {
        return name;
    }

    public BillingReportLeaseVO setName(String name) {
        this.name = name;
        return this;
    }

    public Date getCommencementDate() {
        return DateUtil.clone(commencementDate);
    }

    public BillingReportLeaseVO setCommencementDate(Date commencementDate) {
        this.commencementDate = DateUtil.clone(commencementDate);
        return this;
    }

    public Date getEndDate() {
        return DateUtil.clone(endDate);
    }

    public BillingReportLeaseVO setEndDate(Date endDate) {
        this.endDate = DateUtil.clone(endDate);
        return this;
    }

    public String getLeaseType() {
        return leaseType;
    }

    public BillingReportLeaseVO setLeaseType(String leaseType) {
        this.leaseType = leaseType;
        return this;
    }

    public String getUnitInternalId() {
        return unitInternalId;
    }

    public BillingReportLeaseVO setUnitInternalId(String unitInternalId) {
        this.unitInternalId = unitInternalId;
        return this;
    }

    public String getUnitExternalId() {
        return unitExternalId;
    }

    public BillingReportLeaseVO setUnitExternalId(String unitExternalId) {
        this.unitExternalId = unitExternalId;
        return this;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public BillingReportLeaseVO setUnitNo(String unitNo) {
        this.unitNo = unitNo;
        return this;
    }

    public String getAltname() {
        return altname;
    }

    public BillingReportLeaseVO setAltname(String altname) {
        this.altname = altname;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((altname == null) ? 0 : altname.hashCode());
        result = prime * result + ((commencementDate == null) ? 0 : commencementDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
        result = prime * result + ((internalId == null) ? 0 : internalId.hashCode());
        result = prime * result + ((leaseType == null) ? 0 : leaseType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((unitExternalId == null) ? 0 : unitExternalId.hashCode());
        result = prime * result + ((unitInternalId == null) ? 0 : unitInternalId.hashCode());
        result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
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
        BillingReportLeaseVO other = (BillingReportLeaseVO) obj;
        if (altname == null) {
            if (other.altname != null)
                return false;
        } else if (!altname.equals(other.altname))
            return false;
        if (commencementDate == null) {
            if (other.commencementDate != null)
                return false;
        } else if (!commencementDate.equals(other.commencementDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
        if (internalId == null) {
            if (other.internalId != null)
                return false;
        } else if (!internalId.equals(other.internalId))
            return false;
        if (leaseType == null) {
            if (other.leaseType != null)
                return false;
        } else if (!leaseType.equals(other.leaseType))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (unitExternalId == null) {
            if (other.unitExternalId != null)
                return false;
        } else if (!unitExternalId.equals(other.unitExternalId))
            return false;
        if (unitInternalId == null) {
            if (other.unitInternalId != null)
                return false;
        } else if (!unitInternalId.equals(other.unitInternalId))
            return false;
        if (unitNo == null) {
            if (other.unitNo != null)
                return false;
        } else if (!unitNo.equals(other.unitNo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BillingReportLeaseVO [altname=" + altname + ", commencementDate=" + commencementDate + ", endDate="
                + endDate + ", externalId=" + externalId + ", internalId=" + internalId + ", leaseType=" + leaseType
                + ", name=" + name + ", unitExternalId=" + unitExternalId + ", unitInternalId=" + unitInternalId
                + ", unitNo=" + unitNo + "]";
    }  

}

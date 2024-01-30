package com.mikealbert.accounting.processor.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UNIT_REGISTRATIONS")
public class UnitRegistration {
    
    @Id
    @Column(name="urgId")
    private Long urgId;

    @Column(name="FMS_FMS_ID")
	private Long fmsFmsId;

	@Column(name="PLATE_EXPIRY_DATE")
	private Date plateExpiryDate;   
    
    @Column(name="REGION_CODE")
	private String regionCode;

    public UnitRegistration() {}

    public Long getUrgId() {
        return urgId;
    }

    public void setUrgId(Long urgId) {
        this.urgId = urgId;
    }

    public Long getFmsFmsId() {
        return fmsFmsId;
    }

    public void setFmsFmsId(Long fmsFmsId) {
        this.fmsFmsId = fmsFmsId;
    }

    public Date getPlateExpiryDate() {
        return plateExpiryDate;
    }

    public void setPlateExpiryDate(Date plateExpiryDate) {
        this.plateExpiryDate = plateExpiryDate;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((urgId == null) ? 0 : urgId.hashCode());
        result = prime * result + ((fmsFmsId == null) ? 0 : fmsFmsId.hashCode());
        result = prime * result + ((plateExpiryDate == null) ? 0 : plateExpiryDate.hashCode());
        result = prime * result + ((regionCode == null) ? 0 : regionCode.hashCode());
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
        UnitRegistration other = (UnitRegistration) obj;
        if (urgId == null) {
            if (other.urgId != null)
                return false;
        } else if (!urgId.equals(other.urgId))
            return false;
        if (fmsFmsId == null) {
            if (other.fmsFmsId != null)
                return false;
        } else if (!fmsFmsId.equals(other.fmsFmsId))
            return false;
        if (plateExpiryDate == null) {
            if (other.plateExpiryDate != null)
                return false;
        } else if (!plateExpiryDate.equals(other.plateExpiryDate))
            return false;
        if (regionCode == null) {
            if (other.regionCode != null)
                return false;
        } else if (!regionCode.equals(other.regionCode))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UnitRegistration [urgId=" + urgId + ", fmsFmsId=" + fmsFmsId + ", plateExpiryDate=" + plateExpiryDate
                + ", regionCode=" + regionCode + "]";
    }
        
}

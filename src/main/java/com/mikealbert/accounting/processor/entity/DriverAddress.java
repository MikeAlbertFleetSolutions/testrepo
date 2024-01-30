package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the DRIVER_ADDRESSES database table.
 * 
 */
@Entity
@Table(name="DRIVER_ADDRESSES")
public class DriverAddress implements Serializable {

	@Id	
	@Column(name="DRA_ID")
	private Long draId;

	@Column(name="DRV_DRV_ID")
	private Long drvDrvId;
	
	@Column(name="REGION")
	private String region; 

	@Column(name="ADDRESS_TYPE")
	private String addressType; 	

	public DriverAddress() {}

	public Long getDraId() {
		return draId;
	}

	public void setDraId(Long draId) {
		this.draId = draId;
	}

	public Long getDrvDrvId() {
		return drvDrvId;
	}

	public void setDrvDrvId(Long drvDrvId) {
		this.drvDrvId = drvDrvId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((draId == null) ? 0 : draId.hashCode());
		result = prime * result + ((drvDrvId == null) ? 0 : drvDrvId.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((addressType == null) ? 0 : addressType.hashCode());
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
		DriverAddress other = (DriverAddress) obj;
		if (draId == null) {
			if (other.draId != null)
				return false;
		} else if (!draId.equals(other.draId))
			return false;
		if (drvDrvId == null) {
			if (other.drvDrvId != null)
				return false;
		} else if (!drvDrvId.equals(other.drvDrvId))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (addressType == null) {
			if (other.addressType != null)
				return false;
		} else if (!addressType.equals(other.addressType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DriverAddress [draId=" + draId + ", drvDrvId=" + drvDrvId + ", region=" + region + ", addressType="
				+ addressType + "]";
	}

}
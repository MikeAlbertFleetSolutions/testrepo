package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;

public class AddressVO implements Serializable {
    private static final long serialVersionUID = -4689274895245891578L;
    
	private String addressLine1;
	
	private String addressLine2;
	
	private String townDescription;
	
	private String regionCode;
	
	private String zipCode;
	
	private String countyCode;
	
	private String countryCode;

	public String getAddressLine1() {
		return addressLine1;
	}

	public AddressVO setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
		return this;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public AddressVO setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
		return this;
	}

	public String getTownDescription() {
		return townDescription;
	}

	public AddressVO setTownDescription(String townDescription) {
		this.townDescription = townDescription;
		return this;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public AddressVO setRegionCode(String regionCode) {
		this.regionCode = regionCode;
		return this;
	}

	public String getZipCode() {
		return zipCode;
	}

	public AddressVO setZipCode(String zipCode) {
		this.zipCode = zipCode;
		return this;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public AddressVO setCountyCode(String countyCode) {
		this.countyCode = countyCode;
		return this;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public AddressVO setCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	
}

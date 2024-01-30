package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Composite PK for CityZipCode
 * @author sibley
 */
@Embeddable
public class CityZipCodePK implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "COUNTRY_CODE")
	private String countryCode;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "REGION_CODE")
	private String regionCode;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "COUNTY_CODE")
	private String countyCode;

	@NotNull
	@Size(min = 1, max = 80)
	@Column(name = "CITY_CODE")
	private String cityCode;

	@NotNull
	@Size(min = 1, max = 25)
	@Column(name = "ZIP_CODE")
	private String zipCode;

	public CityZipCodePK() {}

	public CityZipCodePK(String countryCode, String regionCode, String countyCode, String cityCode, String zipCode) {
		this.countryCode = countryCode;
		this.regionCode = regionCode;
		this.countyCode = countyCode;
		this.cityCode = cityCode;
		this.zipCode = zipCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Override
	public String toString() {
		return "CityZipCodePK [countryCode=" + countryCode + ", regionCode=" + regionCode + ", countyCode=" + countyCode
				+ ", cityCode=" + cityCode + ", zipCode=" + zipCode + "]";
	}   
}

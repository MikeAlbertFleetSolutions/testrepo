package com.mikealbert.accounting.processor.vo;

import com.mikealbert.accounting.processor.validation.LeaseValidator;

@LeaseValidator
public class JurisdictionVO {	
	private String country;
	private String region;
	private String county;
	private String city;
	private String zip;
	private String geoCode;
	private String geoCode9;
	
	public JurisdictionVO() {}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getGeoCode9() {
		return geoCode9;
	}

	public void setGeoCode9(String geoCode9) {
		this.geoCode9 = geoCode9;
	}

	@Override
	public String toString() {
		return "JurisdictionVO [country=" + country + ", region=" + region + ", county=" + county + ", city=" + city
				+ ", zip=" + zip + ", geoCode=" + geoCode + ", geoCode9=" + geoCode9 + "]";
	};
	
}

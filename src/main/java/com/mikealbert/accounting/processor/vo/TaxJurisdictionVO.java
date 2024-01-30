package com.mikealbert.accounting.processor.vo;

public class TaxJurisdictionVO {
	String country;
	String region;
	String county;
	String city;
	String postalCode;
	String geoCode;
	String geoCode9;	
		
	public TaxJurisdictionVO() {}
	
	public TaxJurisdictionVO(String country, String region, String county, String city, String postalCode, String geoCode, String geoCode9) {
		this.country = country;
		this.region = region;
		this.county = county;
		this.city = city;
		this.postalCode = postalCode;
		this.geoCode = geoCode;
		this.geoCode9 = geoCode9;
	}
	
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
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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
		return "TaxJurisdictionVO [country=" + country + ", region=" + region + ", county=" + county + ", city=" + city
				+ ", postalCode=" + postalCode + ", geoCode=" + geoCode + ", geoCode9=" + geoCode9 + "]";
	}
	
}

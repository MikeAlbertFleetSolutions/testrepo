package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the EXT_ACC_ADDRESSES database table.
 * 
 */
@Entity
@Table(name="EXT_ACC_ADDRESSES")
public class ExtAccAddress extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EAA_ID_SEQ")    
	@SequenceGenerator(name="EAA_ID_SEQ", sequenceName="EAA_ID_SEQ", allocationSize=1)	
	@Column(name="EAA_ID")
	private Long eaaId;

	@Transient
	private String internalId;
	
	@Transient
	private boolean defaultBilling;
	
	@NotNull
	@Column(name="ADDRESS_CODE")
	private String addressCode;

	@Column(name="ADDRESS_LINE_1")
	private String addressLine1;

	@Column(name="ADDRESS_LINE_2")
	private String addressLine2;

	@Column(name="ADDRESS_LINE_3")
	private String addressLine3;

	@Column(name="ADDRESS_LINE_4")
	private String addressLine4;

	@Column(name="ADDRESS_TYPE")
	private String addressType;

	private String country;

	@Column(name="COUNTY_CODE")
	private String countyCode;

	@Column(name="DEFAULT_IND")
	private String defaultInd;

	@Column(name="GEO_CODE")
	private String geoCode;

	private String postcode;

	private String region;

	@Column(name="STREET_NO")
	private String streetNo;

	@Column(name="TOWN_CITY")
	private String townCity;

	//bi-directional many-to-one association to ExternalAccount
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="ACCOUNT_CODE", referencedColumnName="ACCOUNT_CODE"),
		@JoinColumn(name="ACCOUNT_TYPE", referencedColumnName="ACCOUNT_TYPE"),
		@JoinColumn(name="C_ID", referencedColumnName="C_ID")
		})
	private ExternalAccount externalAccount;
	
	@Transient
	private boolean newAddress;

	public ExtAccAddress() {
	}

	public Long getEaaId() {
		return this.eaaId;
	}

	public void setEaaId(Long eaaId) {
		this.eaaId = eaaId;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public boolean isDefaultBilling() {
		return defaultBilling;
	}

	public void setDefaultBilling(boolean defaultBilling) {
		this.defaultBilling = defaultBilling;
	}

	public String getAddressCode() {
		return this.addressCode;
	}

	public void setAddressCode(String addressCode) {
		this.addressCode = addressCode;
	}

	public String getAddressLine1() {
		return this.addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return this.addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return this.addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getAddressLine4() {
		return this.addressLine4;
	}

	public void setAddressLine4(String addressLine4) {
		this.addressLine4 = addressLine4;
	}

	public String getAddressType() {
		return this.addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountyCode() {
		return this.countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getDefaultInd() {
		return this.defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public String getGeoCode() {
		return this.geoCode;
	}

	public void setGeoCode(String geoCode) {
		this.geoCode = geoCode;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getStreetNo() {
		return this.streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getTownCity() {
		return this.townCity;
	}

	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}

	public ExternalAccount getExternalAccount() {
		return this.externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}
	
	public boolean isNewAddress() {
		return newAddress;
	}

	public void setNewAddress(boolean newAddress) {
		this.newAddress = newAddress;
	}

	@Override
	public int hashCode() {
		return Objects.hash(addressCode, addressLine1, addressLine2, addressLine3, addressLine4, addressType, country,
				countyCode, defaultBilling, defaultInd, eaaId, externalAccount, geoCode, internalId, postcode, region,
				streetNo, townCity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtAccAddress other = (ExtAccAddress) obj;
		return Objects.equals(addressCode, other.addressCode) && Objects.equals(addressLine1, other.addressLine1)
				&& Objects.equals(addressLine2, other.addressLine2) && Objects.equals(addressLine3, other.addressLine3)
				&& Objects.equals(addressLine4, other.addressLine4) && Objects.equals(addressType, other.addressType)
				&& Objects.equals(country, other.country) && Objects.equals(countyCode, other.countyCode)
				&& defaultBilling == other.defaultBilling && Objects.equals(defaultInd, other.defaultInd)
				&& Objects.equals(eaaId, other.eaaId) && Objects.equals(externalAccount, other.externalAccount)
				&& Objects.equals(geoCode, other.geoCode) && Objects.equals(internalId, other.internalId)
				&& Objects.equals(postcode, other.postcode) && Objects.equals(region, other.region)
				&& Objects.equals(streetNo, other.streetNo) && Objects.equals(townCity, other.townCity);
	}

	@Override
	public String toString() {
		return "ExtAccAddress [eaaId=" + eaaId + ", internalId=" + internalId + ", defaultBilling=" + defaultBilling
				+ ", addressCode=" + addressCode + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2
				+ ", addressLine3=" + addressLine3 + ", addressLine4=" + addressLine4 + ", addressType=" + addressType
				+ ", country=" + country + ", countyCode=" + countyCode + ", defaultInd=" + defaultInd + ", geoCode="
				+ geoCode + ", postcode=" + postcode + ", region=" + region + ", streetNo=" + streetNo + ", townCity="
				+ townCity + "]";
	}

	
}
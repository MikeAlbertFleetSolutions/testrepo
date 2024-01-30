package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Mapped to the CITY_ZIP_CODES table
 * @author sibley
 */
@Entity
@Table(name = "CITY_ZIP_CODES")
public class CityZipCode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected CityZipCodePK cityZipCodePK;

	@Column(name = "ZIP_CODE_END")
	private String zipCodeEnd;

	@Column(name = "GEO_CODE")
	private String geoCode;

	public CityZipCode() {}

	public CityZipCode(CityZipCodePK cityZipCodePK) {
		this.cityZipCodePK = cityZipCodePK;
	}

	@Override
	public String toString() {
		return "CityZipCode [cityZipCodePK=" + cityZipCodePK + ", zipCodeEnd=" + zipCodeEnd + ", geoCode=" + geoCode
				+ "]";
	}

}

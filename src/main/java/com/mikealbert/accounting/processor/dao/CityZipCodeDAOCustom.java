package com.mikealbert.accounting.processor.dao;

import java.util.List;

import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

public interface CityZipCodeDAOCustom {
	public List<TaxJurisdictionVO> findByJurisdiction(String country, String region, String county, String city, String zip);	
}

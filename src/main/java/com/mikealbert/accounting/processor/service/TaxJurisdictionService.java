package com.mikealbert.accounting.processor.service;

import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

public interface TaxJurisdictionService {
	TaxJurisdictionVO find(String country, String region, String county, String city, String zip, String rawZip) throws Exception ;
}

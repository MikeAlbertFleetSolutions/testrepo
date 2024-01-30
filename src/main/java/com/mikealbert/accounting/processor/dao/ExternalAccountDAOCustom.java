package com.mikealbert.accounting.processor.dao;

import java.util.List;

import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

public interface ExternalAccountDAOCustom {
	public String desencrypt(String data);
	public String desdecrypt(String data);
	public List<TaxJurisdictionVO> getTaxJurisdiction(String country, String region, String city, String zip);
	public String nextChildAccountCode();
}

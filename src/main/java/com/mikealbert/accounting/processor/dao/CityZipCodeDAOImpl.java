package com.mikealbert.accounting.processor.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.CityZipCode;
import com.mikealbert.accounting.processor.entity.CityZipCodePK;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

public class CityZipCodeDAOImpl extends GenericDAOImpl<CityZipCode, CityZipCodePK> implements CityZipCodeDAOCustom {
	private static final long serialVersionUID = 1935392298000887998L;

	@SuppressWarnings("unchecked")
	@Override
	public List<TaxJurisdictionVO> findByJurisdiction(String country, String region, String county, String city, String zip) {
		StringBuilder stmt;
		Query query;
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>(0);
		
		stmt = new StringBuilder();
		stmt.append("SELECT * ");
		stmt.append("  FROM table(jurisdiction_wrapper.find(:country, :region, :county, :city, :zip))");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("country", country);
		query.setParameter("region", region);
		query.setParameter("county", county);
		query.setParameter("city", city);
		query.setParameter("zip", zip);		

		List<Object[]> records =  (List<Object[]>) query.getResultList();		
		for(Object[] record : records) {
			int i = 0;
			
			TaxJurisdictionVO jurisdiction = new TaxJurisdictionVO();
			jurisdiction.setCountry((String)record[i]);
			jurisdiction.setRegion((String)record[i+=1]);
			jurisdiction.setCounty((String)record[i+=1]);
			jurisdiction.setCity((String)record[i+=1]);
			jurisdiction.setPostalCode((String)record[i+=1]);
			jurisdiction.setGeoCode((String)record[i+=1]);
			jurisdiction.setGeoCode9((String)record[i+=1]);
			
			jurisdictions.add(jurisdiction);
		}	
		
		return jurisdictions;
	}
		
}



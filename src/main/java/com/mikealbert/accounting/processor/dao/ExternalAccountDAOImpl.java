package com.mikealbert.accounting.processor.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

public class ExternalAccountDAOImpl extends GenericDAOImpl<ExternalAccount, ExternalAccountPK> implements ExternalAccountDAOCustom{

	private static final long serialVersionUID = 5089047604128704021L;

	@Override
	public String desencrypt(String data) {
		if(data != null) {
			String stmt = "select fl_card.convert(:data) from dual";
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("data", data);
			data = (String) query.getResultList().get(0);
		}
		return data;
	}

	@Override
	public String desdecrypt(String data) {
		if(data != null) {		
			String stmt = "select fl_card.revert(:data) from dual";
			Query query = entityManager.createNativeQuery(stmt);
			query.setParameter("data", data);
			data = (String) query.getResultList().get(0);
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TaxJurisdictionVO> getTaxJurisdiction(String country, String region, String city, String zip) {
		List<Object[]> records;
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		
		StringBuilder stmt = new StringBuilder();
		stmt.append(" SELECT czc.country_code, czc.region_code, czc.county_code, czc.city_code, czc.zip_code, czc.geo_code ");
		stmt.append("   FROM city_zip_codes czc ");
		stmt.append("   WHERE czc.country_code = :country ");
		stmt.append("     AND (czc.region_code = :region ");
		stmt.append("       OR EXISTS (SELECT 1 FROM region_codes rc2 WHERE rc2.country_code = czc.country_code AND upper(rc2.region_desc) = upper(:region))) ");
		stmt.append("     AND (czc.city_code = :city ");
		stmt.append("       OR EXISTS (SELECT 1 FROM town_city_codes tcc2 WHERE tcc2.country_code = czc.country_code AND tcc2.region_code = czc.region_code AND tcc2.county_code = czc.county_code AND tcc2.town_name = czc.city_code AND upper(tcc2.town_description) = upper(:city))) ");
		stmt.append("     AND ( (upper(:zip) BETWEEN upper(czc.zip_code) AND upper(czc.zip_code_end) ) ");
		stmt.append("       OR (czc.country_code = 'CN' AND upper(substr(:zip, 1, 3)) BETWEEN upper(czc.zip_code) AND upper(czc.zip_code_end)) ) ");

		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("country", country.toUpperCase());
		query.setParameter("region", region.toUpperCase());
		query.setParameter("city", city.toUpperCase());
		query.setParameter("zip", zip.toUpperCase());
		
		records =  (List<Object[]>) query.getResultList();
		
		for(Object[] record : records) {
			int i = 0;
			
			TaxJurisdictionVO jurisdiction = new TaxJurisdictionVO();
			jurisdiction.setCountry((String)record[i]);
			jurisdiction.setRegion((String)record[i+=1]);
			jurisdiction.setCounty((String)record[i+=1]);
			jurisdiction.setCity((String)record[i+=1]);
			jurisdiction.setPostalCode((String)record[i+=1]);
			jurisdiction.setGeoCode((String)record[i+=1]);			
									
			jurisdictions.add(jurisdiction);
		}
				
		return jurisdictions;
	}

	public String nextChildAccountCode() {
		String nextSeq;
		String stmt = " SELECT to_char(child_account_seq.nextval)  FROM dual ";		
		Query query = entityManager.createNativeQuery(stmt);
		nextSeq = (String) query.getSingleResult();
		return nextSeq;
	}

}

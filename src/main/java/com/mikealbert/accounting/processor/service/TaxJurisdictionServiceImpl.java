package com.mikealbert.accounting.processor.service;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.dao.CityZipCodeDAO;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;


@Service("jurisdictionService")
public class TaxJurisdictionServiceImpl extends BaseService implements TaxJurisdictionService {
	@Resource CityZipCodeDAO cityZipCodeDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource XRefService xRefService;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public TaxJurisdictionVO find(String country, String region, String county, String city, String zip, String rawZip) throws Exception {
		List<TaxJurisdictionVO> jurisdictions;
		TaxJurisdictionVO jurisdiction = null;

		LOG.info("Calling find jurisdiction with: country: {}, region: {}, county: {}, city: {}, zip: {} rawZip: {}", country, region, county, city, zip, rawZip);

		//TODO Hack to resolve production bug where NS is sending us a Canadian province that doesn't match the same province in W2k, e.g. QC(NS) -> PQ(W2k)
		String xRefRegion = xrefRegion(country, region);

		jurisdictions = cityZipCodeDAO.findByJurisdiction(country, xRefRegion, county, city, rawZip);
		if(!jurisdictions.isEmpty() ) {
			if(jurisdictions.size() > 1) {throw new Exception(String.format("When passed country: %s, region: %s, county: %s, city: %s, and zip: %s, Too many jurisdictions found: %s", country, region, county, city, rawZip, jurisdictions));}			
			jurisdiction = jurisdictions.get(0);
		}

		LOG.info("Tax jurisdiction find came back with {}", jurisdiction);

		return jurisdiction;
	}

	@Deprecated(forRemoval = true)
	private String xrefRegion(String country, String region) {
		String xrefRegion = region;	

		if(country.toUpperCase().startsWith("C")) {
			try {
				xrefRegion = xRefService.getInternalValue(XRefGroupNameEnum.CANADA_PROVINCE, region); //TODO Remove the CANADA_PROVINCE XRef when NS address scrubber and respective integration goes live				
			} catch (Exception e) {
				xrefRegion = region;
			}
		}		

		return xrefRegion;
	}

}
package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.dao.CityZipCodeDAO;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

@DisplayName("A jurisdiction")
@SpringBootTest
public class TaxJurisdictionServiceTest extends BaseTest{
	@Resource XRefService xRefService;
	@Resource TaxJurisdictionService taxJurisdictionService;
	
    @MockBean CityZipCodeDAO cityZipCodeDAO;
    @MockBean ExternalAccountDAO externalAccountDAO;
    
    static final String COUNTRY = "USA";
    static final String REGION = "OH";
    static final String COUNTY = "HAMILTON";
    static final String CITY = "Evendale";
    static final String ZIP = "45241-2512";
    static final String GEO_CODE = "0021";
    static final String GEO_CODE_9 = "00-000-0021";

    @DisplayName("When jurisdiction is cleansed, the matching jurisdiction record in the DB is returned")
    @Test
    public void testFind() throws Exception {   	
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(new TaxJurisdictionVO(COUNTRY, REGION, COUNTY, CITY, ZIP, GEO_CODE, GEO_CODE_9));   	

		when(cityZipCodeDAO.findByJurisdiction(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(jurisdictions);
		    	
		taxJurisdictionService.find(COUNTRY, REGION, COUNTY, CITY, ZIP, ZIP);
		
		assertEquals(jurisdictions.size(), 1);
    }
    
    @DisplayName("When jurisdiction is cleansed and find yields multiple DB records, an exception is raised")
    @Test
    public void testFindWithMultipleMatches() {  
		assertThrows(Exception.class, () -> {
			List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
			jurisdictions.add(new TaxJurisdictionVO(COUNTRY, REGION, COUNTY, CITY, ZIP, GEO_CODE, GEO_CODE_9));
			jurisdictions.add(new TaxJurisdictionVO(COUNTRY, REGION, COUNTY, CITY, ZIP, GEO_CODE, GEO_CODE_9));

			when(cityZipCodeDAO.findByJurisdiction(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(jurisdictions);
			
			taxJurisdictionService.find(COUNTRY, REGION, COUNTY, CITY, ZIP, ZIP);			
		});	
    }
    
    
    @Disabled
    @Deprecated(forRemoval = true)
	@Test
	public void testGetTaxJurisdiction() throws Exception {
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(new TaxJurisdictionVO(COUNTRY, REGION, COUNTY, CITY, ZIP, GEO_CODE, GEO_CODE_9));		
		jurisdictions.add(new TaxJurisdictionVO("USA", "OH", "061", "SYMMES TOWNSHIP", "45249", "0021", "00-000-0021"));	
		
		when(externalAccountDAO.getTaxJurisdiction(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(jurisdictions);
		TaxJurisdictionVO actualJurisdiction = taxJurisdictionService.find("us", "oh", null, "west chester", "45069", "45069");
		
		assertNotNull(actualJurisdiction, "Tax jurisdiction was not found");
		assertEquals(jurisdictions.get(0).getGeoCode(), actualJurisdiction.getGeoCode(), "Unexpected jurisdiction was returned");
	}
    
    @Disabled
    @Deprecated(forRemoval = true)    
    @DisplayName("When Canadian jurisdictions, the matching jurisdiction record in the DB is returned")
    @Test
    public void testFindUsingOldJurisdictionLookup() throws Exception {   	
    	
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(new TaxJurisdictionVO("CA", "PQ", null, "LaSalle", "H8N1X9", "nnnn", "nn-nnn-nnnn"));   	

		when(externalAccountDAO.getTaxJurisdiction(ArgumentMatchers.contains("CN"), ArgumentMatchers.contains("PQ"), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(jurisdictions);
		    	
		TaxJurisdictionVO taxJurisdiction = taxJurisdictionService.find("CA", "QC", null, "LaSalle", "nnnn", "nn-nnn-nnnn");
		
		assertNotNull(taxJurisdiction);
    }        
		
}
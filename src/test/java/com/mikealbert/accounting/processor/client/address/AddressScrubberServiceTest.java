package com.mikealbert.accounting.processor.client.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.address.scrubber.AddressScrubberService;
import com.mikealbert.webservice.address.scrubber.component.vo.CleansedAddress;
import com.mikealbert.webservice.address.scrubber.component.vo.RawAddress;

@SpringBootTest
public class AddressScrubberServiceTest extends BaseTest{
	@Resource AddressScrubberService addressScrubberService;
	
	@Test
	public void testUpdateAddress() throws Exception {
		final String RAW_ADDRESS_1 = "10340 Evendale Drive";
		final String RAW_ADDRESS_2 = "BLAH";		
		final String RAW_COUNTRY = "";	
		final String RAW_REGION = "";
		final String RAW_COUNTY = "";			
		final String RAW_CITY = "";
		final String RAW_POSTAL_CODE = "45241";		

		final String EXPECTED_ADDRESS_1 = "10340 Evendale Dr";
		final String EXPECTED_ADDRESS_2 = RAW_ADDRESS_2;		
		final String EXPECTED_COUNTRY = "USA";		
		final String EXPECTED_REGION = "OH";
		final String EXPECTED_COUNTY = "Hamilton";		
		final String EXPECTED_CITY = "Cincinnati";
		final String EXPECTED_NOTE = "Cleansed using address line 1";
		final String EXPECTED_POSTAL_CODE = "45241";
		
		CleansedAddress cleansedAddress = addressScrubberService.scrub(new RawAddress(new Date(), RAW_ADDRESS_1, RAW_ADDRESS_2, RAW_COUNTRY, RAW_REGION, RAW_COUNTY, RAW_CITY, RAW_POSTAL_CODE));
		
		assertEquals(EXPECTED_ADDRESS_1, cleansedAddress.getAddress1());
		assertEquals(EXPECTED_ADDRESS_2, cleansedAddress.getAddress2());
		assertEquals(EXPECTED_COUNTRY, cleansedAddress.getCountry());
		assertEquals(EXPECTED_REGION, cleansedAddress.getRegion());
		assertEquals(EXPECTED_COUNTY, cleansedAddress.getCounty());
		assertEquals(EXPECTED_CITY, cleansedAddress.getCity());
		assertEquals(EXPECTED_NOTE, cleansedAddress.getNote());
		assertEquals(1, cleansedAddress.getJurisdictions().size());
		assertTrue(cleansedAddress.getTaxAreaId() > 0);
		assertTrue(cleansedAddress.getPostalCode().contains(EXPECTED_POSTAL_CODE));
		
		cleansedAddress.getJurisdictions().stream()
		.forEach(jurisdiction -> { 
			assertTrue(jurisdiction.getId() > 0); 
		});
		
	}
		
}

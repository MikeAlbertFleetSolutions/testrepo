package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class VendorSuiteAnalyticsServiceTest extends BaseTest{
	@Resource VendorSuiteAnalyticsService vendorSuiteAnalyticsService;
		
	@Test
	public void testGetVendors() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -180);

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> vendors = vendorSuiteAnalyticsService.getVendors(start.getTime(), end.getTime());
		
		assertTrue(vendors.size() > 0, "Vendor records do not exists");
	}	

	@Test 
	public void testGetVendorAddresses() throws Exception {
		final String ENTITY_ID = "1125";
		
		List<Map<String, Object>> addresses = vendorSuiteAnalyticsService.getAddresses(ENTITY_ID);
		
		assertTrue(addresses.size() > 0, "Vendor Address records do not exists");
	}

	@Test 
	public void testGetVendorAddressesAll() throws Exception {		
		List<Map<String, Object>> addresses = vendorSuiteAnalyticsService.getAddresses();
		
		assertTrue(addresses.size() > 100, "Vendor Address records do not exists");		
	}	

	@Test 
	public void testGetVendorAddressesByExternalId() throws Exception {	
		final String EXTERNAL_ID = "310202";
		
		List<Map<String, Object>> addresses = vendorSuiteAnalyticsService.getAddressesByExternalId(EXTERNAL_ID);
		
		assertTrue(addresses.size() == 1, "Vendor Address records do not exists");
		assertTrue(addresses.get(0).get("externalId").equals(EXTERNAL_ID));
	}	
		
	@Disabled
	@Test
	public void testTranslator() {
		List<Map<String, Object>> vendorsIn = new ArrayList<Map<String, Object>>();
		Map<String, Object> one = new HashMap<>();
		Map<String, Object> two = new HashMap<>();
		Map<String, Object> three = new HashMap<>();
		
		one.put("code", "001");
		two.put("code", "002");
		three.put("code", "001");
		
		vendorsIn.add(one);
		vendorsIn.add(two);
		vendorsIn.add(three);
		

		Map<String, List<Map<String, Object>>> vendorsOut = vendorsIn.stream()
		.collect(Collectors.groupingBy(vendorMap -> (String) vendorMap.get("code"), HashMap::new, Collectors.toList()));	
		
		System.out.println(vendorsOut.toString());
	}

}

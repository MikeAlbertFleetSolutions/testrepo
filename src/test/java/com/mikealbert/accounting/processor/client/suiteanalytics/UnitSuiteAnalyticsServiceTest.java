package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UnitSuiteAnalyticsServiceTest extends BaseTest {
	
	@Resource UnitSuiteAnalyticsService unitSuiteAnalyticsService;
	
	@Disabled //TODO Consider removing this test as it takes a great amount of time to get all units
	@Test 
	public void testGetAllExternalUnits() throws Exception {		
		List<Map<String, Object>> units = unitSuiteAnalyticsService.getAllExternalUnits();
		assertTrue(units.size() > 100, "Unit records do not exist in external accounting system");		
	}	

	@Test 
	public void testGetExternalUnitByExternalId() throws Exception {	
		final String EXTERNAL_ID = "1290174";
		
		List<Map<String, Object>> unit = unitSuiteAnalyticsService.getExternalUnitByExternalId(EXTERNAL_ID);
		
		assertTrue(unit.size() == 1, "Unit record does not exists");
		assertTrue(unit.get(0).get("externalid").equals(EXTERNAL_ID));
	}	
	


}

package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@DisplayName("Customer")
public class CustomerSuiteAnalyticsServiceTest extends BaseTest{
	@Resource CustomerSuiteAnalyticsService customerSuiteAnalyticsService;
		
	@Test
	@DisplayName("when updates exists, then the updated customer records are returned")
	public void testGetCustomers() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -240);

		Calendar end = Calendar.getInstance();
		//end.add(Calendar.DATE, 1);		
		
		List<Map<String, Object>> customers = customerSuiteAnalyticsService.getCustomers(start.getTime(), end.getTime());
		
		
		assertTrue(customers.size() > 0, "Customer records do not exists"); //TODO Add a client in NS
	
	}		
}

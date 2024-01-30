package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;


@SpringBootTest
@DisplayName("A reqeust")
public class PurchaseOrderSuiteAnalyticsServiceTest extends BaseTest{
	@Resource PurchaseOrderSuiteAnalyticsService purchaseOrderSuiteAnalyticsService;
	
	//@Disabled
	@Test
	@DisplayName("when request is for closed purchase order transactions during a window of time, then the purchase order transactions for the time period are returned")
	public void testFindClosed() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -180 );

		Calendar end = Calendar.getInstance();
		
		List<Map<String, Object>> result = purchaseOrderSuiteAnalyticsService.findClosed(start.getTime(), end.getTime());
				
		assertTrue(result.size() > 0, "Purchase Order records do not exists");
	}		
}

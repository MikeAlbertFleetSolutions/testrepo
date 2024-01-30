package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.VendorBillPaymentFieldEnum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Vendor Bill Payment source")
public class VendorBillPaymentSuiteAnalyticsServiceTest extends BaseTest{
	@Resource VendorBillPaymentSuiteAnalyticsService vendorBillPaymentSuiteAnalyticsService;
		
	@Test
	@DisplayName("when bill payments were created during a specified time period, the payment records for the time period are returned")
	public void testGetVehiclePayments() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -240);

		Calendar end = Calendar.getInstance();
		//end.add(Calendar.DATE, 1);		
		
		List<Map<String, Object>> payments = vendorBillPaymentSuiteAnalyticsService.getVehiclePayments(start.getTime(), end.getTime());
		
		payments = payments.stream()
		.filter(record -> !record.get(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName()).equals("Prepayment"))
		.collect(Collectors.toList());
		
		assertTrue(payments.size() > 0, "Vehicle payment records do not exists");
	
	}	
	
	@Test
	@DisplayName("when vendor prepayment applications were created during a specified time period, the payment records for the time period are returned")
	public void testGetVehiclePrePayments() throws Exception {		
		Calendar start = Calendar.getInstance();
		start.add(Calendar.DATE, -240);

		Calendar end = Calendar.getInstance();
		//end.add(Calendar.DATE, 1);		
		
		List<Map<String, Object>> payments = vendorBillPaymentSuiteAnalyticsService.getVehiclePayments(start.getTime(), end.getTime());
		
		payments = payments.stream()
		.filter(record -> record.get(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName()).equals("Prepayment"))
		.collect(Collectors.toList());
		
		assertTrue(payments.size() > 0, "Vehicle payment records do not exists");
	
	}	
}

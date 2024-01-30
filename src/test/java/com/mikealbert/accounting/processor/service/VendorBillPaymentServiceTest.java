package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.VendorBillPaymentSuiteAnalyticsService;
import com.mikealbert.accounting.processor.dao.SupplierProgressHistoryDAO;
import com.mikealbert.accounting.processor.enumeration.VendorBillPaymentFieldEnum;
import com.mikealbert.util.data.DateUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Vendor Bill payment date range")
public class VendorBillPaymentServiceTest extends BaseTest{
	@Resource VendorBillPaymentService vendorBillPaymentService;
	
	@MockBean VendorBillPaymentSuiteAnalyticsService vendorBillPaymentSuiteAnalyticsService;
	@MockBean SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	
	@Test
	@DisplayName("vendor bill payments exists, they are transformed and returned")
	public void testGetVehiclePayments() throws Exception{		
		List<Map<String, Object>> mockPayments, actualPayments; 
		
		mockPayments = generateMockVendorBillPayment(2);
		
		when(vendorBillPaymentSuiteAnalyticsService.getVehiclePayments(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockPayments);
		
		actualPayments = vendorBillPaymentService.getVehiclePayments(new Date(), new Date());
		
		assertEquals(actualPayments, mockPayments);
		assertEquals(actualPayments.get(0).get(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName()), "EFTDAILY");
	}
	
	@Test
	@DisplayName("vendor bill payments does not exist, an empty list is returned")
	public void testGetVehiclePaymentsNoPaymentExists() throws Exception{		
		List<Map<String, Object>> mockPayments, actualPayments; 
		
		mockPayments = generateMockVendorBillPayment(0);
		
		when(vendorBillPaymentSuiteAnalyticsService.getVehiclePayments(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockPayments);
		
		actualPayments = vendorBillPaymentService.getVehiclePayments(new Date(), new Date());
		
		assertTrue(actualPayments.isEmpty());
	}	
	
	@Test
	@DisplayName("vendor bill payment exist, it is sent to the internal system")
	public void testNotify() throws Exception {
		Map<String, Object> mockPayment = generateMockVendorBillPayment(1).get(0);
		
		Long docId = Long.parseLong((String)mockPayment.get(VendorBillPaymentFieldEnum.EXTERNAL_ID.getName()));
		Date paymentDate = new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME).parse((String)mockPayment.get(VendorBillPaymentFieldEnum.PAYMENT_DATE.getName()));
		String paymentMethod = (String)mockPayment.get(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName());

		vendorBillPaymentService.notify(mockPayment);
		
    	verify(supplierProgressHistoryDAO, times(1)).logVehicleVendorBillPayment(
    			ArgumentMatchers.eq(docId), 
    			ArgumentMatchers.eq(paymentDate), 
    			ArgumentMatchers.eq(paymentMethod));
	}
	
	private List<Map<String, Object>> generateMockVendorBillPayment(int size) {
		List<Map<String, Object>> payments = new ArrayList<>(0);
		
		for(int i=0; i < size; i++) {
			Map<String, Object> payment = new HashMap<String, Object>();
			payment.put(VendorBillPaymentFieldEnum.EXTERNAL_ID.getName(), Integer.toString((i * -1)));
			payment.put(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName(), "ACH");
			payment.put(VendorBillPaymentFieldEnum.PAYMENT_DATE.getName(), new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME).format(new Date()));
			
			payments.add(payment);
		}
		return payments;
	}
	
}

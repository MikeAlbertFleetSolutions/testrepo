package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.client.suiteanalytics.VendorBillPaymentSuiteAnalyticsService;
import com.mikealbert.accounting.processor.dao.SupplierProgressHistoryDAO;
import com.mikealbert.accounting.processor.enumeration.VendorBillPaymentFieldEnum;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.util.data.DateUtil;

@Service("vendorBillPaymentService")
public class VendorBillPaymentServiceImpl extends BaseService implements VendorBillPaymentService {
	@Resource VendorBillPaymentSuiteAnalyticsService vendorBillPaymentSuiteAnalyticsService;
	@Resource XRefService xRefService;
	@Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public List<Map<String, Object>> getVehiclePayments(Date from, Date to) throws Exception {
		LOG.info("Get Vehicle Payments between {} and {}", from, to);
		
		List<Map<String, Object>> payments = vendorBillPaymentSuiteAnalyticsService.getVehiclePayments(from, to);
		
		payments.stream()
		.forEach(payment -> payment.replace("paymentMethod", xRefPaymentMethod(payment.get("paymentMethod"))));
		
		return payments;
	}	
	
	private String xRefPaymentMethod(Object paymentMethod) throws RuntimeException {
		String xRefPaymentMethod = (String)paymentMethod;

		try {
			xRefPaymentMethod = xRefService.getInternalValue(XRefGroupNameEnum.PAYMENT_METHOD, xRefPaymentMethod);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return xRefPaymentMethod;
	}

	@Override
	@Transactional
	public void notify(Map<String, Object> notification) throws Exception {
		LOG.info("Notify Willow of the vehicle's vendor payment. Notification received -> {}", notification.toString());
		
		Long docId = Long.parseLong((String)notification.get(VendorBillPaymentFieldEnum.EXTERNAL_ID.getName()));
		Date paymentDate = DateUtil.convertToDate((String)notification.get(VendorBillPaymentFieldEnum.PAYMENT_DATE.getName()));
		String paymentMethod = (String)notification.get(VendorBillPaymentFieldEnum.PAYMENT_METHOD.getName());
		
		supplierProgressHistoryDAO.logVehicleVendorBillPayment(docId, paymentDate, paymentMethod);
		
		LOG.info("Notified Willow of the vehicle's vendor payment - docId: {},  paymentDate: {],  paymentMethod: {}", 
				docId, paymentDate, paymentMethod);		
	}
	   
}

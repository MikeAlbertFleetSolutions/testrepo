package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.VendorBillPaymentService;

@Component("vendorBillPaymentNotificationProcessor")
public class VendorBillPaymentNotificationProcessor extends BaseProcessor implements Processor {	
	@Resource VendorBillPaymentService vendorBillPaymentService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {					
        LOG.info(ex.getExchangeId() + "PRE VendorBillPaymentNotificationProcessor... ");

		String message = (String)ex.getMessage().getBody();
		
		Map<String, Object> notification = super.convertJsonToObjectMap(message);
		
		LOG.info("Vendor Bill Payment Notification detail -> {}", notification.toString());

		vendorBillPaymentService.notify(notification);
				        
        LOG.info(ex.getExchangeId() + " POST VendorBillPaymentNotificationProcessor... " + message );
        
		ex.getIn().setBody(notification);
	}
			
}
 
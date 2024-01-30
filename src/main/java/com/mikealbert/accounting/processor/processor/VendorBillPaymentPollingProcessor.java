package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.VendorBillPaymentService;

@Component("vendorBillPaymentPollingProcessor")
public class VendorBillPaymentPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource VendorBillPaymentService vendorBillPaymentService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<Map<String, Object>> payments;
		
        LOG.info(ex.getExchangeId() + "PRE VendorBillPaymentPollingProcessor... ");
                    
        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
            	     
        payments = vendorBillPaymentService.getVehiclePayments(start, end);       
                
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(payments);
    	    	    	
        LOG.info(ex.getExchangeId() + " POST VendorBillPaymentPollingProcessor count ... " + payments.size());    	    	
	}
}

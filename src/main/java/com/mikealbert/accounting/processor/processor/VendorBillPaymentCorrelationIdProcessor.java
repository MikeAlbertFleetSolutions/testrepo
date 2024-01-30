package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("vendorBillPaymentCorrelationIdProcessor")
public class VendorBillPaymentCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " VendorBillPaymentCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> payment = (HashMap<String, Object>)ex.getIn().getBody();
	Long docId = Long.parseLong((String)payment.get("externalId"));

	LOG.info(ex.getExchangeId() + " VendorBillPaymentCorrelationIdProcessor midways through ");
	
	String correlationId = String.format("%d", docId);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " VendorBillPaymentCorrelationIdProcessor completed id=" + correlationId);
	}
}

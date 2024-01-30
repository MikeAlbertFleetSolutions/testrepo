package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("vendorCorrelationIdProcessor")
public class VendorCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " VendorCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> vendor = (HashMap<String, Object>)ex.getIn().getBody();
	String accountCode = (String) vendor.get("accountCode");

	LOG.info(ex.getExchangeId() + " CorrelationIdProcessor midways through ");
	
	String correlationId = (String)ex.getIn().getHeader("JMSCorrelationID"); 
	correlationId = String.format("%s", accountCode);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " VendorCorrelationIdProcessor completed id=" + correlationId);
	}
}

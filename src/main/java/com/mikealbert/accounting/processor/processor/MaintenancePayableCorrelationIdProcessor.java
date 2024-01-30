package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("maintenancePayableCorrelationIdProcessor")
public class MaintenancePayableCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " MaintenancePayableCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> invoice = (HashMap<String, Object>)ex.getIn().getBody();
	Long docId = (Long) invoice.get("docId");
	
	String correlationId = String.format("%d", docId);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " MaintenancePayableCorrelationIdProcessor completed id=" + correlationId);
	}
}

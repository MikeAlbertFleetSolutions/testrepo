package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("purchaseOrderCloseInboundCorrelationIdProcessor")
public class PurchaseOrderCloseInboundCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " PurchaseOrderCloseInboundCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> po = (HashMap<String, Object>)ex.getIn().getBody();
	String docId = (String) po.get("externalId");
	
	String correlationId = (String)ex.getIn().getHeader("JMSCorrelationID"); 
	correlationId = String.format("%s", docId);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " PurchaseOrderCloseInboundCorrelationIdProcessor completed id=" + correlationId);
	}
}

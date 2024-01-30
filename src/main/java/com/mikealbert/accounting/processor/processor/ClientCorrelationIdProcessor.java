package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientCorrelationIdProcessor")
public class ClientCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " ClientCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> client = (HashMap<String, Object>)ex.getIn().getBody();
	String accountCode = (String) client.get("accountCode");
	
	String correlationId = (String)ex.getIn().getHeader("JMSCorrelationID"); 
	correlationId = String.format("%s", accountCode);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " ClientCorrelationIdProcessor completed id=" + correlationId);
	}
}

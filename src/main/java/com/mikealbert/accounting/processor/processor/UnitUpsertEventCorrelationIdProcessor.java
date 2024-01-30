package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("unitUpsertEventCorrelationIdProcessor")
public class UnitUpsertEventCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " UnitUpsertEventCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, String> message = (HashMap<String, String>)ex.getIn().getBody();
	
	String correlationId = message.get("externalId");

	LOG.info(ex.getExchangeId() + " UnitUpsertEventCorrelationIdProcessor midways through ");
				
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " UnitUpsertEventCorrelationIdProcessor completed id=" + correlationId);
	}
}

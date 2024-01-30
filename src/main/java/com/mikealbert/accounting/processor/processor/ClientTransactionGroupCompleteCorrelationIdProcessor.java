package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientTransactionGroupCompleteCorrelationIdProcessor")
public class ClientTransactionGroupCompleteCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " ClientTransactionGroupCompleteCorrelationIdProcessor is started ...");
	
	String correlationId = String.valueOf(System.currentTimeMillis());
				
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " ClientTransactionGroupCompleteCorrelationIdProcessor completed id=" + correlationId);
	}
}

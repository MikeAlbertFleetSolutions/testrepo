package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.AccountingEventMessageVO;

@Component("leaseNovateCorrelationIdProcessor")
public class LeaseNovateCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
		LOG.info(ex.getExchangeId() + " LeaseNovateCorrelationIdProcessor is started ...");
		
		AccountingEventMessageVO message = (AccountingEventMessageVO)ex.getIn().getBody();
		String correlationId = message.getEntityId();
		ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
	    ex.getIn().setBody(ex.getIn().getBody());
	    
		LOG.info(ex.getExchangeId() + " LeaseNovateCorrelationIdProcessor completed id=" + correlationId);
	}
}

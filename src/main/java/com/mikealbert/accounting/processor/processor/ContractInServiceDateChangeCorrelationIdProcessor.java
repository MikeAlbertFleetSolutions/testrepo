package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.ContractInServiceDateChangeVO;

@Component("contractInServiceDateChangeCorrelationIdProcessor")
public class ContractInServiceDateChangeCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " ContractInServiceDateChangeCorrelationIdProcessor is started ...");
	
	ContractInServiceDateChangeVO contract = (ContractInServiceDateChangeVO)ex.getIn().getBody();
	   
	String correlationId = String.format("%d", contract.getId());	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " ContractInServiceDateChangeCorrelationIdProcessor completed id=" + correlationId);
	}
}

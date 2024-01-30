package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("prepareErrorProcessor")
public class PrepareErrorProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(PrepareErrorProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {			
        LOG.info(ex.getExchangeId() + " PrepareErrorProcessor... ");
        String message = null;
        
        Throwable cause = ex.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        
        message = cause.getMessage() == null ? cause.toString() : cause.getMessage();
        
        ex.getIn().setHeader("FailedBecause", message);
        		
        LOG.info(ex.getExchangeId() + " POST PrepareErrorProcessor... ");
	}
}

package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("errorProcessor")
public class ErrorProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(ErrorProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {			
        LOG.info(ex.getExchangeId() + " ErrorProcessor... ");
        
		Throwable caused = ex.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
		String message = caused.getMessage();
		Map<String, String> response = new HashMap<>();
		response.put("error", message);
		
		LOG.info(ex.getExchangeId() + " ErrorProcessor cause... " + caused.toString());

		ObjectMapper mapper = new ObjectMapper();
		message = mapper.writeValueAsString(response);
		
		ex.getIn().setBody(message);
		
        LOG.info(ex.getExchangeId() + " POST ErrorProcessor... ");

	}
}

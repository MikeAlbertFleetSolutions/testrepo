package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkDiagnosticService;
import com.mikealbert.accounting.processor.vo.SuiteTalkDiagnostic;

@Component("suiteTalkDiagnosticReadProcessor")
public class SuiteTalkDiagnosticReadProcessor implements Processor {
	@Resource SuiteTalkDiagnosticService suiteTalkDiagnosticService;
	
	private static final Logger LOG = LogManager.getLogger(SuiteTalkDiagnosticReadProcessor.class);
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START SuiteTalkDiagnosticReadProcessor... " + message);	
        
        SuiteTalkDiagnostic diagnostic = suiteTalkDiagnosticService.getSuiteTalkDiagnostic();
        
		ObjectMapper mapper = new ObjectMapper();
				
		String response = mapper.writeValueAsString(diagnostic);
		
        LOG.info(ex.getExchangeId() + " POST SuiteTalkDiagnosticReadProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

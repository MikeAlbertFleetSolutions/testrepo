package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.accounting.processor.service.XRefService;

@Component("xRefPutProcessor")
public class XRefPutProcessor extends BaseProcessor implements Processor {
	@Resource XRefService xRefService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START xrefPutProcessor... " + message);	
        
		Map<String, String> xRefMap = super.convertJsonToMap(message);
		
		XRef xRef = xRefService.createXRef(new XRef(xRefMap));
						
		String response = new ObjectMapper().writeValueAsString(xRef);
		
        LOG.info(ex.getExchangeId() + " POST xrefPutProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

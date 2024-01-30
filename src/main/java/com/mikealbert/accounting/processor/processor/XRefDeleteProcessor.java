package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.accounting.processor.service.XRefService;

@Component("xRefDeleteProcessor")
public class XRefDeleteProcessor extends BaseProcessor implements Processor {
	@Resource XRefService xRefService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START XrefDeleteProcessor... " + message);	
        
		Map<String, String> xRefMap = super.convertJsonToMap(message);
				
	    xRefService.deleteXRef(new XRef(xRefMap));
								
        LOG.info(ex.getExchangeId() + " POST XRefDeleteProcessor... ");
	}
}

package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.UnitService;

@Component("unitExternalReadProcessor")
public class UnitExternalReadProcessor extends BaseProcessor implements Processor {
	@Resource UnitService unitService;
	
	private static final Logger LOG = LogManager.getLogger(UnitExternalReadProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {		
		List<Map<String, Object>> units = null;
		String message = (String)ex.getMessage().getBody();
        LOG.info(ex.getExchangeId() + " START UnitExternalReadProcessor... " + message);
        
		Map<String, String> map = super.convertJsonToMap(message);
		String externalId = map.get("externalId");
		if(map.containsKey("externalId") && externalId.equals("*")) {
			units = unitService.getExternalUnits();
		} else {
			units = unitService.getExternalUnits(externalId);
		}

        String response = new ObjectMapper().writeValueAsString(units);
		ex.getIn().setBody(response);
		
        LOG.info(ex.getExchangeId() + " POST UnitExternalReadProcessor... ");
        
	}
}

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
import com.mikealbert.accounting.processor.service.LeaseService;

@Component("leaseExternalReadProcessor")
public class LeaseExternalReadProcessor extends BaseProcessor implements Processor {
	@Resource LeaseService leaseService;
	
	private static final Logger LOG = LogManager.getLogger(LeaseExternalReadProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {		
		List<Map<String, Object>> leases = null;
		String message = (String)ex.getMessage().getBody();
        
		Map<String, String> map = super.convertJsonToMap(message);
		String externalId = map.get("externalId");
		if(map.containsKey("externalId") && externalId.equals("*")) {
			leases = leaseService.getExternalLeases();
		} else {
			leases = leaseService.getExternalLeases(externalId);
		}

        String response = new ObjectMapper().writeValueAsString(leases);
		ex.getIn().setBody(response);
		
        LOG.info(ex.getExchangeId() + " POST LeaseExternalReadProcessor... " + leases.size());
        
	}
}

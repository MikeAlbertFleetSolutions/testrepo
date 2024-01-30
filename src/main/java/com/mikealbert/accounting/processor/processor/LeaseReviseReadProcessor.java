package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseReviseReadProcessor")
public class LeaseReviseReadProcessor implements Processor {
	@Resource LeaseService leaseService;
	
	private static final Logger LOG = LogManager.getLogger(LeaseReviseReadProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START LeaseReviseReadProcessor... " + message);	
        
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = mapper.readValue(message, new TypeReference<Map<String, String>>(){});
		
		LeaseVO lease = leaseService.getReviseLeaseRecord(map.get("qmdId"));
		
		ex.getIn().setBody(lease);
		
        LOG.info(ex.getExchangeId() + " POST LeaseReviseReadProcessor... ");        
	}
}

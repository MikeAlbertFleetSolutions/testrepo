package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;

@Component("leaseTerminateProcessor")
public class LeaseTerminateProcessor extends BaseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(LeaseTerminateProcessor.class);
	
	@Resource LeaseService leaseService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start LeaseTerminateProcessor... ");
        
        String json = (String)ex.getMessage().getBody();
        
		Map<String, String> map = super.convertJsonToMap(json);
		
		String quoId = map.get("quoId");
		
        LOG.info(ex.getExchangeId() + " LeaseTerminateProcessor quoId: " + quoId);
        
        List<Map<String, String>> response = leaseService.terminateLease(quoId);
		
        LOG.info(ex.getExchangeId() + " POST LeaseTerminateProcessor... " + response.toString());
	}
}

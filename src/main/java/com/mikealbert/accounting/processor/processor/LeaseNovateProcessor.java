package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseNovateProcessor")
public class LeaseNovateProcessor implements Processor {
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource LeaseService leaseService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start LeaseNovateProcessor... ");
        
		Object body = ex.getMessage().getBody();

		if(body == null) {
			LOG.info(ex.getExchangeId() + " LeaseNovateProcessor Lease is null");
			return;
		}

		LeaseVO lease = (LeaseVO)body;
		
        LOG.info(ex.getExchangeId() + " LeaseNovateProcessor Lease: " + lease);
        
        leaseService.novateLease(lease);
		
        LOG.info(ex.getExchangeId() + " POST LeaseNovateProcessor... ");
        
		ex.getIn().setBody(lease);
	}
}

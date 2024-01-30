package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseAmendProcessor")
public class LeaseAmendProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(LeaseAmendProcessor.class);
	
	@Resource LeaseService leaseService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start LeaseAmendProcessor... ");
        
        LeaseVO lease = (LeaseVO)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " LeaseAmendProcessor Lease: " + lease.toString());
        
        String response = leaseService.amendLease(lease);
		
        LOG.info(ex.getExchangeId() + " POST LeaseAmendProcessor... " + response);
        
		ex.getIn().setBody(lease);
	}
}

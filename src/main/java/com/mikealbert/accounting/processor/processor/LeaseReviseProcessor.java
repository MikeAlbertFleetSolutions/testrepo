package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseReviseProcessor")
public class LeaseReviseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(LeaseReviseProcessor.class);
	
	@Resource LeaseService leaseService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String response = "";
		LOG.info(ex.getExchangeId() + "Start LeaseReviseProcessor... ");
		
    	LeaseVO lease = (LeaseVO)ex.getMessage().getBody();
				
        LOG.info(ex.getExchangeId() + " LeaseReviseProcessor Lease: " + lease.toString());
        
        if(!lease.isAmendmentBeforeRevision()) {
        	response = leaseService.modifyLease(lease);
            LOG.info(ex.getExchangeId() + " POST LeaseReviseProcessor... " + response);
        } else {
            LOG.info(ex.getExchangeId() + " POST LeaseReviseProcessor... We have an Amendment before Revision. Lease not interfaced with NetSuite ");
        }
		
        
		ex.getIn().setBody(lease);
	}
}

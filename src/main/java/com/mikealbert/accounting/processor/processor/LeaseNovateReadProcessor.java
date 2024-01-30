package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.AccountingEventMessageVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseNovateReadProcessor")
public class LeaseNovateReadProcessor implements Processor {
	@Resource LeaseService leaseService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String body = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START LeaseNovateReadProcessor... " + body);	
        
		ObjectMapper mapper = new ObjectMapper();
		AccountingEventMessageVO message  = mapper.readValue(body, AccountingEventMessageVO.class);		
		
		LeaseVO lease = leaseService.getNovateLeaseRecord(message.getEntityId());
		
		ex.getIn().setBody(lease);
		
        LOG.info(ex.getExchangeId() + " POST LeaseNovateReadProcessor... ");        
	}
}

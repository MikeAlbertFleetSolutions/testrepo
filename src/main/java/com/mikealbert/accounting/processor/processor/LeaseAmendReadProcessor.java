package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("leaseAmendReadProcessor")
public class LeaseAmendReadProcessor extends BaseProcessor implements Processor {
	@Resource LeaseService leaseService;
	
	private static final Logger LOG = LogManager.getLogger(LeaseAmendReadProcessor.class);
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START LeaseAmendReadProcessor... " + message);	
        		
		Map<String, String> map = super.convertJsonToMap(message);
		
		LeaseVO lease = leaseService.getAmendLeaseRecord(map.get("qmdId"));
		
		EventEnum event = EventEnum.valueOf(map.get("event"));
		if(event == EventEnum.LEASE_AMENDMENT || event == EventEnum.LEASE_EXTENSION) {
			lease.setCbvImpact(true);
		}
		
		ex.getIn().setBody(lease);
		
        LOG.info(ex.getExchangeId() + " POST LeaseAmendReadProcessor... ");
	}
}

package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("leaseExternalApiReadProcessor")
public class LeaseExternalApiReadProcessor extends BaseProcessor implements Processor {
	@Resource LeaseService leaseService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
        
		Map<String, String> map = super.convertJsonToMap(message);
		String externalId = map.get("externalId");

		List<LeaseVO> leases = leaseService.getExternalLease(externalId, true);

		ex.getIn().setBody(leases);		
		
        LOG.info(ex.getExchangeId() + " POST LeaseExternalApiReadProcessor... ");        
	}
}

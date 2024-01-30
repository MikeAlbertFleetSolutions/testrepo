package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.address.scrubber.AddressScrubberService;
import com.mikealbert.webservice.address.scrubber.component.vo.CleansedAddress;
import com.mikealbert.webservice.address.scrubber.component.vo.RawAddress;

@Component("addressScrubberProcessor")
public class AddressScrubberProcessor extends BaseProcessor implements Processor {
	@Resource AddressScrubberService addressScrubberService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START AddressScrubberProcessor... " + message);	
        
		Map<String, String> map = super.convertJsonToMap(message);
		
		RawAddress rawAddress = new RawAddress(new Date(), map.get("address1"), map.get("address2"), map.get("country"), map.get("region"), map.get("county"), map.get("city"), map.get("postalCode"));
		CleansedAddress cleasedAddress = addressScrubberService.scrub(rawAddress);
			
		String response = new ObjectMapper().writeValueAsString(cleasedAddress);
		
        LOG.info(ex.getExchangeId() + " POST AddressScrubberProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

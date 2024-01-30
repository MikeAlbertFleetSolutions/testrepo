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
import com.mikealbert.accounting.processor.service.VendorService;

@Component("readVendorAddressesProcessor")
public class ReadVendorAddressesProcessor extends BaseProcessor implements Processor {
	@Resource VendorService vendorService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());	
	
	@Override
	public void process(Exchange ex) throws Exception {		
		List<Map<String, Object>> vendorAddresses = null;
		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START ReadVendorAddressesProcessor... " + message);	
        		
		Map<String, String> map = super.convertJsonToMap(message);

		String externalId = map.get("externalId");
		if(map.containsKey("externalId") && externalId.equals("*")) {
			vendorAddresses = vendorService.getAddresses();			
		} else {
			vendorAddresses = vendorService.getAddresses(externalId);
		}
		
		String response = new ObjectMapper().writeValueAsString(vendorAddresses);
		
        LOG.info(ex.getExchangeId() + " POST ReadVendorAddressesProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

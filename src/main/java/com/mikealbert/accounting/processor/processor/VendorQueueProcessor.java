package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.service.VendorService;

@Component("vendorQueueProcessor")
public class VendorQueueProcessor extends BaseProcessor implements Processor {	
	@Resource VendorService vendorService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {						
        LOG.info(ex.getExchangeId() + "PRE VendorQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();
				
		Map<String, String> vendor = super.convertJsonToMap(message);
		
		List<Map<String, Object>> addresses = vendorService.getAddresses(vendor);
		
		ExternalAccount account = vendorService.upsertVendor(vendor, addresses);

		List<Map<String, Object>> updatedVendorAddresses = vendorService.getAddresses(vendor);
				
		List<Long> deletedAddressIds = vendorService.reconcileDeletedAddresses(account, updatedVendorAddresses);

		if(!deletedAddressIds.isEmpty()) vendorService.resetDefaultAddress(account, updatedVendorAddresses);
		
		vendorService.closeChildAccounts(account, updatedVendorAddresses);
				        
        LOG.info(ex.getExchangeId() + " POST VendorQueueProcessor... " );
        
		ex.getIn().setBody(vendor);
	}
			
}
 
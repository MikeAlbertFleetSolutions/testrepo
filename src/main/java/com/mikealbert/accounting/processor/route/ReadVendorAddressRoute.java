package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.VendorQueueSelectorEnum;

@Component
public class ReadVendorAddressRoute extends BaseRoute {		
			
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), VendorQueueSelectorEnum.GET_VENDOR_ADDRESSES.getName()))
		.to("bean:readVendorAddressesProcessor");	 
	}
}

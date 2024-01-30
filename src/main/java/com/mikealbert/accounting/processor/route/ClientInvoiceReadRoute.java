package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.ClientQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class ClientInvoiceReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=2&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientQueueSelectorEnum.GET_INVOICE.getName()))
		.to("bean:clientInvoiceReadProcessor");
		
	}
}

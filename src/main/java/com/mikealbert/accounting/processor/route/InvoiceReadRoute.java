package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.InvoiceQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class InvoiceReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), InvoiceQueueSelectorEnum.GET_INVOICEAP_BY_DOCID.getName()))
		.to("bean:invoiceReadProcessor");				
	}
}

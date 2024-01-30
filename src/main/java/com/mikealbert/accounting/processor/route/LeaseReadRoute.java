package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.LeaseQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class LeaseReadRoute extends BaseRoute {		

	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
		.handled(true);

		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), LeaseQueueSelectorEnum.GET_INTERNAL_LEASE.getName()))
		.to("bean:leaseNewReadProcessor")
	    .marshal().json(JsonLibrary.Jackson)
	    .convertBodyTo(String.class);	

		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), LeaseQueueSelectorEnum.GET_REVISE_LEASE.getName()))
		.to("bean:leaseReviseReadProcessor")
	    .marshal().json(JsonLibrary.Jackson)
	    .convertBodyTo(String.class);

		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), LeaseQueueSelectorEnum.GET_AMEND_LEASE.getName()))
		.to("bean:leaseAmendReadProcessor")
	    .marshal().json(JsonLibrary.Jackson)
	    .convertBodyTo(String.class);		
	}

}

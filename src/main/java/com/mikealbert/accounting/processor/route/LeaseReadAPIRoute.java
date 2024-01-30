package com.mikealbert.accounting.processor.route;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.LeaseQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class LeaseReadAPIRoute extends BaseRoute {		

	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.useOriginalMessage()		
		.asyncDelayedRedelivery()
		.maximumRedeliveries(retryMax)
		.redeliveryDelay(retryDelay)
		.backOffMultiplier(2)
		.to("bean:errorProcessor")		
		.handled(true);

		from(String.format("jms:%s.%s?concurrentConsumers=5&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), LeaseQueueSelectorEnum.GET_EXTERNAL_API_LEASE.getName()))
		.to("bean:leaseExternalApiReadProcessor")
	    .marshal().json(JsonLibrary.Jackson)
	    .convertBodyTo(String.class);
	}

}

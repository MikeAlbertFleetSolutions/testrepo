package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.LeaseQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class LeaseExternalReadRoute extends BaseRoute {		

	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
		.handled(true);

		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), LeaseQueueSelectorEnum.GET_EXTERNAL_LEASE.getName()))
				.to("bean:leaseExternalReadProcessor");
	}

}

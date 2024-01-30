package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.BillingReportLeaseQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class BillingReportLeaseReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=5&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), BillingReportLeaseQueueSelectorEnum.GET_EFFECTIVE_LEASE.getName()))
		.to("bean:billingReportLeaseReadProcessor");		
	}
}

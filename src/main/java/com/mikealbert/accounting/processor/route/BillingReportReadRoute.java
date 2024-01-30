package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.BillingReportQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class BillingReportReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=1&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), BillingReportQueueSelectorEnum.GET_CLIENT_INVOICED_TRANSACTIONS.getName()))
		.to("bean:billingReportReadProcessor");		
	}
}
 
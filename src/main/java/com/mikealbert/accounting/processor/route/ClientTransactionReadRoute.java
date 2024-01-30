package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.ClientTransactionGroupQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.ClientTransactionQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class ClientTransactionReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=1&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientTransactionGroupQueueSelectorEnum.FIND_PENDING_BY_ACCOUNTING_PERIOD_ID.getName()))
		.to("bean:clientTransactionReadProcessor");		

		from(String.format("jms:%s.%s?concurrentConsumers=3&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientTransactionQueueSelectorEnum.GET_CLIENT_AGING_TRANSACTIONS.getName()))
		.to("bean:clientAgingTransactionReadProcessor");			
	}
}

package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.ClientQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class ClientReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=5&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientQueueSelectorEnum.GET_EXTERNAL_CLIENT.getName()))
		.to("bean:clientReadProcessor");

		from(String.format("jms:%s.%s?concurrentConsumers=1&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientQueueSelectorEnum.GET_EXTERNAL_CLIENTS_WITH_AGING_BALANCE.getName()))
		.to("bean:clientAgingBalanceReadProcessor");
		
		from(String.format("jms:%s.%s?concurrentConsumers=1&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), ClientQueueSelectorEnum.GET_EXTERNAL_CLIENTS_INCLUDE_PURCHASE_BALANCE.getName()))
		.to("bean:clientIncludePurchaseBalanceReadAllProcessor");		
		
	}
}

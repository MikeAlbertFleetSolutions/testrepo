package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.CacheQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class CacheRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), CacheQueueSelectorEnum.EVICT.getName()))
		.to("bean:cacheEvictProcessor");
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), CacheQueueSelectorEnum.GET_NAMES.getName()))
		.to("bean:cacheReadNamesProcessor");		
	}
}

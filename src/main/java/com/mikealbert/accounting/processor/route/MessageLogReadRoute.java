package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.MessageLogQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class MessageLogReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), MessageLogQueueSelectorEnum.GET_BY_ID.getName()))
		.to("bean:messageLogReadProcessor");
		
	}
}

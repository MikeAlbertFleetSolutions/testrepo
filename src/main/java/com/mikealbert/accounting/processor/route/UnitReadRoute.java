package com.mikealbert.accounting.processor.route;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.UnitQueueSelectorEnum;

@Component
public class UnitReadRoute extends BaseRoute {		

	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
		.handled(true);

		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), UnitQueueSelectorEnum.GET_UNIT_EXTERNAL.getName()))
				.to("bean:unitExternalReadProcessor");
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), UnitQueueSelectorEnum.GET_UNIT_INTERNAL.getName()))
		.to("bean:unitInternalReadProcessor")
	    .marshal().json(JsonLibrary.Jackson)
	    .convertBodyTo(String.class);
	}

}

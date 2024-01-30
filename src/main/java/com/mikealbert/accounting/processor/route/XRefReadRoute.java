package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.XRefQueueSelectorEnum;

@Component
public class XRefReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), XRefQueueSelectorEnum.GET_XREF_BY_GROUP_NAME.getName()))
		.to("bean:xRefReadByGroupNameProcessor");
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), XRefQueueSelectorEnum.GET_XREF_INTERNAL_VALUE.getName()))
		.to("bean:xRefReadInternalValueProcessor");
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), XRefQueueSelectorEnum.GET_XREF_EXTERNAL_VALUE.getName()))
		.to("bean:xRefReadExternalValueProcessor");		
	}
}

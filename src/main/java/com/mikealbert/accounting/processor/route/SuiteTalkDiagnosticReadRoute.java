package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.SuiteTalkDiagnosticQueueSelectorEnum;

@Component
public class SuiteTalkDiagnosticReadRoute extends BaseRoute {		
			
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), SuiteTalkDiagnosticQueueSelectorEnum.GET_SUITETALK_DIAGNOSTIC.getName()))
		.to("bean:suiteTalkDiagnosticReadProcessor");
	}
}

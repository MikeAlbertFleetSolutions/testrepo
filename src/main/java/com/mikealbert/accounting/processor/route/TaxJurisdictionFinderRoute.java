package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.TaxJurisdictionQueueSelectorEnum;

@Component
public class TaxJurisdictionFinderRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=15&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), TaxJurisdictionQueueSelectorEnum.GET_OLD_JURISDICTION.getName()))
		.to("bean:taxJurisdictionFinderProcessor");
				
	}
}

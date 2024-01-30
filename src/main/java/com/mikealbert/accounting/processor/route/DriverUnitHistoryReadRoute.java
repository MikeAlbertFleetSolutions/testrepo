package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.DriverUnitHistoryQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class DriverUnitHistoryReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?concurrentConsumers=5&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), DriverUnitHistoryQueueSelectorEnum.GET_EFFECTIVE_DRIVER_UNIT_HISTORY.getName()))
		.to("bean:driverUnitHistorySingleReadProcessor");
		
		from(String.format("jms:%s.%s?concurrentConsumers=5&selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), DriverUnitHistoryQueueSelectorEnum.GET_ALL_DRIVER_UNIT_HISTORY.getName()))
		.to("bean:driverUnitHistoryAllReadProcessor");
	}
}

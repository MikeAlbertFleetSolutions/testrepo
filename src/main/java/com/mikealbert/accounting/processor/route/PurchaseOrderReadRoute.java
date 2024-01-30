package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.PurchaseOrderQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class PurchaseOrderReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), PurchaseOrderQueueSelectorEnum.GET_PURCHASE_ORDER_BY_DOCID.getName()))
		.to("bean:purchaseOrderReadProcessor");				
	}

}
 
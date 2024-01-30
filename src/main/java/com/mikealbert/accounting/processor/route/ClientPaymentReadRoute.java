package com.mikealbert.accounting.processor.route;

import com.mikealbert.constant.accounting.enumeration.PaymentQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.springframework.stereotype.Component;

@Component
public class ClientPaymentReadRoute extends BaseRoute {		
		
	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
	    .handled(true);
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), PaymentQueueSelectorEnum.GET_LAST_PAYMENT_BY_INVOICE_EXTERNAL_ID.getName()))
		.to("bean:clientPaymentApplyReadProcessor");				
	}
}

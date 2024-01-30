package com.mikealbert.accounting.processor.route;

import org.springframework.stereotype.Component;

import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class AssetReadRoute extends BaseRoute {		

	@Override
	public void configure() throws Exception {		
		onException(Exception.class)
		.to("bean:errorProcessor")		
		.handled(true);

		/*
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AssetQueueSelectorEnum.PLACEINSERVICE.getName()))
		.to("bean:assetReadProcessor");
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AssetQueueSelectorEnum.CREATE.getName()))
		.to("bean:assetReadProcessor");
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AssetQueueSelectorEnum.UPDATE_TYPE.getName()))
		.to("bean:assetReadProcessor");
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AssetQueueSelectorEnum.CANCEL_PO_TO_STOCK.getName()))
		.to("bean:assetReadProcessor");
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AssetQueueSelectorEnum.DISPOSE.getName()))
		.to("bean:assetReadProcessor");
		*/
		from(String.format("jms:%s.%s?selector=noun='%s'", qPrefix, QueueEnum.ACCOUNTING_REQUEST_RESPONSE.getName(), AccountingNounEnum.ASSET))
		.to("bean:assetReadProcessor");
		
		
	}
}
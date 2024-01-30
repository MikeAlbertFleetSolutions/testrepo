package com.mikealbert.accounting.processor.route;

import java.sql.SQLRecoverableException;

import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.processor.PrepareErrorProcessor;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.XRefQueueSelectorEnum;

import oracle.net.ns.NetException;

@Component
public class XRefDeleteRoute extends BaseRoute {		
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure() throws Exception {	
		
		onException(NetException.class, SQLRecoverableException.class ) 
		.asyncDelayedRedelivery()
		.maximumRedeliveries(retryMax)
		.redeliveryDelay(retryDelay)
		.maximumRedeliveryDelay(retryDelayMax)
		.backOffMultiplier(2);		
		
		errorHandler(deadLetterChannel(String.format("jms:%s.%s.%s", qPrefix, QueueEnum.XREF.getName(), "DEAD"))
				.useOriginalMessage()				
				.onPrepareFailure(new PrepareErrorProcessor())
				.logExhausted(true)
				.logHandled(true)
				.retryAttemptedLogLevel(LoggingLevel.WARN)
				.retriesExhaustedLogLevel(LoggingLevel.ERROR));	
		
		from(String.format("jms:%s.%s?selector=action='%s'", qPrefix, QueueEnum.XREF.getName(), XRefQueueSelectorEnum.DELETE_XREF.getName()))
		.to("bean:xRefDeleteProcessor");		
	}

}

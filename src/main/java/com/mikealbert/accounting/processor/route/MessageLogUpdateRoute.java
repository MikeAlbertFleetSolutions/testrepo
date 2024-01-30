package com.mikealbert.accounting.processor.route;

import java.sql.SQLRecoverableException;

import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.processor.PrepareErrorProcessor;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class MessageLogUpdateRoute extends BaseRoute{

	@Override
	public void configure() throws Exception {
		onException(SQLRecoverableException.class)
		.useOriginalMessage()		
		.asyncDelayedRedelivery()
		.maximumRedeliveries(retryMax)
		.redeliveryDelay(retryDelay)
		.maximumRedeliveryDelay(retryDelayMax)
		.backOffMultiplier(2);
			
		errorHandler(deadLetterChannel(String.format("jms:%s.%s.%s", qPrefix, QueueEnum.MESSAGE_LOG_UPDATE.getName(), "DEAD"))
				.useOriginalMessage()				
				.onPrepareFailure(new PrepareErrorProcessor())
				.logExhausted(true)
				.logHandled(true)
				.retryAttemptedLogLevel(LoggingLevel.WARN)
				.retriesExhaustedLogLevel(LoggingLevel.ERROR));

		from(String.format("jms:%s.%s", qPrefix, QueueEnum.MESSAGE_LOG_UPDATE.getName()))
		.to("bean:messageLogUpdateProcessor");
	}

}

package com.mikealbert.accounting.processor.route;

import java.sql.SQLRecoverableException;

import org.apache.axis.AxisFault;
import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.processor.PrepareErrorProcessor;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.netsuite.webservices.platform.faults_2023_2.ExceededConcurrentRequestLimitFault;
import com.netsuite.webservices.platform.faults_2023_2.ExceededRequestLimitFault;
import com.netsuite.webservices.platform.faults_2023_2.ExceededRequestSizeFault;
import com.netsuite.webservices.platform.faults_2023_2.ExceededUsageLimitFault;
import com.netsuite.webservices.platform.faults_2023_2.InvalidCredentialsFault;
import com.netsuite.webservices.platform.faults_2023_2.InvalidSessionFault;
import com.netsuite.webservices.platform.faults_2023_2.UnexpectedErrorFault;

import oracle.net.ns.NetException;

@Component
public class LeaseNovateRoute extends BaseRoute {		
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure() throws Exception {
		
		onException(
				ExceededConcurrentRequestLimitFault.class, ExceededRequestLimitFault.class, ExceededRequestSizeFault.class, 
				ExceededUsageLimitFault.class, InvalidSessionFault.class, UnexpectedErrorFault.class, NetException.class, SQLRecoverableException.class, 
				InvalidCredentialsFault.class, AxisFault.class, RetryableSuiteTalkException.class)
		.asyncDelayedRedelivery()
		.maximumRedeliveries(retryMax)
		.redeliveryDelay(retryDelay)
		.maximumRedeliveryDelay(retryDelayMax)
		.backOffMultiplier(2);		
		
		errorHandler(deadLetterChannel(String.format("jms:%s.%s.%s", qPrefix, QueueEnum.LEASE_NOVATE.getName(), "DEAD"))
				.useOriginalMessage()
				.onPrepareFailure(new PrepareErrorProcessor())
				.logExhausted(true)
				.logHandled(true)
				.retryAttemptedLogLevel(LoggingLevel.WARN)
				.retriesExhaustedLogLevel(LoggingLevel.ERROR));
		
		from(String.format("jms:%s.%s", qPrefix, QueueEnum.LEASE_NOVATE.getName()))
		.to("bean:leaseNovateReadProcessor")
		.to("bean:leaseNovateProcessor");
	}
}

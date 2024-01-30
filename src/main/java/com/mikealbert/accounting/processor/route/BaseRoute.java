package com.mikealbert.accounting.processor.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
abstract class BaseRoute extends RouteBuilder {		
	@Value("${mafs.broker.queue.prefix}")
	String qPrefix;
	
	@Value("${mafs.route.retry.max}")
	int retryMax;	
	
	@Value("${mafs.route.retry.delay}")
	long retryDelay;	

	@Value("${mafs.route.retry.delay.max}")
	long retryDelayMax;	
}

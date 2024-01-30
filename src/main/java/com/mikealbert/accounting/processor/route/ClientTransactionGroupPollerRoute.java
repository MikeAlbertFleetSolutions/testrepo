package com.mikealbert.accounting.processor.route;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientTransactionGroupPollerRoute extends BaseRoute {					
	@Value("${mafs.polling.cron.client.transaction-group}")
	String eventPollingCron;	
	
	@Value("${mafs.scheduler.instance.name}")
	String schedulerInstanceName;
		
	@Override
	public void configure() throws Exception {		  		
		from(String.format("quartz2://%s/pollNetSuiteClientTransactionGroupsTimer?stateful=true&cron=%s", schedulerInstanceName, eventPollingCron))	
		  .setHeader(CustomHeader.POLLER_NAME, constant(PollerEnum.CLIENT_TRANSACTION_GROUPS.getName()))		
	      .to("bean:clientTransactionGroupPollingProcessor")
		  .split(body())
  		    .to("bean:clientTransactionGroupCorrelationIdProcessor")
		    .marshal().json(JsonLibrary.Jackson)
		    .to(String.format("jms:queue:%s.%s?%s&useMessageIDAsCorrelationID=false", qPrefix , QueueEnum.CLIENT_TRANSACTION_GROUP_UPDATE.getName(), "jmsMessageType=Text"))
	      .end()
	      .to("bean:appLogHandler");		
	}
}

package com.mikealbert.accounting.processor.route;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UnitUpsertEventPollerRoute extends BaseRoute {					
	@Value("${mafs.polling.cron.unit-upsert-event}")
	String eventPollingCron;	
	
	@Value("${mafs.scheduler.instance.name}")
	String schedulerInstanceName;
		
	@Override
	public void configure() throws Exception {		  		
		from(String.format("quartz2://%s/pollUnitUpsertEventTimer?stateful=true&cron=%s", schedulerInstanceName, eventPollingCron))	
		  .setHeader(CustomHeader.POLLER_NAME, constant(PollerEnum.UNIT_UPSERT_EVENT.getName()))		
	      .to("bean:unitUpsertEventPollingProcessor")
		  .split(body())
  		    .to("bean:unitUpsertEventCorrelationIdProcessor")		  
		    .marshal().json(JsonLibrary.Jackson)
		    .to(String.format("jms:queue:%s.%s?%s&useMessageIDAsCorrelationID=false", qPrefix , QueueEnum.UNIT_UPSERT.getName(), "jmsMessageType=Text"))
	      .end()
	      .to("bean:appLogHandler");		
	}
}

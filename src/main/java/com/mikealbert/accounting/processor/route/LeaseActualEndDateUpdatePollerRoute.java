package com.mikealbert.accounting.processor.route;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component
public class LeaseActualEndDateUpdatePollerRoute extends BaseRoute {					
	@Value("${mafs.polling.cron.lease.actual-end-date-update-event}")
	String eventPollingCron;	
	
	@Value("${mafs.scheduler.instance.name}")
	String schedulerInstanceName;
		
	@Override
	public void configure() throws Exception {		  		
		from(String.format("quartz2://%s/pollLeaseActualEndDateUpdateEventTimer?stateful=true&cron=%s", schedulerInstanceName, eventPollingCron))	
		  .setHeader(CustomHeader.POLLER_NAME, constant(PollerEnum.LEASE_ACTUAL_END_DATE_UPDATE.getName()))		
	      .to("bean:leaseActualEndDateUpdatePollingProcessor")
		  .split(body())
  		    .to("bean:leaseActualEndDateUpdateCorrelationIdProcessor")		  
		    .marshal().json(JsonLibrary.Jackson)
		    .to(String.format("jms:queue:%s.%s?%s&useMessageIDAsCorrelationID=false", qPrefix , QueueEnum.LEASE_ACTUAL_END_DATE_UPDATE.getName(), "jmsMessageType=Text"))
	      .end()
	      .to("bean:appLogHandler");		
	}

}

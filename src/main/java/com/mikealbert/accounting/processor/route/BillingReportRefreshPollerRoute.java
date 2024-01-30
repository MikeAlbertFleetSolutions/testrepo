package com.mikealbert.accounting.processor.route;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.enumeration.PollerEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;


@Component
public class BillingReportRefreshPollerRoute extends BaseRoute {					
	@Value("${mafs.polling.cron.client.billing-report-refresh}")
	String eventPollingCron;	
	
	@Value("${mafs.scheduler.instance.name}")
	String schedulerInstanceName;
		
	@Override
	public void configure() throws Exception {		  		
		from(String.format("quartz2://%s/pollBillingReportRefreshTimer?stateful=true&cron=%s", schedulerInstanceName, eventPollingCron))	
		  .setHeader(CustomHeader.POLLER_NAME, constant(PollerEnum.BILLING_REPORT_REFRESH.getName()))		
	      .to("bean:billingReportRefreshPollingProcessor")
		  .split(body())
  		    .to("bean:billingReportRefreshCorrelationIdProcessor")
		    .marshal().json(JsonLibrary.Jackson)
		    .to(String.format("jms:queue:%s.%s?%s&useMessageIDAsCorrelationID=false", qPrefix , QueueEnum.BILLING_REPORT_REFRESH.getName(), "jmsMessageType=Text"))
	      .end()
	      .to("bean:appLogHandler");		
	}
}

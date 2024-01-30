package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;

@Component("billingReportRefreshCorrelationIdProcessor")
public class BillingReportRefreshCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " BillingReportRefreshCorrelationIdProcessor is started ...");
	
	BillingReportRefreshMessageVO message = (BillingReportRefreshMessageVO)ex.getIn().getBody();	
	
	String correlationId = (String)ex.getIn().getHeader("JMSCorrelationID"); 
	correlationId = String.format("%s.%s.%s", message.getAccountCode(), message.getStartPeriod(), message.getEndPeriod());	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " BillingReportRefreshCorrelationIdProcessor completed id=" + correlationId);
	}
}

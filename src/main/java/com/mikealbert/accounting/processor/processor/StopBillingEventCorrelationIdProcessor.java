package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.LeaseTerminationRequestVO;

@Component("stopBillingEventCorrelationIdProcessor")
public class StopBillingEventCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " StopBillingEventCorrelationIdProcessor is started ...");
	
	LeaseTerminationRequestVO leaseTerminationRequest = (LeaseTerminationRequestVO)ex.getIn().getBody();
	Long id = leaseTerminationRequest.getQuoId();

	LOG.info(ex.getExchangeId() + " StopBillingEventCorrelationIdProcessor midways through ");
	
	String correlationId = String.format("%d", id);	
			
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " StopBillingEventCorrelationIdProcessor completed id=" + correlationId);
	}
}

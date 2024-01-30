package com.mikealbert.accounting.processor.processor;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;

@Component("disposalInvoiceCorrelationIdProcessor")
public class DisposalInvoiceCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {
		LOG.info(ex.getExchangeId() + this.getClass().getSimpleName() + " is started ...");

		DisposalInvoiceVO disposalInvoice = (DisposalInvoiceVO)ex.getIn().getBody();
								
		ex.getIn().setHeader("JMSCorrelationID", disposalInvoice.getTransactionExtId());
	    ex.getIn().setBody(ex.getIn().getBody());
	    
		LOG.info(ex.getExchangeId() + this.getClass().getSimpleName() + " Transaction Ext Id =" + disposalInvoice.getTransactionExtId());
	}
}

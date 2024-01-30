package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.InvoiceService;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;

@Component("disposalInvoiceNsToWillowQueueProcessor")
public class DisposalInvoiceNsToWillowQueueProcessor extends BaseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(DisposalInvoiceNsToWillowQueueProcessor.class);
	
	@Resource InvoiceService invoiceService;
		
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start DisposalInvoiceNsToWillowQueueProcessor... ");
		String message = (String)ex.getMessage().getBody();

        ObjectMapper mapper = new ObjectMapper();
        DisposalInvoiceVO disposalInvoice = mapper.readValue(message, DisposalInvoiceVO.class);
		
        LOG.info(ex.getExchangeId() + " DisposalInvoiceNsToWillowQueueProcessor " + disposalInvoice.toString());
        
        if(disposalInvoice.getTransactionId() != null)
        	invoiceService.processNsToWillowDisposalInvoice(disposalInvoice);
                
        LOG.info(ex.getExchangeId() + " End DisposalInvoiceNsToWillowQueueProcessor " + disposalInvoice.toString());
        
		ex.getIn().setBody(disposalInvoice);
	}
}

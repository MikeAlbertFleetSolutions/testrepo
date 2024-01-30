package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.CreditService;
import com.mikealbert.accounting.processor.service.InvoiceService;

@Component("invoiceInitializationProcessor")
public class InvoiceInitializationProcessor extends BaseProcessor implements Processor{
	@Resource InvoiceService invoiceService;
	@Resource CreditService creditService;
	
	private static final Logger LOG = LogManager.getLogger(InvoiceInitializationProcessor.class);
	
	@Override
	public void process(Exchange ex) throws Exception {				
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start invoiceInitializationProcessor ... {}", message);
		
		Map<String, Object> map = super.convertJsonToObjectMap(message);
		
		invoiceService.updateGlAccToOne(((Integer)map.get("docId")).longValue());		
									
		LOG.info(ex.getExchangeId() + "End invoiceInitializationProcessor ...");
	}
	
}

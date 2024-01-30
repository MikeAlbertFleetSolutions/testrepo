package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.CreditService;
import com.mikealbert.accounting.processor.service.InvoiceService;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;

@Component("invoiceReadProcessor")
public class InvoiceReadProcessor extends BaseProcessor implements Processor{
	@Resource InvoiceService invoiceService;
	@Resource CreditService creditService;
	
	private static final Logger LOG = LogManager.getLogger(InvoiceReadProcessor.class);
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String response;
		
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start InvoiceReadProcessor ... {}", message);
				
		Map<String, Object> map = super.convertJsonToObjectMap(message);
			
		ObjectMapper mapper = new ObjectMapper();
		
		boolean isCredit = map.get("isCredit") == null || !((boolean)map.get("isCredit")) ? false : true;		
		if(isCredit) {
			CreditVO payableTransaction = creditService.getCredit(((Integer)map.get("docId")).longValue());
			response = mapper.writeValueAsString(payableTransaction);			
		} else {
			InvoiceVO payableTransaction = invoiceService.getInvoice(((Integer)map.get("docId")).longValue(), (Boolean)map.get("copyPurchaseOrder"));
			response = mapper.writeValueAsString(payableTransaction);			
		}
						
		LOG.info(ex.getExchangeId() + "End InvoiceReadProcessor ... {}", response);
		
		ex.getIn().setBody(response);		
	}
	
}

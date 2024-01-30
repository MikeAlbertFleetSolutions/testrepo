package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.service.CreditService;
import com.mikealbert.accounting.processor.service.InvoiceService;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.accounting.processor.vo.TransactionVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("invoiceNewProcessor")
public class InvoiceNewProcessor implements Processor {
	@Resource DocDAO docDAO;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource InvoiceService invoiceService;
	@Resource CreditService creditService;
		
	private final Logger LOG = LogManager.getLogger(this.getClass()); 

	@Override
	public void process(Exchange ex) throws Exception {
        LOG.info(ex.getExchangeId() + "Start InvoiceNewProcessor... ");
        
		String message = (String)ex.getMessage().getBody();        
        
		ObjectMapper mapper = new ObjectMapper();
		TransactionVO<?, ?> transaction = mapper.readValue(message, TransactionVO.class);
	
		if(transaction.getClass().getName().equals(InvoiceVO.class.getName())) {
			InvoiceVO invoice = mapper.readValue(message, InvoiceVO.class);
			
			if(invoice.isCreateFromPurchaseOrder()) {
				Long poExternalId = docDAO.getPurchaseOrderDocIdByInvoiceDocId(Long.valueOf(invoice.getExternalId()));
				PurchaseOrderVO po = purchaseOrderService.get(poExternalId);
				invoiceService.create(invoice, po);			
			} else {
				invoiceService.create(invoice);			
			}			
		} else {
			CreditVO credit = mapper.readValue(message, CreditVO.class);
			creditService.create(credit);
		}
		        	        
        LOG.info(ex.getExchangeId() + "End InvoiceNewProcessor ...");
        
        ex.getIn().setBody(message);		
	}

}

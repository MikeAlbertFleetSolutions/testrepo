package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

@Component("purchaseOrderNewProcessor")
public class PurchaseOrderNewProcessor implements Processor {	
	@Resource PurchaseOrderService purchaseOrderService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {			
		PurchaseOrderVO po = null;
		
		LOG.info(ex.getExchangeId() + "PRE PurchaseOrderNewProcessor... ");

		//Message body/PO can be null here. For example, PO was immediately cancelled. 
		String message = (String)ex.getMessage().getBody();
		if(message != null && !message.equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			po = mapper.readValue(message, PurchaseOrderVO.class);

			purchaseOrderService.add(po);
		}
		
        LOG.info(ex.getExchangeId() + "POST PurchaseOrderNewProcessor... " + po);	
        
		ex.getIn().setBody(message);
	}
			
}
 
package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;


@Component("purchaseOrderCloseInboundQueueProcessor")
public class PurchaseOrderCloseInboundQueueProcessor extends BaseProcessor implements Processor {	

	@Resource MessageLogService messageLogService;
	@Resource PurchaseOrderService purchaseOrderService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE PurchaseOrderCloseInboundQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();		
		Map<String, Object> po = new ObjectMapper().readValue(message, Map.class);
		String docId = (String) po.get("externalId");
		if(docId != null && !docId.equalsIgnoreCase("")) {
			purchaseOrderService.closeInternal(Long.valueOf(docId));
		}
														               
		ex.getIn().setBody(message);

        LOG.info(ex.getExchangeId() + " POST PurchaseOrderCloseInboundQueueProcessor completed ... " + message);
	}
	
	
}
 
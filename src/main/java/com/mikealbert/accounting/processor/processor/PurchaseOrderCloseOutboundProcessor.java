package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.exception.SuiteTalkNoRecordFoundException;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;

@Component("purchaseOrderCloseOutboundProcessor")
public class PurchaseOrderCloseOutboundProcessor extends BaseProcessor implements Processor {	
	@Resource PurchaseOrderService purchaseOrderService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {	
		Long docId;
		
        LOG.info(ex.getExchangeId() + "PRE PurchaseOrderCloseOutboundProcessor... ");

		String json = (String)ex.getMessage().getBody();
		
		Map<String, String> map = super.convertJsonToMap(json);
		docId = Long.parseLong(map.get("docId"));
		
		try {
			purchaseOrderService.closeExternal(docId);
		} catch(SuiteTalkNoRecordFoundException e) {
			Integer redeliveryCounter = (Integer)ex.getMessage().getHeader(Exchange.REDELIVERY_COUNTER);
			if(redeliveryCounter != null && redeliveryCounter.compareTo(retryMax - 1) == 0) {
				LOG.warn(e.getMessage());				
			} else {
				throw e;
			}
		}
		
        LOG.info(ex.getExchangeId() + "POST PurchaseOrderCloseOutboundProcessor... " + json);	
	}
			
}
 
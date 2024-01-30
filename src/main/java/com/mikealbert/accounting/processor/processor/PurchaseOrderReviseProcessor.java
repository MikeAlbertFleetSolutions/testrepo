package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.PurchaseOrderService;

@Component("purchaseOrderReviseProcessor")
public class PurchaseOrderReviseProcessor extends BaseProcessor implements Processor {	
	@Resource PurchaseOrderService purchaseOrderService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {			
        LOG.info(ex.getExchangeId() + "PRE PurchaseOrderReviseProcessor... ");

		String json = (String)ex.getMessage().getBody();
		
		Map<String, String> map = super.convertJsonToMap(json);
						
		purchaseOrderService.revise(Long.parseLong(map.get("docId")));
		
        LOG.info(ex.getExchangeId() + "POST PurchaseOrderReviseProcessor... " + json);	
	}			
}
 
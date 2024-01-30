package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

@Component("purchaseOrderReadProcessor")
public class PurchaseOrderReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource PurchaseOrderService purchaseOrderService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start PurchaseOrderReadProcessor ..." + message);
		
		Map<String, String> map = super.convertJsonToMap(message);
		
		Long docId = Long.parseLong(map.get("docId"));
		
		PurchaseOrderVO po = purchaseOrderService.get(docId);
		
		ObjectMapper mapper = new ObjectMapper();
		String response = mapper.writeValueAsString(po);		
		
		LOG.info(ex.getExchangeId() + "End PurchaseOrderReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}	
}

package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientInvoiceReadProcessor")
public class ClientInvoiceReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource ClientInvoiceService clientInvoiceService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientInvoiceReadProcessor ..." + message);
		
		Map<String, String> map = super.convertJsonToMap(message);
		
		String externalId = String.valueOf(map.get("externalId"));
		
		ClientInvoiceVO clientInvoiceVO = clientInvoiceService.get(null, externalId);
		
		String response = super.convertToJSON(clientInvoiceVO);
		
		LOG.info(ex.getExchangeId() + "End ClientInvoiceReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}	
}

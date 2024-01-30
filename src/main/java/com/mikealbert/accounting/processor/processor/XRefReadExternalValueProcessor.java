package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@Component("xRefReadExternalValueProcessor")
public class XRefReadExternalValueProcessor extends BaseProcessor implements Processor {
	@Resource XRefService xRefService;
	
	private static final Logger LOG = LogManager.getLogger(XRefReadExternalValueProcessor.class);
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START XRefReadExternalValueProcessor... " + message);	
        
		Map<String, String> map = super.convertJsonToMap(message);
		
		String value = xRefService.getExternalValue(XRefGroupNameEnum.getXRefGroupName(map.get("groupName")), map.get("internalValue"));
				
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("value", value);
		
		String response = new ObjectMapper().writeValueAsString(valueMap);
		
        LOG.info(ex.getExchangeId() + " POST XRefReadExternalValueProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

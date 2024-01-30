package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@Component("xRefReadByGroupNameProcessor")
public class XRefReadByGroupNameProcessor extends BaseProcessor implements Processor {
	@Resource XRefService xRefService;
	
	private static final Logger LOG = LogManager.getLogger(XRefReadByGroupNameProcessor.class);
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START XRefReadByGroupNameProcessor... " + message);	
        		
		Map<String, String> map = super.convertJsonToMap(message);
		
		List<XRef> xRefs = xRefService.getByGroupName(XRefGroupNameEnum.getXRefGroupName(map.get("groupName")));
				
		String response = new ObjectMapper().writeValueAsString(xRefs);
		
        LOG.info(ex.getExchangeId() + " POST XRefReadByGroupNameProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

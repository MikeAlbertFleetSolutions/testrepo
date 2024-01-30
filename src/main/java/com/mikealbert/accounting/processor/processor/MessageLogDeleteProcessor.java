package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.service.MessageLogService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("messageLogDeleteProcessor")
public class MessageLogDeleteProcessor implements Processor {
	@Resource MessageLogService messageLogService;
		
	private final Logger LOG = LogManager.getLogger(this.getClass()); 

	@Override
	public void process(Exchange ex) throws Exception {
        LOG.info(ex.getExchangeId() + "Start MessageLogDeleteProcessor... ");
        
		String message = (String)ex.getMessage().getBody();        
        
		ObjectMapper mapper = new ObjectMapper();
		MessageLog messageLog = mapper.readValue(message, MessageLog.class);
		
		messageLogService.delete(messageLog.getMlgId());

        LOG.info(ex.getExchangeId() + "End MessageLogDeleteProcessor ... {}", message);
        
        ex.getIn().setBody(message);		
	}

}

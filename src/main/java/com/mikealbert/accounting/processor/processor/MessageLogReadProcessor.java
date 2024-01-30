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

@Component("messageLogReadProcessor")
public class MessageLogReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource MessageLogService messageLogService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start MessageLogReadProcessor ..." + message);
		
		MessageLog messageLog  = new ObjectMapper().readValue(message, MessageLog.class);
		
		messageLog = messageLogService.find(messageLog.getMlgId());

		String response = super.convertToJSON(messageLog);
		
		LOG.info(ex.getExchangeId() + "End MessageLogReadProcessor ..." + response);
		
		ex.getIn().setBody(response);		
	}	
}

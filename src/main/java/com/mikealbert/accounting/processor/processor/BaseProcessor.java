package com.mikealbert.accounting.processor.processor;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;
import com.mikealbert.constant.accounting.enumeration.QueueSelectorEnum;
import com.mikealbert.util.data.DateUtil;

public abstract class BaseProcessor {

	@Resource JmsTemplate jmsTemplate;
	@Resource MessageConverter messageConverter;
	
	@Value("${mafs.broker.queue.prefix}") 
	String qPrefix;	

	@Value("${mafs.route.retry.max}")
	int retryMax;	

	@Value("${spring.profiles.active}")
	protected String activeProfile;

	protected Map<String, String> convertJsonToMap(String json) throws Exception{		
		return new ObjectMapper().readValue(json, new TypeReference<Map<String, String>>(){});		
	}
	
	protected Map<String, Object> convertJsonToObjectMap(String json) throws Exception{		
		return new ObjectMapper().readValue(json, new TypeReference<Map<String, Object>>(){});		
	}	
	
	public void sendToQueue(QueueEnum qName, String correlationId, Object payload) {
		jmsTemplate.convertAndSend(String.format("%s.%s", qPrefix, qName.getName()), payload, message -> {
			message.setJMSCorrelationID(correlationId);
			return message;
		});
	}

	public void sendToQueue(QueueEnum qName, String correlationId, Object payload, QueueSelectorEnum action) throws Exception {		
		jmsTemplate.convertAndSend(String.format("%s.%s", qPrefix, qName.getName()), payload, message -> {
			message.setJMSCorrelationID(correlationId);
			message.setStringProperty("action", action.getName());
			return message;
		});
	}

	public String convertToJSON(Object object) throws Exception {
		return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))		
				.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
				.writeValueAsString(object);
	}
	

}

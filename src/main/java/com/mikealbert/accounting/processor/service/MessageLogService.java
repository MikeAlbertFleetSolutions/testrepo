package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

public interface MessageLogService {
	public MessageLog find(Long id);
	
	public List<MessageLog> find(EventEnum event, Date start, Date end);

	public MessageLog find(EventEnum event, String messageId);	

	public List<MessageLog> findWithPartialMessageId(EventEnum event, String partialOrFullMessageId);

	public void start(EventEnum event, String messageId);

	public void start(EventEnum event, List<String> messageIds);

	public void end(EventEnum event, String messageId) throws Exception;
	
	public void save(MessageLog messageLog);

	public void delete(Long id);
}

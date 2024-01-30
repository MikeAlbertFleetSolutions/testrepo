package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.dao.MessageLogDAO;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Service("messageLogService")
public class MessageLogServiceImpl implements MessageLogService  {
	@Resource MessageLogDAO messageLogDAO;

	@Override
	public MessageLog find(Long id) {
		return messageLogDAO.findById(id).orElse(null);
	}
	
	@Override
	public List<MessageLog> find(EventEnum event, Date start, Date end) {		
		return messageLogDAO.findByEventNameAndEndDateBetween(event.name(), start, end);
	}

	@Override
	public MessageLog find(EventEnum event, String messageId) {		
		return messageLogDAO.findByEventNameAndMessageId(event.name(), messageId).orElse(null);
	}


	@Override
	public List<MessageLog> findWithPartialMessageId(EventEnum event, String partialOrFullMessageId) {
		return messageLogDAO.findByPartialEventNameAndMessageId(event.name(), partialOrFullMessageId);
	}	

	@Transactional
	@Override
	public void start(EventEnum event, String messageId) {
		save(new MessageLog(event.name(), messageId));
	}

	@Transactional
	@Override
	public void start(EventEnum event, List<String> messageIds) {
		messageIds.stream()
		        .map(messageId -> new MessageLog(event.name(), messageId))
				.forEach(messageLog -> save(messageLog));
	}

	@Transactional
	@Override
	public void end(EventEnum event, String messageId) throws Exception {
		MessageLog messageLog = messageLogDAO
		        .findByEventNameAndMessageId(event.name(), messageId)
				.orElseThrow(() -> new Exception(String.format("Message log does not exist for event = %s, messageId = %s", event, messageId)));

		messageLog.setEndDate(new Date());		
		messageLogDAO.save(messageLog);
	}
		
	@Override
	public void save(MessageLog messageLog) {
		MessageLog existingMessageLog = messageLogDAO.findByEventNameAndMessageId(messageLog.getEventName(), messageLog.getMessageId()).orElse(null);

		if(existingMessageLog == null) {
			messageLogDAO.save(messageLog);
		}
	}

	@Override
	public void delete(Long id) {
		messageLogDAO.deleteById(id);		
	}

}

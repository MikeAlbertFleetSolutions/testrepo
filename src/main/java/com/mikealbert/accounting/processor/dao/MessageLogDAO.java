package com.mikealbert.accounting.processor.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.MessageLog;

public interface MessageLogDAO extends CrudRepository<MessageLog, Long>, MessageLogDAOCustom {
	List<MessageLog> findByEventNameAndEndDateBetween(String eventName, Date start, Date end);

	//List<MessageLog> findByEventNameAndMessageIdStartingWith(String eventName, String messageId);

	Optional<MessageLog> findByEventNameAndMessageId(String eventName, String messageId);
}



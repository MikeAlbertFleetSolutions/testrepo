package com.mikealbert.accounting.processor.dao;

import java.util.List;

import com.mikealbert.accounting.processor.entity.MessageLog;

public interface MessageLogDAOCustom {
    List<MessageLog> findByPartialEventNameAndMessageId(String eventName, String messageId);    
}

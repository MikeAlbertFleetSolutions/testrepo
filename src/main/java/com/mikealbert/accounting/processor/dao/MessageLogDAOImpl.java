package com.mikealbert.accounting.processor.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.MessageLog;

public class MessageLogDAOImpl extends GenericDAOImpl<MessageLog, Long> implements MessageLogDAOCustom{

    @SuppressWarnings("unchecked")
    @Override
    public List<MessageLog> findByPartialEventNameAndMessageId(String eventName, String messageId) {
		List<Object[]> records;
		List<MessageLog> result = new ArrayList<>();
		
		StringBuilder stmt = new StringBuilder()
            .append(" SELECT event_name, message_id, start_date, end_date ")
            .append(" FROM message_log ")
            .append(" WHERE event_name = :eventName ")
            .append("   AND message_id LIKE :messageId ");

            Query query = entityManager.createNativeQuery(stmt.toString());
            query.setParameter("eventName", eventName);
            query.setParameter("messageId", messageId.concat("%"));  
            
            records =  (List<Object[]>) query.getResultList();
		
            for(Object[] record : records) {
                int i = 0;
                
                MessageLog mlg = new MessageLog();
                mlg.setEventName((String)record[i]);
                mlg.setMessageId((String)record[i+=1]);
                mlg.setStartDate((Date)record[i+=1]);
                mlg.setEndDate((Date)record[i+=1]);
                                        
                result.add(mlg);
            }            
        
        return result;
    }
    
}

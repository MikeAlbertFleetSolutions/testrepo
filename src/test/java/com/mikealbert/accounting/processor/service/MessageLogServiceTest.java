package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class MessageLogServiceTest extends BaseTest{
	@Resource MessageLogService messageLogService;

	static final String MESSAGE_ID_1 = "accounting-period1|1Cbbbbbbbb|group-no-1";
	static final String MESSAGE_ID_2 = "accounting-period1|1Cbbbbbbbb|group-no-2";
	static final String MESSAGE_ID_3 = "accounting-period2|1Caaaaaaaa|group-no-3";

	@DisplayName("when logging the start of a message, then message log is created with a default start date")
	@Test
	public void testStartOfSingleMessage(){
		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		MessageLog messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		
		assertEquals(EventEnum.GROUP_TRANSACTION.name(), messageLog.getEventName());
		assertEquals(MESSAGE_ID_1, messageLog.getMessageId());

		assertNotNull(messageLog.getStartDate());

		assertNull(messageLog.getEndDate());
		
	}

	@DisplayName("when logging the start of mulitple messages, then message logs are created with a default start date")
	@Test
	public void testStartOfMultipleMessages(){	
		List<String> messageIds = new ArrayList<>(0);
		messageIds.add(MESSAGE_ID_1);
		messageIds.add(MESSAGE_ID_2);		
		
		messageLogService.start(EventEnum.GROUP_TRANSACTION, messageIds);

		MessageLog messageLog1 = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		MessageLog messageLog2 = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_2);		
		
		assertEquals(EventEnum.GROUP_TRANSACTION.name(), messageLog1.getEventName());
		assertEquals(EventEnum.GROUP_TRANSACTION.name(), messageLog2.getEventName());

		assertEquals(MESSAGE_ID_1, messageLog1.getMessageId());
		assertEquals(MESSAGE_ID_2, messageLog2.getMessageId());
		
		assertNotNull(messageLog1.getStartDate());
		assertNotNull(messageLog2.getStartDate());		

		assertNull(messageLog1.getEndDate());
		assertNull(messageLog2.getEndDate());		
	}	
	
	@DisplayName("when logging the end of a message, then message log corresponding message log is updated with the end date")
	@Test
	public void testEnd() throws Exception{

		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		messageLogService.end(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		MessageLog messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		
		assertEquals(EventEnum.GROUP_TRANSACTION.name(), messageLog.getEventName());
		assertEquals(MESSAGE_ID_1, messageLog.getMessageId());

		assertNotNull(messageLog.getStartDate());
		assertNotNull(messageLog.getEndDate());
		
	}

	@DisplayName("when logging the end of a message log that does not exist, then a NotFound Exception is thrown")
	@Test
	public void testEndWhenNotFound() throws Exception{
		assertThrows(Exception.class, () -> {
			messageLogService.end(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		});
	}	

	@DisplayName("when finding message log by it's id, then the matched log is returned")
	@Test
	public void testFindById() {
		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		MessageLog messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		messageLog = messageLogService.find(messageLog.getMlgId());

		assertEquals(MESSAGE_ID_1, messageLog.getMessageId());
	}
	
	
	@DisplayName("when finding message log that ended between dates, then the matched logs are returned")
	@Test
	public void testFindBetweenDates() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);

		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		messageLogService.end(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		List<MessageLog> messageLogs = messageLogService.find(EventEnum.GROUP_TRANSACTION, cal.getTime(), new Date());

		assertNotNull( 
			messageLogs.stream()
		            .filter(messageLog -> messageLog.getMessageId().equals(MESSAGE_ID_1))
				    .findFirst()
				    .orElse(null)
		);
	}


	@DisplayName("when finding message log based on event and a partial messageId, then the matched logs are returned")
	@Test
	public void testFindWithPartialMessageId() {

		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);
		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_2);	

		List<MessageLog> messageLogs = messageLogService.findWithPartialMessageId(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1.split("\\|")[0]);

		assertEquals(2, messageLogs.size());
	}
	
	@DisplayName("when updating the message log, then the start and/or end date can be updated")
	@Test
	public void testUpsert() {
		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		MessageLog originalMessageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		originalMessageLog.setStartDate(new Date());
		originalMessageLog.setEndDate(new Date());

		messageLogService.save(originalMessageLog);

		MessageLog updatedMessageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		assertEquals(originalMessageLog.getMlgId(), updatedMessageLog.getMlgId());
		assertEquals(originalMessageLog.getEventName(), updatedMessageLog.getEventName());
		assertEquals(originalMessageLog.getMessageId(), updatedMessageLog.getMessageId());
		assertEquals(originalMessageLog.getStartDate(), updatedMessageLog.getStartDate());
		assertEquals(originalMessageLog.getEndDate(), updatedMessageLog.getEndDate());
	}

	@DisplayName("when deleting a message log with a given id, then only the message log with the id is removed")
	@Test
	public void testDelete() {
		messageLogService.start(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		MessageLog messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		messageLogService.delete(messageLog.getMlgId());

		messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, MESSAGE_ID_1);

		assertNull(messageLog);
		
	}

}

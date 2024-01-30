package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@DataJpaTest
public class MessageLogDAOTest extends BaseTest{
	@Resource MessageLogDAO messageLogDAO;
	
	
	@DisplayName("when records exist, then count returns the total number of records")
	@Test
	public void testCount() {
		long rowCount = messageLogDAO.count();
		
		assertTrue(rowCount > 0);		
	}

	@DisplayName("when the record's end date is between a period of time, then the record is returned")
	@Test
	public void testFindByEndDateBetween() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);

		List<MessageLog> result = messageLogDAO.findByEventNameAndEndDateBetween(EventEnum.GROUP_TRANSACTION.name(), cal.getTime(), new Date());

		assertEquals(1, result.size());

	}
	
	@DisplayName("when searching by event name and message id, then matched record is returned")
	@Test
	public void testFindByEventNameAndMessageId() {
		MessageLog result = messageLogDAO.findByEventNameAndMessageId(EventEnum.GROUP_TRANSACTION.name(), "TEST1-MESSAGE-ID").orElse(null);

		assertNotNull(result);
	}	

	// @DisplayName("when searching by event name and message id starting with, then the matched records are returned")
	// @Test
	// public void testFindByEventNameAndMessageIdStartingWith() {
	// 	List<MessageLog> result = messageLogDAO.findByEventNameAndMessageIdStartingWith(EventEnum.GROUP_TRANSACTION.name(), "TEST1");

	// 	assertEquals(1, result.size());
	// }

	@DisplayName("when searching by event name and partial message id , then the matched records are returned")
	@Test
	public void testFindByPartialEventNameAndMessageId() {
		List<MessageLog> result = messageLogDAO.findByPartialEventNameAndMessageId(EventEnum.GROUP_TRANSACTION.name(), "TEST2");

		assertEquals(1, result.size());

		result.stream()
		    .forEach(r -> {
				assertNotNull(r.getEventName());
				assertNotNull(r.getMessageId());				
				assertNotNull(r.getStartDate());
				assertNotNull(r.getEndDate());
			});
	}	
}

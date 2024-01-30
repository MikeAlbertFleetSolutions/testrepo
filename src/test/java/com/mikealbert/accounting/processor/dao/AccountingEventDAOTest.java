package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@DataJpaTest
@DisplayName("ACCOUNTING_EVENTS")
public class AccountingEventDAOTest extends BaseTest{
	@Resource AccountingEventDAO accountingEventDAO;
	
	static final String ENTITY_ID = "-1";		
	static final String ENTITY = AccountingNounEnum.CONTRACT.name();
	static final String EVENT = EventEnum.STOP_BILLING.name();	
	static final String OP_CODE = "UNIT-TEST";		
		
	@Test
	@DisplayName("when getting count,  the correct count is returned")	
	public void testCount() {
		long rowCount = accountingEventDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}
	
	@Test
	@DisplayName("when accounting events exists, the events returned were created within the specified time period")	
	public void testFindEvents() {
		long count = accountingEventDAO.count();
		
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DATE, 1);
		Date to = cal.getTime();
		
		cal.add(Calendar.DATE, 10);
		Date future = cal.getTime();
		
		AccountingEvent newEvent = new AccountingEvent();
		newEvent.setEntity(ENTITY);
		newEvent.setEntityId(ENTITY_ID);
		newEvent.setEvent(EVENT);
		newEvent.setCreateDate(future);
		newEvent.setOpCode(OP_CODE);
		accountingEventDAO.save(newEvent);
				
		List<AccountingEvent> events = accountingEventDAO.findEvents(ENTITY, EVENT, to);
		
		assertEquals(count, events.size(), "Did not find record(s)");		
	}	
			
}

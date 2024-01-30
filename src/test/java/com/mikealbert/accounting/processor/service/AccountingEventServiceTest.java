package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.dao.AccountingEventDAO;
import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@SpringBootTest
public class AccountingEventServiceTest extends BaseTest{
	@Resource AccountingEventService accountingEventService;
	
	@MockBean  AccountingEventDAO accountingEventDAO;	
	
	@Test
	public void testGetEvents() throws Exception{
		List<AccountingEvent> mockEvents, actualEvents;
		
		Calendar cal = Calendar.getInstance();
		 
		cal.add(Calendar.DATE, 1);
		Date to = cal.getTime();
		
		mockEvents = generateAccountingEvents(); 
		when(accountingEventDAO.findEvents(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
		.thenReturn(mockEvents);
		
		actualEvents = accountingEventService.get(AccountingNounEnum.CONTRACT, EventEnum.STOP_BILLING, to);
		
		assertEquals(mockEvents, actualEvents);
	}
	
	@Test
	public void testGetAsLongIds() throws Exception{
		List<AccountingEvent> mockEvents;
		List<Long> actualIds;
		
		Calendar cal = Calendar.getInstance();
			
		cal.add(Calendar.DATE, 1);
		Date to = cal.getTime();
		
		mockEvents = generateAccountingEvents(); 
		when(accountingEventDAO.findEvents(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
		.thenReturn(mockEvents);
		
		actualIds = accountingEventService.getAsLongIds(AccountingNounEnum.CONTRACT, EventEnum.STOP_BILLING, to);
		
		assertEquals(mockEvents.get(0).getEntityId(), actualIds.get(0).toString());
	}	
	
	private List<AccountingEvent> generateAccountingEvents() {
		List<AccountingEvent> events = new ArrayList<>(0);
		
		AccountingEvent event = new AccountingEvent();
		event.setEntity(AccountingNounEnum.CONTRACT.name());
		event.setEntityId("-1");
		event.setEvent(EventEnum.STOP_BILLING.name());
		event.setCreateDate(new Date());
		event.setOpCode("UNIT-TEST");		
		
		events.add(event);
		
		return events;
	}
		
	private List<AccountingEvent> generateDriverAccountingEvents() {
		List<AccountingEvent> events = new ArrayList<>(0);
		Date now = new Date();
		
		System.out.println("first Now: " + now);
		AccountingEvent event = new AccountingEvent();
		event.setAetId(1l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-1");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");		
		events.add(event);

		event = new AccountingEvent();
		event.setAetId(2l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-2");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");
		events.add(event);
		
		event = new AccountingEvent();
		event.setAetId(3l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-1");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");
		events.add(event);
		
		now.setTime(now.getTime() + Long.valueOf("100000")); ; //resetting the date so that it is a little bit more than the previous date
		System.out.println("Second Now: " + now);
		event = new AccountingEvent();
		event.setAetId(4l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-2");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");
		events.add(event);

		event = new AccountingEvent();
		event.setAetId(5l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-1");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");
		events.add(event);


		event = new AccountingEvent();
		event.setAetId(6l);
		event.setEntity(AccountingNounEnum.DRIVER.name());
		event.setEntityId("-3");
		event.setEvent(EventEnum.UPSERT.name());
		event.setCreateDate(now);
		event.setOpCode("UNIT-TEST");
		events.add(event);

		return events;
	}

	@Test
	public void testGetDistinctEventsPerInterval() throws Exception{
		List<AccountingEvent> mockEvents;
		List<AccountingEvent> events;
				
		Calendar cal = Calendar.getInstance();
		 
		cal.add(Calendar.DATE, 1);
		Date to = cal.getTime();
		
		mockEvents = generateDriverAccountingEvents();
		AccountingEvent mockEvent = mockEvents.stream()
					.filter(e-> e.getAetId().equals(4l))
					.findFirst()
					.orElse(null);
		System.out.println("mockEvent Date: " + mockEvent.getCreateDate());

		when(accountingEventDAO.findEvents(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
		.thenReturn(mockEvents);

		events = accountingEventService.getDistinctEventsPerTimeInterval(AccountingNounEnum.DRIVER, EventEnum.UPSERT, to);
		
		assertEquals(events.size(), 3);
		
		events.forEach(event -> {
				assertEquals(mockEvent.getCreateDate(), event.getCreateDate());		
				});
	}

}

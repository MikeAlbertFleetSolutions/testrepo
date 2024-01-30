package com.mikealbert.accounting.processor.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.dao.AccountingEventDAO;
import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Service("accountingEventService")
public class AccountingEventServiceImpl extends BaseService implements AccountingEventService {
	@Resource
	AccountingEventDAO accountingEventDAO;

	final Logger LOG = LogManager.getLogger(this.getClass());

	@Value("${mafs.accounting.event.deduplicate.time.millisecond}")
	private Long timeIntervalInMilliSeconds;
	
	@Transactional
	private List<AccountingEvent> setEventProcessed(List<AccountingEvent> events) {
		events = events.stream()
				.map(e -> {
					e.setIsProcessed("Y");
					return e;
					})
				.collect(Collectors.toList());
		accountingEventDAO.saveAll(events);
		return events;		
	}

	@Transactional
	private AccountingEvent setEventProcessed(AccountingEvent event) {
		event.setIsProcessed("Y");
		accountingEventDAO.save(event);
		return event;		
	}

	@Override
	public List<AccountingEvent> get(AccountingNounEnum entity, EventEnum event, Date to) throws Exception {
		List<AccountingEvent> events = accountingEventDAO.findEvents(entity.name(), event.name(), to);
		return setEventProcessed(events);
	}

	@Override
	public List<Long> getAsLongIds(AccountingNounEnum entity, EventEnum event, Date to) throws Exception {
		return accountingEventDAO.findEvents(entity.name(), event.name(), to).stream()
				.map(e -> {
						setEventProcessed(e);
						return Long.parseLong(e.getEntityId());
				}).collect(Collectors.toList());
	}

	@Override
	public List<AccountingEvent> getDistinctEventsPerTimeInterval(AccountingNounEnum entity, EventEnum event, Date to) throws Exception {
		List<AccountingEvent> events = accountingEventDAO.findEvents(entity.name(), event.name(), to);
		events = setEventProcessed(events);
		List<AccountingEvent> distinctEvents = new ArrayList<>();

		events.sort(Comparator.comparing(AccountingEvent::getEntityId).thenComparing(AccountingEvent::getCreateDate).reversed());

		AccountingEvent prevEvent = new AccountingEvent();
		for (AccountingEvent accEvent : events) {
			if (prevEvent.getAetId() == null) { // First Time
				distinctEvents.add(accEvent);
				prevEvent = accEvent;
			} else {
				if ((accEvent.getEntityId().equals(prevEvent.getEntityId())) && (accEvent.getCreateDate().getTime()
						- prevEvent.getCreateDate().getTime() > timeIntervalInMilliSeconds)) {
					// When EntityId is same but time is more than one minute apart
					distinctEvents.add(accEvent);
					prevEvent = accEvent;
				} else if (!accEvent.getEntityId().equals(prevEvent.getEntityId())) {
					// When EntityId is not same
					distinctEvents.add(accEvent);
					prevEvent = accEvent;
				}
			}
		}
		return distinctEvents;
	}

}
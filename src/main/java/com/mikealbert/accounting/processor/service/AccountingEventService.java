package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

public interface AccountingEventService {
	List<AccountingEvent> get(AccountingNounEnum entity, EventEnum event, Date to) throws Exception;

	List<Long> getAsLongIds(AccountingNounEnum entity, EventEnum event, Date to) throws Exception;

	/*
	 * This method will return a single record for an eventId within a min.
	 */
	List<AccountingEvent> getDistinctEventsPerTimeInterval(AccountingNounEnum entity, EventEnum event, Date to) throws Exception;

}
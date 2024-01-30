package com.mikealbert.accounting.processor.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.AccountingEvent;

public interface AccountingEventDAO extends CrudRepository<AccountingEvent, Long> {

	@Query("SELECT a FROM AccountingEvent a WHERE a.entity = ?1 AND a.event = ?2 AND a.createDate <= ?3 AND a.isProcessed IS NULL ")
	List<AccountingEvent> findEvents(String entity, String event, Date to);

}
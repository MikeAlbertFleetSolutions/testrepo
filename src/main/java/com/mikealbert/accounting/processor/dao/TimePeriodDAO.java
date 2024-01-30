package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.TimePeriod;

public interface TimePeriodDAO extends CrudRepository<TimePeriod, Long>{
	
	@Query("SELECT tp FROM TimePeriod tp WHERE tp.calendarYear.id.cId = ?1 AND apStatus = ?2 ")
	public List<TimePeriod> findByCIdAndApStatus(long cId, String apStatus);

}

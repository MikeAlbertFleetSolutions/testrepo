package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.UnitTitleHistory;

public interface UnitTitleHistoryDAO extends CrudRepository<UnitTitleHistory, Long> {
	
	public List<UnitTitleHistory> findByFmsFmsId(Long fmsFmsId);
}

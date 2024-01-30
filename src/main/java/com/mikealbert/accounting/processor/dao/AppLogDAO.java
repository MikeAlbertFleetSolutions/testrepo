package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.AppLog;

public interface AppLogDAO extends CrudRepository<AppLog, Long> {
	public AppLog findTopByNameOrderByPlgIdDesc(String name);	
	public AppLog findTopByNameOrderByCreateDateDesc(String name);
}



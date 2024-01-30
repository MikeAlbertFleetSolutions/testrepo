package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.Driver;


public interface DriverDAO extends CrudRepository<Driver,Long>, DriverDAOCustom {
	
}

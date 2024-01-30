package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.DriverCostCenter;

public interface DriverCostCenterDAO extends CrudRepository<DriverCostCenter, Long>, DriverCostCenterDAOCustom {		
}

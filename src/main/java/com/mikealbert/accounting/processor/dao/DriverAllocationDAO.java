package com.mikealbert.accounting.processor.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.DriverAllocation;

/**
* DAO for DriverAllocation Entity
* @author maheshwary
*/
public interface DriverAllocationDAO extends CrudRepository<DriverAllocation, Long>{
	
	@Query("SELECT da FROM DriverAllocation da WHERE da.fleetMaster.fmsId = ?1 AND ?2 between da.allocationDate AND NVL(da.deallocationDate, ?2+1) ")
	public DriverAllocation findCurrentDriver(Long fmsId, Date onDate);

}

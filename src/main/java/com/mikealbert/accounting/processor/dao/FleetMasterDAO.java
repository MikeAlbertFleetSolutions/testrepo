package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.FleetMaster;

public interface FleetMasterDAO extends CrudRepository<FleetMaster, Long>, FleetMasterDAOCustom {
	
	@Query("Select fms FROM FleetMaster fms where fms.unitNo = ?1 ")
	public List<FleetMaster> findByUnitNo(String unitNo);

}

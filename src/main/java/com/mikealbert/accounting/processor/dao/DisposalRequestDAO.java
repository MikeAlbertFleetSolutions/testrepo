package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.DisposalRequest;

public interface DisposalRequestDAO extends CrudRepository<DisposalRequest, Long>{
	
	List<DisposalRequest> findByFmsIdAndSaleDateNotNull(Long fmsId);

}

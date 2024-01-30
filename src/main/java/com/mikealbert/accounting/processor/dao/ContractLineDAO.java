package com.mikealbert.accounting.processor.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.ContractLine;

public interface ContractLineDAO extends CrudRepository<ContractLine, Long>{
	
	@Query("Select cln FROM ContractLine cln WHERE cln.fleetMaster.fmsId = ?1 AND cln.startDate IS NOT NULL")
	public List<ContractLine> findContractLineByFmsIdAndStartDateNotNull(Long fmsId);
	
	@Query("SELECT cln FROM ContractLine cln WHERE cln.fleetMaster.fmsId = ?1 AND cln.quotationModel.quoteStatus = '6' ")
	public ContractLine getActiveContractLine(Long fmsId);
	
	@Query("SELECT COUNT(DISTINCT cln.conConId) FROM ContractLine cln WHERE cln.fleetMaster.fmsId = ?1 ")
	public Long getCountOfDistinctContractsPerFms(Long fmsId);

	public Optional<ContractLine> findByClnId(Long clnId);
	
}

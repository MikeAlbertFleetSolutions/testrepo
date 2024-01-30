package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;

public interface ExternalAccountDAO extends CrudRepository<ExternalAccount, ExternalAccountPK>, ExternalAccountDAOCustom {		
	@Query("SELECT ea " 
			+ "  FROM ExternalAccount ea " 
			+ "  INNER JOIN FETCH ea.externalAccountAddresses eaa " 
			+ "  WHERE eaa.eaaId = ?1 ")
	ExternalAccount findByExternalAccountAddress(Long eaaId);

	@Query("SELECT DISTINCT ea " 
			+ "  FROM ExternalAccount ea " 
			+ "  INNER JOIN FETCH ea.externalAccountAddresses eaa " 
			+ "  WHERE eaa.eaaId in ?1 ")
	List<ExternalAccount> findByExternalAccountAddresses(List<Long> eaaIds);
}

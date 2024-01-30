package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.ExtAccAddress;

public interface ExtAccAddressDAO extends CrudRepository<ExtAccAddress, Long>, ExtAccAddressDAOCustom {
	@Query("SELECT eaa "
			+ " FROM ExtAccAddress eaa "
			+ " WHERE eaa.externalAccount.parentAccountEntity =?1 "
			+ "   AND eaa.externalAccount.parentAccountType = ?2 "
			+ "   AND eaa.externalAccount.parentAccount = ?3 ")
	List<ExtAccAddress> findByRelatedAccountId(long cId, String accountType, String accountCode);
	
	@Query("SELECT eaa "
			+ " FROM ExtAccAddress eaa "
			+ " WHERE eaa.externalAccount.id.cId =?1 "
			+ "   AND eaa.externalAccount.id.accountType = ?2 "
			+ "   AND eaa.externalAccount.id.accountCode = ?3 ")
	ExtAccAddress findByAccount(long cId, String accountType, String accountCode); 	
}

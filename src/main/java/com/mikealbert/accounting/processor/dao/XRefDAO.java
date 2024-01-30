package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.XRef;

public interface XRefDAO extends CrudRepository<XRef, String> {
	@Query("SELECT x FROM XRef x WHERE x.xRefPK.groupName = ?1 AND UPPER(x.xRefPK.internalValue) = UPPER(?2)")
	List<XRef> findByGroupNameAndInternalValue(String groupName, String internalValue);
	
	@Query("SELECT x FROM XRef x WHERE x.xRefPK.groupName = ?1 AND UPPER(x.xRefPK.externalValue) = UPPER(?2)")
	List<XRef> findByGroupNameAndExternalValue(String groupName, String externalValue);
	
	@Query("SELECT x FROM XRef x WHERE x.xRefPK.groupName = ?1 ")
	List<XRef> findByGroupName(String groupName);	
}

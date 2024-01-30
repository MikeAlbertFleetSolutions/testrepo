package com.mikealbert.accounting.processor.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.AssetItem;


public interface AssetItemDAO extends CrudRepository<AssetItem, Long>, AssetItemDAOCustom {
	
	@Query("Select ai FROM AssetItem ai Where ai.fleetId = ?1 and addOnSeq = '000' ")
	public AssetItem getParentAsset(Long fleetId);
}

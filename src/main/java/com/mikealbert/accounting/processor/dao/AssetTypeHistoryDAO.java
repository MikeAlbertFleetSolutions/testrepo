package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.AssetTypeHistory;

public interface AssetTypeHistoryDAO extends CrudRepository<AssetTypeHistory, Long> {

}

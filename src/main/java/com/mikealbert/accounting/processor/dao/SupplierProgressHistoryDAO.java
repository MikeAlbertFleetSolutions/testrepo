package com.mikealbert.accounting.processor.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.accounting.processor.entity.SupplierProgressHistory;

/**
* DAO for SupplierProgressHistory Entity
* @author Sibley
*/

public interface SupplierProgressHistoryDAO extends JpaRepository<SupplierProgressHistory, Long> , SupplierProgressHistoryDAOCustom {}

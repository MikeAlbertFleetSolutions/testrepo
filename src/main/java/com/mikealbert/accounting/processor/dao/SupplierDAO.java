package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.Supplier;

public interface SupplierDAO extends CrudRepository<Supplier, Long> {}



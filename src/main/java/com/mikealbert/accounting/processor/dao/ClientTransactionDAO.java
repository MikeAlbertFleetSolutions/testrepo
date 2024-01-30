package com.mikealbert.accounting.processor.dao;

import com.mikealbert.accounting.processor.entity.Doc;

import org.springframework.data.repository.CrudRepository;


public interface ClientTransactionDAO extends CrudRepository<Doc, Long>, ClientTransactionDAOCustom {
}

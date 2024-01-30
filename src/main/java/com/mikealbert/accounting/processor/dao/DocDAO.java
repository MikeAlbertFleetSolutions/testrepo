package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.Doc;


public interface DocDAO extends CrudRepository<Doc, Long>, DocDAOCustom {
}

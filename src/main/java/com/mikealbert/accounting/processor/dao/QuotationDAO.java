package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.Quotation;


public interface QuotationDAO extends CrudRepository<Quotation, Long>, QuotationDAOCustom {
}

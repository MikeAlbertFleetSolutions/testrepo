package com.mikealbert.accounting.processor.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.QuotationModel;


public interface QuotationModelDAO extends CrudRepository<QuotationModel, Long>, QuotationModelDAOCustom {
	Optional<QuotationModel> findByQmdId(Long qmdId);
}

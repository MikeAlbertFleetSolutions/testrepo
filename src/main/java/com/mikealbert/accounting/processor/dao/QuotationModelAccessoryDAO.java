package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.QuotationModelAccessory;

public interface QuotationModelAccessoryDAO extends CrudRepository<QuotationModelAccessory, Long>{

	@Query("Select qma from QuotationModelAccessory qma where qma.quotationModel.qmdId = ?1 ")
	List<QuotationModelAccessory> findByQmdId(Long qmdId);

}

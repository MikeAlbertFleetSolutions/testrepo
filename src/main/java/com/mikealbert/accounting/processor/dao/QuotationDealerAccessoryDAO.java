package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.QuotationDealerAccessory;

public interface QuotationDealerAccessoryDAO extends CrudRepository<QuotationDealerAccessory, Long>{
	
	@Query("Select qda from QuotationDealerAccessory qda where qda.quotationModel.qmdId = ?1 ")
	List<QuotationDealerAccessory> findByQmdId(Long qmdId);

}

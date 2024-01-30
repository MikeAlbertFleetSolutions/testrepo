package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.QuotationStepStructure;
import com.mikealbert.accounting.processor.entity.QuotationStepStructurePK;

public interface QuotationStepStructureDAO extends CrudRepository<QuotationStepStructure, QuotationStepStructurePK> {

    @Query("select qss from QuotationStepStructure qss where qss.id.qmdQmdId = ?1 ")
	public List<QuotationStepStructure> findAllByQmdId(BigDecimal qmdId);

}

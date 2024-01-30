package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.QuotationStepStructure;

@DataJpaTest
public class QuotationStepStructureDAOTest extends BaseTest{

	private List<QuotationStepStructure> quotationStepStructure = new ArrayList<>();
	@Resource QuotationStepStructureDAO quotationStepStructureDAO;
		
	@Test
	public void testFindAllByQmdId() {
		quotationStepStructure.addAll(quotationStepStructureDAO.findAllByQmdId(BigDecimal.valueOf(12345)));
		assertTrue(quotationStepStructure.size() > 1, "Not able to pull correct step Structure");
	}
	
}

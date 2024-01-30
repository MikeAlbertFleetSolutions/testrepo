package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;

@DataJpaTest
@DisplayName("ContractLinesDAO test class")
public class ContractLinesDAOTest extends BaseTest{
	@Resource ContractLineDAO contractLineDAO;
	

	@Test
	@DisplayName("Test to verify count of distinct Contracts per fmsId")
	public void testGetCountOfDistinctContractsPerFms() {
		Long fmsId = Long.valueOf(1273618);
		Long cnt = contractLineDAO.getCountOfDistinctContractsPerFms(fmsId);		
		//One contract set for this fmsId in amendLease.sql
		assertEquals(1, cnt);
	}
	
}

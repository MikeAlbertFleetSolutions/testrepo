package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;

@DataJpaTest
public class SupplierDAOTest extends BaseTest{
	@Resource SupplierDAO supplierDAO;
	
	static final String POLLING_JOB_NAME = "TEST-JOB";
		
	@Test
	public void testCount() {
		long rowCount = supplierDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}
		
}

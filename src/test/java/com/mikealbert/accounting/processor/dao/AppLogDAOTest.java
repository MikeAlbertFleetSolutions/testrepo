package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.AppLog;

@DataJpaTest
public class AppLogDAOTest extends BaseTest{
	@Resource AppLogDAO appLogDAO;
	
	static final String POLLING_JOB_NAME = "TEST-JOB";
		
	@Test
	public void testCount() {
		long rowCount = appLogDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}
	
	@Test
	public void findByNameOrderByCreateDate() {
		AppLog log = appLogDAO.findTopByNameOrderByCreateDateDesc(POLLING_JOB_NAME);
		assertNotNull(log);
	}
	
	@Test
	public void findByNameOrderByPlgId() {
		AppLog log = appLogDAO.findTopByNameOrderByPlgIdDesc(POLLING_JOB_NAME);
		assertNotNull(log);
	}	
		
}

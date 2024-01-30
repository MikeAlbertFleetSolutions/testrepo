package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.Doc;

@DataJpaTest
public class DocDAOTest extends BaseTest{
	@Resource DocDAO docDAO;
	
	@Test
	void testGetInvoiceArIdFromInvoiceApId() {
		Long id = docDAO.getInvoiceArIdFromInvoiceApId(2L);
		assertNotNull(id);
	}
	
	@Test
	void testGetMaintenanceInvoiceIds() {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR, -1);

		Calendar end = Calendar.getInstance();
		
		List<Long> ids = docDAO.getMaintenanceInvoiceIds(start.getTime(), end.getTime());
		
		assertFalse(ids.isEmpty());
	}
	
	@Test
	void testGetMaintenanceCreditIds() {
		Calendar start = Calendar.getInstance();
		start.add(Calendar.HOUR, -1);

		Calendar end = Calendar.getInstance();
		
		List<Long> ids = docDAO.getMaintenanceCreditIds(start.getTime(), end.getTime());
		
		assertFalse(ids.isEmpty());
	}	
	
	@Test
	void testSave() {
		Doc doc = docDAO.findById(2L).orElse(null);
		doc.setGlAcc(1L);
		doc = docDAO.save(doc);
		
		assertEquals(1L, doc.getGlAcc());
	}
	
	@Test
	void testHasDistRecord() {
		assertTrue(docDAO.hasDistRecord(1L), "Dist record was not found");
	}
	
}

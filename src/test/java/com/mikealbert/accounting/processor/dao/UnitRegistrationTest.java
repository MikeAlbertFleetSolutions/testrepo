package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.UnitRegistration;

@DataJpaTest
public class UnitRegistrationTest extends BaseTest{
	@Resource UnitRegistrationDAO unitRegistrationDAO;
		    
	@Test
	public void testFindByFmsFmsId() {

		List<UnitRegistration> unitRegistrations = unitRegistrationDAO.findByFmsFmsId(1L);
		
		assertTrue(unitRegistrations.size() > 0, "Unit Registrations were not found");
	}
	
			
}

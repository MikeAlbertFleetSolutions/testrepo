package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;

@DataJpaTest
public class DriverDAOTest extends BaseTest{
	@Resource DriverDAO driverDAO;
		    
	@Test
	public void testGetCurrentGaragedState() {
		final String EXPECTED_STATE = "OH";

		String state = driverDAO.getCurrentGaragedState(1L);
		
		assertEquals(EXPECTED_STATE, state);
	}
	
			
}

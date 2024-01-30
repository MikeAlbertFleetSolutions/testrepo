package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;

@DataJpaTest
public class ExtAccAddressDAOTest extends BaseTest{
	@Resource ExtAccAddressDAO extAccAddressDAO;
		
	static final long C_ID = 1l;
	static final String ACCOUNT_TYPE = "S";
	static final String ACCOUNT_CODE = "00000000";
	static final String CHILD_ACCOUNT_CODE = "C0000000";	
	
	@Test
	public void testCount() {
		long rowCount = extAccAddressDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}
	
	@Test
	public void testFindByRelatedAccountId() {
		List<ExtAccAddress> result = extAccAddressDAO.findByRelatedAccountId(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE);
		assertEquals(1, result.size());
	}
	
	@Test
	public void testFindByAccount() {
		ExtAccAddress result = extAccAddressDAO.findByAccount(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE);
		assertEquals(ACCOUNT_CODE, result.getExternalAccount().getId().getAccountCode());
	}
	
	@Test
	public void testIsLinkedToVehicleMovement() {
		boolean unLinked = !extAccAddressDAO.isLinkedToVehicleMovement(0L);
		boolean linked = extAccAddressDAO.isLinkedToVehicleMovement(1L);

		assertTrue(unLinked);		
		assertTrue(linked);
	}
}

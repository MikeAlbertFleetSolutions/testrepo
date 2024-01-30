package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.ClientBillingTransaction;

@DataJpaTest
public class ClientBillingTransactionDAOTest extends BaseTest{
	@Resource ClientBillingTransactionDAO clientBillingTransactionDAO;
	
	static final String POLLING_JOB_NAME = "TEST-JOB";
		
	@Test
	public void testCount() {
		long rowCount = clientBillingTransactionDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}

	@Test
	public void testFindByAccountCodeAndAccountingPeriod() {
		List<ClientBillingTransaction> txns = clientBillingTransactionDAO.findByAccountCodeAndAccountingPeriod("00000000", "JAN-2022");
		
		assertTrue(txns.size() > 0, "Did not find record(s)");		
	}
			
	@Test
	public void testFindByTranIntIdAndLineNo() {
		ClientBillingTransaction txn = clientBillingTransactionDAO.findByTranIntIdAndLineNo("0", 1L).orElse(null);

		assertNotNull(txn);
	}
			
}

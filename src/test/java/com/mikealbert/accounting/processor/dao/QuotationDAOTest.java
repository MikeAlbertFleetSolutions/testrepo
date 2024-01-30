package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.AmendmentLeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.util.data.DateUtil;

@DataJpaTest
public class QuotationDAOTest extends BaseTest{
	@Resource QuotationDAO quotationDAO;
	
	private List<AmendmentLeaseAccountingScheduleVO> expectedAmendLeaseAcctScheduleVOs;
		
	@BeforeEach
	public void init() throws Exception {
		this.expectedAmendLeaseAcctScheduleVOs = new ArrayList<AmendmentLeaseAccountingScheduleVO>();
		AmendmentLeaseAccountingScheduleVO amendmentLeaseAcctScheduleVO = new AmendmentLeaseAccountingScheduleVO();
		amendmentLeaseAcctScheduleVO.setQelId(new BigDecimal("4084759"));
		amendmentLeaseAcctScheduleVO.setResidual(new BigDecimal("72.50"));
		amendmentLeaseAcctScheduleVO.setRechargeAmount(new BigDecimal("145.00"));
		amendmentLeaseAcctScheduleVO.setRechargeInd("N");
		amendmentLeaseAcctScheduleVO.setMonthlyLeaseAmount(new BigDecimal("6.57"));
		amendmentLeaseAcctScheduleVO.setTransDate(DateUtil.convertToDate("2019-12-02T00:00:00"));
		this.expectedAmendLeaseAcctScheduleVOs.add(amendmentLeaseAcctScheduleVO);
	}

	@Test
	public void testFindByEventTypeIds() {
		String qmdId = "1";
		LeaseVO leaseVO = quotationDAO.findLease(qmdId);
		
		assertNotNull(leaseVO.getClientExternalId());
		assertNotNull(leaseVO.getInterestRate());
		assertNotNull(leaseVO.getDepreciationFactor());
	}

	@Disabled //TODO H2 DB 2.0 is not supporting something in the query
	@Test
	public void testFindLeaseAccountingSchedule() {
		String qmdId = "733916";
		LeaseVO leaseVO = quotationDAO.findLease(qmdId);
		
		leaseVO = quotationDAO.findLeaseAccountingSchedule(leaseVO, qmdId);
		
		if (!leaseVO.getLeaseAccountingSchedule().isEmpty())
			assertTrue(true);		
	}

	@Test
	public void testFindParentExternalId() {
		String qmdId = "733916";
		String expectedParentExternalId = "517386-2";
		String actualParentExternalId = quotationDAO.findParentExternalIdByQmdId(qmdId);
		assertEquals(expectedParentExternalId, actualParentExternalId);
	}

	@Test
	public void testFindPreviousQmdId() {
		String qmdId = "733916";
		String expectedPrevQmdId = "715773";
		String actualPrevQmdId = quotationDAO.findPreviousQmdId(qmdId);
		assertEquals(expectedPrevQmdId, actualPrevQmdId);
	}
	
	@Test
	public void testGetNonRechargeAmendLeaseAccountingSchedule() {
		String qmdId = "733916";
		String prevQmdId = "715773";	
		List<AmendmentLeaseAccountingScheduleVO> actualAmendLeaseAcctScheduleVOs = 
				quotationDAO.getNonRechargeAmendLeaseAccountingSchedule(qmdId, prevQmdId);
		
		assertArrayEquals(this.expectedAmendLeaseAcctScheduleVOs.toArray(), actualAmendLeaseAcctScheduleVOs.toArray());
	}
	
	@Test
	public void testIsUnitOnContractFms() {
		Long fmsId = 12345l;
		Boolean result = quotationDAO.isUnitOnContract(fmsId);
		assertFalse(result);
	}
	
	@Test
	public void testIsUnitOnContractUnitNo() {
		String unitNo = "00000000";
		Boolean result = quotationDAO.isUnitOnContract(unitNo);
		assertFalse(result);
	}
	
	@Test
	public void testGetProductCodeByQuoId() {
		String productCode = quotationDAO.getProductCodeByQuoId(1L);
		assertNotNull(productCode);
	}
	
}

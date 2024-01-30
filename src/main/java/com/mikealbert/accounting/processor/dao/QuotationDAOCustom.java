package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.accounting.processor.vo.AmendmentLeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;

public interface QuotationDAOCustom {
	public LeaseVO findLease(String qmdId);
	public LeaseVO findLeaseAccountingSchedule(LeaseVO leaseVO, String qmdId);
	
	public String findParentExternalIdByQmdId(String qmdId);

	public String findParentExternalIdByClnId(Long clnId);	
	
	public String findPreviousQmdId(String qmdId);
	
	public List<AmendmentLeaseAccountingScheduleVO> getNonRechargeAmendLeaseAccountingSchedule(String qmdId, String previousQmdId);
	public List<AmendmentLeaseAccountingScheduleVO> getRechargeAmendLeaseAccountingSchedule(String qmdId, String previousQmdId);
	public List<LeaseAccountingScheduleVO> getRemainingStepSchedule(String qmdId, List<BigDecimal> qels, BigDecimal totalMonthlyLeaseAmount);
	
	public Boolean isPriorAmendmentExist(String qmdId, Long quoId);
	public BigDecimal findLeaseAssetFairValueRevision(Long fmsId, String qmdId, String productType);
	//public BigDecimal findLeaseAssetFairValueAmendment(Long fmsId, String qmdId, String previousQmdId, String productType);

	public BigDecimal getCustomerCapCost(Long qmdId);
	public BigDecimal getCustomerCapCostAmendment(Long qmdId, Long previousQmdId);	
	
	//public BigDecimal findLeaseAssetFairValueOriginal(Long fmsId, String qmdId, String productType);
	
	public Boolean isUnitOnContract(Long fmsId);
	public Boolean isUnitOnContract(String unitNo);
	
	public BigDecimal getResidualByQmdId(Long qmdId);
	
	public List<Long> findQuoIdsByClnIds(List<Long> clnIds);
	
	public String getProductCodeByQuoId(Long quoId);

}

package com.mikealbert.accounting.processor.dao;

import java.util.Date;

import com.mikealbert.accounting.processor.vo.CostCenterVO;

public interface DriverCostCenterDAOCustom {
	public CostCenterVO getActiveCostCenter(Long cId, String accountType, String accountCode, Long drvId, Date date);
}

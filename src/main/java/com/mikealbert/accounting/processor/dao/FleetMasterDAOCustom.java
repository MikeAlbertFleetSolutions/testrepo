package com.mikealbert.accounting.processor.dao;

import com.mikealbert.accounting.processor.entity.Product;
import com.mikealbert.accounting.processor.vo.UnitVO;

public interface FleetMasterDAOCustom {
	public Product findProductByUnitNoForLatestContract(String unitNo);
	public String getFleetStatus(String unitNo);
	public Boolean isVehicleOffLease(Long fmsId);
	public Boolean isVehicleDisposed(Long fmsId);
	public UnitVO getUnitInfo(UnitVO unitVO);
}

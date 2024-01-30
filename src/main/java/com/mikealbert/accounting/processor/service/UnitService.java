package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.vo.UnitVO;

public interface UnitService {
		
	FleetMaster getFleetMasterByUnit(String unitNo) throws Exception;
	String getFleetStatusCode(String unitNo) throws Exception;	
	Boolean isVehicleOffContract(String unitNo) throws Exception;
	Boolean isVehicleDisposedOff(String unitNo) throws Exception;
	List<Map<String, Object>> getExternalUnits();
	List<Map<String, Object>> getExternalUnits(String externalId);
	UnitVO getUnitInfo(UnitVO unit) throws Exception;
	String upsertUnit(UnitVO unit) throws Exception;
}

package com.mikealbert.accounting.processor.dao;

import java.util.List;

import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;

public interface DriverDAOCustom {
	public  List<DriverUnitHistoryUpsertVO> getDriverUnitHistoryDetail(DriverUnitHistoryUpsertVO duh) throws Exception;
	public String getCurrentGaragedState(Long fmsId);
}
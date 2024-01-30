package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

public interface DriverService {

	List<DriverUnitHistoryUpsertVO> getDriverUnitHistoryUpsertRequest(DriverUnitHistoryUpsertVO driverUnitHistory) throws Exception;

	String driverUnitHistoryUpsert(List<DriverUnitHistoryUpsertVO> drvUnitHistoryVOs) throws Exception;

	List<DriverUnitHistoryUpsertVO> initializeDriverUnitHistoryRecords(List<AccountingEvent> accEvents);

	DriverUnitHistoryVO readDuhByUnitInternalIdAndDate(String unitInternalId, Date effectiveDate) throws Exception;

	List<DriverUnitHistoryVO> findAllDuhs() throws Exception;
}
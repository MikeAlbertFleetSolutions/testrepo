package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

public interface DriverSuiteTalkService  {
	
	public void upsertDriverUnitHistory(DriverUnitHistoryUpsertVO drvUnitHistoryVO) throws Exception;
	
	public void deleteDriverUnitHistory(DriverUnitHistoryUpsertVO drvUnitHistoryVO) throws Exception;

	public DriverUnitHistoryVO readDuhByUnitInternalIdAndDate(String unitInternalId, Date effectiveDate) throws Exception;

	public List<DriverUnitHistoryVO> readAllDuhs() throws Exception;	
}

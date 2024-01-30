package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suitetalk.DriverSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DriverDAO;
import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Service("driverService")
public class DriverServiceImpl extends BaseService implements DriverService {

	@Resource DriverDAO driverDAO;
	@Resource DriverSuiteTalkService driverSuiteTalkService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	public List<DriverUnitHistoryUpsertVO> getDriverUnitHistoryUpsertRequest(DriverUnitHistoryUpsertVO driverUnitHistory) throws Exception {
		return driverDAO.getDriverUnitHistoryDetail(driverUnitHistory);
	}

	public String driverUnitHistoryUpsert(List<DriverUnitHistoryUpsertVO> drvUnitHistoryVOs) throws Exception {
		
		for (DriverUnitHistoryUpsertVO drvUnitHistVO : drvUnitHistoryVOs) {
			super.validate(drvUnitHistVO);
		}

		for (DriverUnitHistoryUpsertVO drvUnitHistVO : drvUnitHistoryVOs) {
			LOG.info(String.format("DriverServiceImpl.driverUnitHistoryUpsert for DUH: %s", drvUnitHistoryVOs.toString()));
			driverSuiteTalkService.upsertDriverUnitHistory(drvUnitHistVO);
		}
		
		return "Processed Driver Unit History records";
	}

	public List<DriverUnitHistoryUpsertVO> initializeDriverUnitHistoryRecords(List<AccountingEvent> accEvents) {
		
		List<DriverUnitHistoryUpsertVO> drvUnitHistoryVOs = accEvents.parallelStream().map(accEvent -> {
			return new DriverUnitHistoryUpsertVO(accEvent.getAetId(), AccountingNounEnum.valueOf(accEvent.getEntity()), EventEnum.valueOf(accEvent.getEvent()), 
					accEvent.getEntityId(), accEvent.getCreateDate());
		}).collect(Collectors.toList());

		return drvUnitHistoryVOs;
	}

	@Override
	public DriverUnitHistoryVO readDuhByUnitInternalIdAndDate(String unitInternalId, Date effectiveDate) throws Exception {
		return unitInternalId == null || effectiveDate == null ? null : driverSuiteTalkService.readDuhByUnitInternalIdAndDate(unitInternalId, effectiveDate);
	}

	@Override
	public List<DriverUnitHistoryVO> findAllDuhs() throws Exception {
		return driverSuiteTalkService.readAllDuhs();
	}

}
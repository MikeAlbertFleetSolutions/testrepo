package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.accounting.processor.service.AccountingEventService;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.DriverService;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("driverUnitHistoryEventPollingProcessor")
public class DriverUnitHistoryEventPollingProcessor implements Processor {
	@Resource
	AppLogService appLogService;
	@Resource
	AccountingEventService accountingEventService;
	@Resource
	DriverService driverService;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Handler
	public void process(Exchange ex) throws Exception {
		Date end;

		LOG.info(ex.getExchangeId() + "PRE DriverUnitHistory EventPollingProcessor... ");

		end = appLogService.getEndDate();

		List<AccountingEvent> events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.DRIVER, EventEnum.UPSERT, end);
		List<DriverUnitHistoryUpsertVO> drvUnitHistoryVOs = driverService.initializeDriverUnitHistoryRecords(events);

		events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.DRIVER_ALLOCATION, EventEnum.UPSERT, end);
		drvUnitHistoryVOs.addAll(driverService.initializeDriverUnitHistoryRecords(events));

		events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.QUOTE, EventEnum.ACCEPT, end);
		drvUnitHistoryVOs.addAll(driverService.initializeDriverUnitHistoryRecords(events));

		events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.QUOTE, EventEnum.DRIVER_CHANGE, end);
		drvUnitHistoryVOs.addAll(driverService.initializeDriverUnitHistoryRecords(events));

		events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.DOC, EventEnum.RELEASE_MAIN_PO, end);
		drvUnitHistoryVOs.addAll(driverService.initializeDriverUnitHistoryRecords(events));
		
		events = accountingEventService
				.getDistinctEventsPerTimeInterval(AccountingNounEnum.UNIT, EventEnum.DUH_INSERT, end);
		drvUnitHistoryVOs.addAll(driverService.initializeDriverUnitHistoryRecords(events));

		ex.getIn().setHeader("endDate", end);
		ex.getIn().setBody(drvUnitHistoryVOs);

		LOG.info(ex.getExchangeId() + " POST DriverUnitHistoryEventPollingProcessor count ... "
				+ drvUnitHistoryVOs.size());
	}
}
package com.mikealbert.accounting.processor.processor;

import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("driverUnitHistoryEventCorrelationIdProcessor")
public class DriverUnitHistoryEventCorrelationIdProcessor {

	private final Logger LOG = LogManager.getLogger(this.getClass());

	public void process(Exchange ex) throws Exception {
		LOG.info(ex.getExchangeId() + " DriverUnitHistoryEventCorrelationIdProcessor is started ...");

		DriverUnitHistoryUpsertVO driverUnitHistory = (DriverUnitHistoryUpsertVO) ex.getIn().getBody();

		String correlationId = null;

		switch(driverUnitHistory.getNoun()) {
		case DRIVER:
			correlationId = String.format("%d", driverUnitHistory.getDrvId());
			break;
		case DRIVER_ALLOCATION:
			correlationId = String.format("%d", driverUnitHistory.getDalId());
			break;
		case UNIT:
			correlationId = String.format("%d", driverUnitHistory.getFmsId());
			break;
		case QUOTE:
			if(EventEnum.ACCEPT.equals(driverUnitHistory.getEvent()))
				correlationId = String.format("%d", driverUnitHistory.getQmdId());
			else if(EventEnum.DRIVER_CHANGE.equals(driverUnitHistory.getEvent()))
				correlationId = String.format("%d", driverUnitHistory.getQuoId());
			break;
		case DOC:
			correlationId = String.format("%d", driverUnitHistory.getDocId());
			break;
		default:
			//Don't expect to drop here because we have controlled which nouns we are processing
			throw new Exception(String.format("Error in DriverDAOImpl.getDriverUnitHistoryDetail:: Unexpected Noun found: %s", driverUnitHistory.toString()));
		}

		ex.getIn().setHeader("JMSCorrelationID", correlationId);

		ex.getIn().setBody(driverUnitHistory);

		LOG.info(ex.getExchangeId() + " DriverUnitHistoryEventCorrelationIdProcessor completed id=" + correlationId);
	}
}
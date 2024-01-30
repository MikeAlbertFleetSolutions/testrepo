package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("driverUnitHistorySingleReadProcessor")
public class DriverUnitHistorySingleReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Resource ServiceCache serviceCache;
	
	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start driverUnitHistorySingleReadProcessor ..." + message);
		
		Map<String, Object> map = super.convertJsonToObjectMap(message);
		
		String unitInternalId = String.valueOf(map.get("unitInternalId"));
		Date effectiveDate = new Date((Long)map.get("effectiveDate"));

		DriverUnitHistoryVO driverUnitHistoryVO =  serviceCache.findDuhByUnitInternalIdAndDate(unitInternalId, effectiveDate);

		String response = super.convertToJSON(driverUnitHistoryVO);

		ex.getIn().setBody(response);

		LOG.info(ex.getExchangeId() + "End driverUnitHistorySingleReadProcessor ...");		
				
	}	
}

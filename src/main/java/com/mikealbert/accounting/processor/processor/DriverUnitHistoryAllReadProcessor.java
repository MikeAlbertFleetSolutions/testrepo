package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("driverUnitHistoryAllReadProcessor")
public class DriverUnitHistoryAllReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Resource ServiceCache serviceCache;
	
	@Override
	public void process(Exchange ex) throws Exception {
		LOG.info(ex.getExchangeId() + "Start DriverUnitHistoryAllReadProcessor ...");

		List<DriverUnitHistoryVO> duhs = serviceCache.finalAllDuhs();

		String response = super.convertToJSON(duhs);

		ex.getIn().setBody(response);

		LOG.info(ex.getExchangeId() + "End DriverUnitHistoryAllReadProcessor ...");		
				
	}	
}

package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.DriverService;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("driverUnitHistoryProcessor")
public class DriverUnitHistoryProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(DriverUnitHistoryProcessor.class);
	
	@Resource DriverService driverService;
			
	@SuppressWarnings("unchecked")
	@Override
    public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start DriverUnitHistoryProcessor... ");
        
        List<DriverUnitHistoryUpsertVO> drvUpsertVOs = (List<DriverUnitHistoryUpsertVO>)ex.getMessage().getBody();
                
        String response = driverService.driverUnitHistoryUpsert(drvUpsertVOs);
		
        LOG.info(ex.getExchangeId() + " POST DriverUnitHistoryProcessor... " + response);
        
		ex.getIn().setBody(drvUpsertVOs);
	}
}

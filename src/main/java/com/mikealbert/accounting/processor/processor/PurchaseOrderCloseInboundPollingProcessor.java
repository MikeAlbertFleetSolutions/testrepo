package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.PurchaseOrderService;

@Component("purchaseOrderCloseInboundPollingProcessor")
public class PurchaseOrderCloseInboundPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource PurchaseOrderService purchaseOrderService;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<Map<String, Object>> pos;
		
        LOG.info(ex.getExchangeId() + "PRE PurchaseOrderCloseInboundPollingProcessor... ");

        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
               
    	pos = purchaseOrderService.findClosedPOs(start, end);  
    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(pos);

        LOG.info(ex.getExchangeId() + " POST PurchaseOrderCloseInboundPollingProcessor count ... " + pos.size());    	
    	
	}
}

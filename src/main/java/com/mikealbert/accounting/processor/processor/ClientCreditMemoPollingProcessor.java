package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.ClientCreditMemoService;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientCreditMemoPollingProcessor")
public class ClientCreditMemoPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource ClientCreditMemoService clientCreditMemoService;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<Map<String, Object>> creditMemos;
		
        LOG.info(ex.getExchangeId() + "PRE ClientCreditMemoPollingProcessor... ");

        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
               
    	creditMemos = clientCreditMemoService.findUpdatedUngroupedCreditMemos(start, end);  
    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(creditMemos);

        LOG.info(ex.getExchangeId() + " POST ClientCreditMemoPollingProcessor count ... " + creditMemos.size());    	
    	
	}
}

package com.mikealbert.accounting.processor.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.service.MessageLogService;

@Component("clientTransactionGroupCompletePollingProcessor")
public class ClientTransactionGroupCompletePollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource MessageLogService messageLogService;
	@Resource ClientTransactionGroupService clientTransactionGroupService;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		List<Map<String, Object>> messages = new ArrayList<>(0);

        LOG.info(ex.getExchangeId() + "PRE ClientTransactionGroupCompletePollingProcessor... ");

        Date start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        Date end = appLogService.getEndDate();
             
		try {
    	    boolean isInternalTransactionGroupingComplete = clientTransactionGroupService.hasInternalGroupingBeenCompleted(start, end);  
    
		    if(isInternalTransactionGroupingComplete) {
		    	List<String> completedAccountingPeriods = clientTransactionGroupService.completedInternalGroupAccountingPeriods(start, end);
    
		    	Map<String, Object> message = new HashMap<>();
		    	message.put("transactionGroupingCompleted", isInternalTransactionGroupingComplete);
		    	message.put("accountingPeriods", completedAccountingPeriods);
		    	messages.add(message);			
		    }
		} catch(Exception e) {
			LOG.error(e);
		} finally {    			 	
    	    ex.getIn().setHeader("endDate", end); 
            ex.getIn().setBody(messages);
	    }    	    	
		
        LOG.info(ex.getExchangeId() + " POST ClientTransactionGroupCompletePollingProcessor result ... " + messages);    	
    	
	}
}

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

import com.mikealbert.accounting.processor.service.AccountingEventService;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.LeaseTerminationRequestVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("stopBillingEventPollingProcessor")
public class StopBillingEventPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource AccountingEventService accountingEventService;
	@Resource LeaseService leaseService;

	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date end; 
		List<Long> contractLineIds;
		List<LeaseTerminationRequestVO> leaseTerminiationRequests;
		
        LOG.info(ex.getExchangeId() + "PRE StopBillingEventPollingProcessor... ");
                    
        end = appLogService.getEndDate();
            	     
        contractLineIds = accountingEventService.getAsLongIds(AccountingNounEnum.CONTRACT, EventEnum.STOP_BILLING, end);       
        
        leaseTerminiationRequests = leaseService.initializeLeaseTerminationRequests(contractLineIds);       
        
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(leaseTerminiationRequests);
    	    	    	
        LOG.info(ex.getExchangeId() + " POST StopBillingEventPollingProcessor count ... " + leaseTerminiationRequests.size());    	    	
	}
}

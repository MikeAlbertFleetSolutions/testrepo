package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import com.mikealbert.accounting.processor.vo.AccountingEventMessageVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("leaseInterestUpdatePollingProcessor")
public class LeaseInterestUpdatePollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource AccountingEventService accountingEventService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date end; 
		List<AccountingEvent> events;
		
        LOG.info(ex.getExchangeId() + "PRE LeaseInterestUpdatePollingProcessor... ");
        
        end = appLogService.getEndDate();
	     
        events = accountingEventService.get(AccountingNounEnum.QUOTE, EventEnum.INTEREST_UPDATE, end);  
        
        List<AccountingEventMessageVO> messages = events.stream()
		        .map(e -> new AccountingEventMessageVO()
				                  .setEntityId(e.getEntityId())
								  .setEntity(AccountingNounEnum.valueOf(e.getEntity()))
								  .setEvent(EventEnum.valueOf(e.getEvent())))
    			.collect(Collectors.toList()); 
    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(messages);

        LOG.info(ex.getExchangeId() + " POST LeaseInterestUpdatePollingProcessor count ... " + messages.size());    	
    	
	}
}

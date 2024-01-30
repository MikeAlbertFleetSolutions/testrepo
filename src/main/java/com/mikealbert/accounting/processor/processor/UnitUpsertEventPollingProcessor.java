package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.AccountingEventService;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("unitUpsertEventPollingProcessor")
public class UnitUpsertEventPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource AccountingEventService accountingEventService;
	@Resource LeaseService leaseService;

	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date end; 
		List<Long> fmsIds;
		
        LOG.info(ex.getExchangeId() + "PRE UnitUpsertEventPollingProcessor... ");
                    
        end = appLogService.getEndDate();
            	     
        fmsIds = accountingEventService.getAsLongIds(AccountingNounEnum.UNIT, EventEnum.UPSERT, end);       
        
		List<Map<String, String>> unitMessages = fmsIds.stream()
			.map(id -> {
				    Map<String, String> map = new HashMap<>();
				    map.put("externalId", id.toString());
					map.put("sourceContext", EventEnum.UPSERT.name());
					return map; })
			.collect(Collectors.toList());
        
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(unitMessages);
    	    	    	
        LOG.info(ex.getExchangeId() + " POST UnitUpsertEventPollingProcessor count ... " + unitMessages.size());    	    	
	}
}

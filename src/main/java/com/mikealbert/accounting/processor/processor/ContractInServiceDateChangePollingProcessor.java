package com.mikealbert.accounting.processor.processor;

import java.util.ArrayList;
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
import com.mikealbert.accounting.processor.vo.ContractInServiceDateChangeVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("contractInServiceDateChangePollingProcessor")
public class ContractInServiceDateChangePollingProcessor extends BaseProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource AccountingEventService accountingEventService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date end; 
		List<Long> ids;
		List<ContractInServiceDateChangeVO> payload = new ArrayList<>(0);
		
        LOG.info(ex.getExchangeId() + "PRE ContractInServiceDateChangePollingProcessor... ");
                    
        end = appLogService.getEndDate();
            	     
        ids = accountingEventService.getAsLongIds(AccountingNounEnum.CONTRACT, EventEnum.IN_SERVICE_DATE_CHANGE, end);
        
        ids.stream()
        .map(id -> new ContractInServiceDateChangeVO(id))
        .forEach(payload::add);
        
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(payload);
    	    	    	
        LOG.info(ex.getExchangeId() + " POST ContractInServiceDateChangePollingProcessor count ... " + payload.size());    	    	
	}
}

package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientTransactionGroupPollingProcessor")
public class ClientTransactionGroupPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource ClientTransactionGroupService clientTransactionGroupService;	
	@Resource MessageLogService messageLogService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Override
	public void process (Exchange ex) throws Exception{				
        LOG.info(ex.getExchangeId() + "PRE clientTransactionGroupPollingProcessor... ");

        Date start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        Date end = appLogService.getEndDate();
               
    	List<ClientTransactionGroupVO> updatedClientTransactionGroupVOs = clientTransactionGroupService.getUpdates(start, end);  

		List<ClientTransactionGroupVO> allClientTransactionGroupVOs = clientTransactionGroupService.findAllClientTransactiongGroupsByAccountingPeriod(clientTransactionGroupService.distinctAccountingPeriodId(updatedClientTransactionGroupVOs));
		
		messageLogService.start(EventEnum.GROUP_TRANSACTION, clientTransactionGroupService.formatMessageIds(allClientTransactionGroupVOs));
		    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(updatedClientTransactionGroupVOs);
    	    	
        LOG.info(ex.getExchangeId() + " POST clientTransactionGroupPollingProcessor count ... " + updatedClientTransactionGroupVOs.size());    	
    	
	}
}

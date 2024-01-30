package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;
import com.mikealbert.util.data.DateUtil;

@Component("billingReportRefreshPollingProcessor")
public class BillingReportRefreshPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource BillingReportService billingReportService;

	@Value("${mafs.client-billing-transaction.base-date}")
	String clientBillingTransactionBaseDate;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date base, start, end; 
		List<BillingReportRefreshMessageVO> updates;
		
        LOG.info(ex.getExchangeId() + "PRE billingReportRefreshPollingProcessor... ");

		base = DateUtil.convertToDate(clientBillingTransactionBaseDate, DateUtil.PATTERN_DATE);
        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
               
    	updates = billingReportService.findAndDispatchUpdates(base, start, end);  
    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(updates);
    	    

        LOG.info(ex.getExchangeId() + " POST billingReportRefreshPollingProcessor count ... " + updates.size());    	
    	
	}
}

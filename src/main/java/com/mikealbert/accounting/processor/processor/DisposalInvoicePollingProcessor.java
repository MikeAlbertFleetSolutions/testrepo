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

import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.service.InvoiceService;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;

@Component("disposalInvoicePollingProcessor")
public class DisposalInvoicePollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource InvoiceService invoiceService; 
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<DisposalInvoiceVO> listDisposalInvoice;
		
        LOG.info(ex.getExchangeId() + "PRE disposalInvoicePollingProcessor... ");            
               
        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
        
        listDisposalInvoice = invoiceService.getDisposalInvoiceList(start, end);
    	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(listDisposalInvoice);
	
        LOG.info(ex.getExchangeId() + " POST disposalInvoicePollingProcessor count ... " + listDisposalInvoice.size());    	
    	
	}
}

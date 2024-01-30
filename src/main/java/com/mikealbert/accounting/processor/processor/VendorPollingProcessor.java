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
import com.mikealbert.accounting.processor.service.VendorService;

@Component("vendorPollingProcessor")
public class VendorPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource VendorService vendorService;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<Map<String, Object>> vendors;
		
        LOG.info(ex.getExchangeId() + "PRE vendorPollingProcessor... ");

        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
               
    	vendors = vendorService.getVendors(start, end);  
    			 	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(vendors);
    	    	
    	
        LOG.info(ex.getExchangeId() + " POST vendorPollingProcessor count ... " + vendors.size());    	
    	
	}
}

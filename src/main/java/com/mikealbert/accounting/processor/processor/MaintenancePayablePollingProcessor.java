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
import com.mikealbert.accounting.processor.service.CreditService;
import com.mikealbert.accounting.processor.service.InvoiceService;

@Component("maintenancePayablePollingProcessor")
public class MaintenancePayablePollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource InvoiceService invoiceService;
	@Resource CreditService creditService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		List<Map<String, Object>> docIdMaps = new ArrayList<>(0);
		
        LOG.info(ex.getExchangeId() + "PRE MaintenancePayablePollingProcessor... ");
        
        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
            	        
        invoiceService.getMaintenanceInvoiceIds(start, end).stream()
        .forEach(docId -> {
        	Map<String, Object> docIdMap = new HashMap<>();
        	docIdMap.put("docId", docId);
        	docIdMap.put("copyPurchaseOrder", false);
        	docIdMap.put("isCredit", false);        	
        	docIdMaps.add(docIdMap);
        });
        
        creditService.getMaintenanceCreditIds(start, end).stream()
        .forEach(docId -> {
        	Map<String, Object> docIdMap = new HashMap<>();
        	docIdMap.put("docId", docId);
        	docIdMap.put("copyPurchaseOrder", false);        	
        	docIdMap.put("isCredit", true);        	
        	docIdMaps.add(docIdMap);
        });        
                
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(docIdMaps);
    	    	    	
        LOG.info(ex.getExchangeId() + " POST MaintenancePayablePollingProcessor count ... " + docIdMaps.size());    	    	
	}
}

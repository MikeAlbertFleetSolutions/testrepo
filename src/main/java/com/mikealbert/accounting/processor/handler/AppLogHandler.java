package com.mikealbert.accounting.processor.handler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;

@Component("appLogHandler")
public class AppLogHandler {
	@Resource AppLogService appLogService;	
	
	@Handler
	public void handle(Exchange ex) throws Exception{		            	
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> records = (List<Map<String, Object>>) ex.getIn().getBody();
        
        appLogService.log((String) ex.getIn().getHeader(CustomHeader.POLLER_NAME), convertToJSON(records), (Date) ex.getIn().getHeader(CustomHeader.END_DATE));    	    	  	    	
	}
	
	private String convertToJSON(List<Map<String, Object>> records) throws Exception{		
		String recordsJSON = new ObjectMapper().writeValueAsString(records);	
    	return recordsJSON;
	}		
}

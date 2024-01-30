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

import com.mikealbert.accounting.processor.client.suiteanalytics.AssetSuiteAnalyticsService;
import com.mikealbert.accounting.processor.constants.CustomHeader;
import com.mikealbert.accounting.processor.service.AppLogService;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;

@Component("assetPollingProcessor")
public class AssetPollingProcessor implements Processor {
	@Resource AppLogService appLogService;
	@Resource AssetSuiteAnalyticsService assetSuiteAnalyticsService;	
	
	private final Logger LOG = LogManager.getLogger(this.getClass());			
	
	@Handler
	public void process (Exchange ex) throws Exception{
		Date start, end; 
		//List<Map<String, Object>> assets;
		List<NgAssetsPerUnitVO> listAsset;
		
        LOG.info(ex.getExchangeId() + "PRE assetPollingProcessor... ");            
               
        start = appLogService.getStartDate((String)ex.getIn().getHeader(CustomHeader.POLLER_NAME));
        end = appLogService.getEndDate();
        
        listAsset = assetSuiteAnalyticsService.getAsset(start, end);  
    	
    	ex.getIn().setHeader("endDate", end); 
        ex.getIn().setBody(listAsset);
    	    	
    	
        LOG.info(ex.getExchangeId() + " POST assetPollingProcessor count ... " + listAsset.size());    	
    	
	}
}

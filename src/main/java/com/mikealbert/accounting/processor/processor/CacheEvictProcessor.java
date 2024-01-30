package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.CacheService;

@Component("cacheEvictProcessor")
public class CacheEvictProcessor extends BaseProcessor implements Processor{
	@Resource CacheService cacheService;
	
	private static final Logger LOG = LogManager.getLogger(CacheEvictProcessor.class);
		
	
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START CacheEvictProcessor... " + message);	
        
        Map<String, String> map = super.convertJsonToMap(message);
        
        String cacheName = map.get("name");
        
        if("*".equals(cacheName)) {
        	cacheService.evictAll();
        } else {
        	cacheService.evict(cacheName);
        }
								
        LOG.info(ex.getExchangeId() + " POST CacheEvictProcessor... ");        
	}
}

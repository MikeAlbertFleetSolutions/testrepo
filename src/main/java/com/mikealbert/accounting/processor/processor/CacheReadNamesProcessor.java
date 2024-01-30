package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.CacheService;
import com.mikealbert.accounting.processor.vo.CacheVO;

@Component("cacheReadNamesProcessor")
public class CacheReadNamesProcessor implements Processor {
	@Resource CacheService cacheService;
	
	private static final Logger LOG = LogManager.getLogger(CacheReadNamesProcessor.class);	
	
	@Override
	public void process(Exchange ex) throws Exception {				
        LOG.info(ex.getExchangeId() + " START CacheReadNamesProcessor... ");	
        				
		List<CacheVO> caches = cacheService.getAll();
						
		ObjectMapper mapper = new ObjectMapper();
		String response = mapper.writeValueAsString(caches);
		
        LOG.info(ex.getExchangeId() + " POST CacheReadNamesProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}

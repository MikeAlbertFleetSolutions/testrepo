package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.UnitVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("unitInternalReadProcessor")
public class UnitInternalReadProcessor extends BaseProcessor implements Processor {
	@Resource UnitService unitService;
	@Resource Environment env;
	
	private static final Logger LOG = LogManager.getLogger(UnitInternalReadProcessor.class);
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START UnitInternalReadProcessor... " + message);	
        
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = mapper.readValue(message, new TypeReference<Map<String, String>>(){});

		LOG.info("UnitInternalReadProcessor message map: {} ", map);

		UnitVO unitVO = new UnitVO();
		unitVO.setFmsId(Long.valueOf(map.get("externalId")));
		unitVO	= unitService.getUnitInfo(unitVO);
		ex.getIn().setBody(unitVO);
		
        LOG.info(ex.getExchangeId() + " POST UnitInternalReadProcessor... ");
        
	}
}

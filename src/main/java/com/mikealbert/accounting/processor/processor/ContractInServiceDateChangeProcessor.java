package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.ContractService;
import com.mikealbert.accounting.processor.vo.ContractInServiceDateChangeVO;

@Component("contractInServiceDateChangeProcessor")
public class ContractInServiceDateChangeProcessor extends BaseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(ContractInServiceDateChangeProcessor.class);
	
	@Resource ContractService contractService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start ContractInServiceDateChangeProcessor... ");
        
        String json = (String)ex.getMessage().getBody();
        
        ObjectMapper mapper = new ObjectMapper();
        ContractInServiceDateChangeVO contract = mapper.readValue(json, ContractInServiceDateChangeVO.class);
        		
		contractService.notify(contract.getId());
			
        LOG.info(ex.getExchangeId() + " POST ContractInServiceDateChangeProcessor... " + contract.toString());
	}
}

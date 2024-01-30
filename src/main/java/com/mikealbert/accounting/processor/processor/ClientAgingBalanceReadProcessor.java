package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.vo.ClientVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientAgingBalanceReadProcessor")
public class ClientAgingBalanceReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource ClientService clientService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientAgingBalanceReadProcessor ..." + message);
							
		List<ClientVO> clientVOs = clientService.findWithBalance();
		
		String response = super.convertToJSON(clientVOs);
		
		LOG.info(ex.getExchangeId() + "End ClientAgingBalanceReadProcessor ... " + clientVOs.size());
		
		ex.getIn().setBody(response);		
	}	
}

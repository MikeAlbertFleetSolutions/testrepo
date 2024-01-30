package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientTransactionReadProcessor")
public class ClientTransactionReadProcessor extends BaseProcessor implements Processor {	
	@Resource ClientTransactionGroupService clientTransactionGroupService;
	@Resource ClientService clientService;

	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE ClientTransactionReadProcessor... ");

		String message = (String)ex.getMessage().getBody();

		Map<String, String> messageMap = super.convertJsonToMap(message);

		List<Map<String, Object>> result;
		if(messageMap.containsKey("isGrouped")) {
		    result = clientTransactionGroupService.findGroupTransactions(messageMap.get("accountingPeriodId"), 
			        clientService.formatExternalId(messageMap.get("clientAccountCode")), Boolean.parseBoolean(messageMap.get("isGrouped")));
		} else {
		    result = clientTransactionGroupService.findGroupTransactions(messageMap.get("accountingPeriodId"), 
			        clientService.formatExternalId(messageMap.get("clientAccountCode")));
		}
				
        String response = super.convertToJSON(result);
														               
		ex.getIn().setBody(response);

        LOG.info(ex.getExchangeId() + " POST ClientTransactionReadProcessor completed ... " + response);
	}
			
}
 
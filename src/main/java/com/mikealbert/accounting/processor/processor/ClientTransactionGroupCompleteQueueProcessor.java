package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientTransactionGroupCompleteQueueProcessor")
public class ClientTransactionGroupCompleteQueueProcessor extends BaseProcessor implements Processor {
	@Resource ClientTransactionGroupService clientTransactionGroupService;
		
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE ClientTransactionGroupCompleteQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();
		Map<String, Object> messageMap = super.convertJsonToObjectMap(message);
				
		clientTransactionGroupService.complete();

		clientTransactionGroupService.emailComplete((List<String>)messageMap.get("accountingPeriods"));
        
		ex.getIn().setBody(message);

        LOG.info(ex.getExchangeId() + " POST ClientTransactionGroupCompleteQueueProcessor completed ... " + message);
	}    
}

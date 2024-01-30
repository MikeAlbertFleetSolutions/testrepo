package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.enumeration.ClientFieldEnum;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("clientQueueProcessor")
public class ClientQueueProcessor extends BaseProcessor implements Processor {	
	@Resource ClientService clientService;
	
	@Value("${spring.profiles.active}")
	private String activeProfile;

	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {						
        ExternalAccount account;

		LOG.info(ex.getExchangeId() + "PRE ClientQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();
				
		Map<String, String> client = super.convertJsonToMap(message);
					
		clientService.getClientParents(client).stream()
		        .forEach(parentMap -> super.sendToQueue(QueueEnum.CLIENT_UPDATE, parentMap.get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()), parentMap));

		account = clientService.process(client);
												               
		ex.getIn().setBody(account);

        LOG.info(ex.getExchangeId() + " POST ClientQueueProcessor completed ... " + account);		
	}
			
}
 
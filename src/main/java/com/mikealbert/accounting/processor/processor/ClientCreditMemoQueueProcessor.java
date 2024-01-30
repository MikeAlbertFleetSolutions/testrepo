package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.enumeration.ClientCreditMemoFieldEnum;
import com.mikealbert.accounting.processor.service.ClientCreditMemoService;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientCreditMemoQueueProcessor")
public class ClientCreditMemoQueueProcessor extends BaseProcessor implements Processor {	
	@Resource ClientCreditMemoService clientCreditMemoService;

	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE ClientCreditMemoQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();
				
		Map<String, String> creditMemoMap = super.convertJsonToMap(message);

		ClientCreditMemoVO clientCreditMemoVO = new ClientCreditMemoVO()
		        .setInternalId(creditMemoMap.get(ClientCreditMemoFieldEnum.INTERNAL_ID.getScriptId()))
				.setExternalId(creditMemoMap.get(ClientCreditMemoFieldEnum.EXTERNAL_ID.getScriptId()))
				.setTranId(creditMemoMap.get(ClientCreditMemoFieldEnum.TRAN_ID.getScriptId()))
				.setTransactionNumber(creditMemoMap.get(ClientCreditMemoFieldEnum.TRANSACTION_NUMBER.getScriptId()));
					
		clientCreditMemoService.process(clientCreditMemoVO);
												               
		ex.getIn().setBody(message);

        LOG.info(ex.getExchangeId() + " POST ClientCreditMemoQueueProcessor completed ... " + message);
	}
			
}
 
package com.mikealbert.accounting.processor.processor;

import java.util.HashMap;
import java.util.Map;

import com.mikealbert.accounting.processor.enumeration.ClientCreditMemoFieldEnum;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientCreditMemoCorrelationIdProcessor")
public class ClientCreditMemoCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
	LOG.info(ex.getExchangeId() + " ClientCreditMemoCorrelationIdProcessor is started ...");
	
	@SuppressWarnings("unchecked")
	Map<String, Object> creditMemo = (HashMap<String, Object>)ex.getIn().getBody();
	
	String correlationId = (String) creditMemo.get(ClientCreditMemoFieldEnum.EXTERNAL_ID.getScriptId());
				
	ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
    ex.getIn().setBody(ex.getIn().getBody());
    
	LOG.info(ex.getExchangeId() + " ClientCreditMemoCorrelationIdProcessor completed id=" + correlationId);
	}
}

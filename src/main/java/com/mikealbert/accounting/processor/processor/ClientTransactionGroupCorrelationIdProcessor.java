package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientTransactionGroupCorrelationIdProcessor")
public class ClientTransactionGroupCorrelationIdProcessor {
	@Resource ClientTransactionGroupService clientTransactionGroupService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {	
		LOG.info(ex.getExchangeId() + " ClientTransactionGroupCorrelationIdProcessor is started ...");
		
		ClientTransactionGroupVO clientTransactionGroupVO = (ClientTransactionGroupVO)ex.getIn().getBody();
		
		String correlationId = clientTransactionGroupService.formatMessageId(clientTransactionGroupVO);
					
		ex.getIn().setHeader("JMSCorrelationID", correlationId);	          
		ex.getIn().setBody(ex.getIn().getBody());
		
		LOG.info(ex.getExchangeId() + " ClientTransactionGroupCorrelationIdProcessor completed id=" + correlationId);
	}
}

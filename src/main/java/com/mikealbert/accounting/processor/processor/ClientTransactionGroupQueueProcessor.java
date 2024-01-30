package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.service.ClientCreditMemoService;
import com.mikealbert.accounting.processor.service.ClientInvoiceDepositService;
import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.service.ClientTransactionGroupService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;

@Component("clientTransactionGroupQueueProcessor")
public class ClientTransactionGroupQueueProcessor extends BaseProcessor implements Processor {	
	@Resource ClientTransactionGroupService clientTransactionGroupService;
	@Resource ClientInvoiceDepositService clientInvoiceDepositService;
	@Resource ClientInvoiceService clientInvoiceService;
	@Resource ClientCreditMemoService clientCreditMemoService;
	@Resource ClientService clientService;
	@Resource MessageLogService messageLogService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE ClientTransactionGroupQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();
				
		ClientTransactionGroupVO clientTransactionGroupVO = new ObjectMapper().readValue(message, ClientTransactionGroupVO.class);
		clientTransactionGroupVO.setClientAccountCode(clientService.parseAccountCodeFromExternalId(clientTransactionGroupVO.getClientExternalId()));			     

		if(!hasBeenProcessed(clientTransactionGroupVO)) {
			clientCreditMemoService.process(clientCreditMemoService.findByTransactionGroup(clientTransactionGroupVO));

			List<ClientInvoiceVO> groupedInvoices = clientInvoiceService.findByTransactionGroup(clientTransactionGroupVO);
			List<ClientInvoiceDepositVO> deposits = clientInvoiceDepositService.deposits(groupedInvoices);
			clientInvoiceDepositService.process(deposits);
								
			clientTransactionGroupService.process(clientTransactionGroupVO);

			messageLogService.end(EventEnum.GROUP_TRANSACTION, clientTransactionGroupService.formatMessageId(clientTransactionGroupVO));
		}		
														               
		ex.getIn().setBody(message);

        LOG.info(ex.getExchangeId() + " POST ClientTransactionGroupQueueProcessor completed ... " + message);
	}

	public boolean hasBeenProcessed(ClientTransactionGroupVO clientTransactionGroupVO) {
		boolean isProcessed = false;

		MessageLog messageLog = messageLogService.find(EventEnum.GROUP_TRANSACTION, clientTransactionGroupService.formatMessageId(clientTransactionGroupVO));
		if(messageLog != null && messageLog.getEndDate() != null) {
			isProcessed = true;
		}

		return isProcessed;
	}
			
}
 
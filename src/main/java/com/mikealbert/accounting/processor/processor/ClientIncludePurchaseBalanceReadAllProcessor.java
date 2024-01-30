package com.mikealbert.accounting.processor.processor;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

@Component("clientIncludePurchaseBalanceReadAllProcessor")
public class ClientIncludePurchaseBalanceReadAllProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource ClientService clientService;
	@Resource ClientInvoiceService clientInvoiceService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientIncludePurchaseBalanceReadAllProcessor ..." + message);
							
		List<ClientVO> clientVOs = clientService.findActive();
		for(ClientVO clientVO : clientVOs) {
			if(clientVO.getBalance().compareTo(BigDecimal.ZERO) != 0 || clientVO.getUnappliedBalance().compareTo(BigDecimal.ZERO) != 0) {
				clientVO.setPurchaseBalance(clientInvoiceService.sumBalance(clientInvoiceService.findOutStanding(clientVO.getInternalId(), clientVO.getExternalId(), ControlCodeEnum.AM_LC)));
			}
		}
		
		String response = super.convertToJSON(clientVOs);
		
		LOG.info(ex.getExchangeId() + "End ClientIncludePurchaseBalanceReadAllProcessor ... " + clientVOs.size());
		
		ex.getIn().setBody(response);		
	}	
}

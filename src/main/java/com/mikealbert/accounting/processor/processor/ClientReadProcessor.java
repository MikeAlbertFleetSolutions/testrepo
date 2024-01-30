package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.AgingTransactionService;
import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientPaymentService;
import com.mikealbert.accounting.processor.service.ClientService;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientReadProcessor")
public class ClientReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource ClientService clientService;
	@Resource ClientInvoiceService clientInvoiceService;
	@Resource ClientPaymentService clientPaymentService;
	@Resource AgingTransactionService agingTransactionService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientReadProcessor ..." + message);
		
		Map<String, String> map = super.convertJsonToMap(message);
		
		String externalId = String.valueOf(map.get("externalId"));
		boolean loadPurchaseBalance = Boolean.parseBoolean(map.get("loadPurchaseBalance"));
				
		ClientVO clientVO = clientService.get(externalId, loadPurchaseBalance);

		if(loadPurchaseBalance) {
			clientVO = loadPurchaseBalance(clientVO);
		}
		
		String response = super.convertToJSON(clientVO);
		
		LOG.info(ex.getExchangeId() + "End ClientReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}	

	private ClientVO loadPurchaseBalance(ClientVO clientVO) throws Exception {
		return clientVO
		        .setPurchaseBalance(clientInvoiceService.sumBalance(clientInvoiceService.findOutStanding(clientVO.getInternalId(), clientVO.getExternalId(), ControlCodeEnum.AM_LC)));		
	}
}

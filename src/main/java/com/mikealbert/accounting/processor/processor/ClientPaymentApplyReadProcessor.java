package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ClientInvoiceService;
import com.mikealbert.accounting.processor.service.ClientPaymentService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientPaymentApplyReadProcessor")
public class ClientPaymentApplyReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource ClientInvoiceService clientInvoiceService;
	@Resource ClientPaymentService clientPaymentService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientPaymentApplyReadProcessor ..." + message);
		
		Map<String, String> map = super.convertJsonToMap(message);
		
		String invoiceInternalId = "null".equals(String.valueOf(map.get("internalId"))) ? null : String.valueOf(map.get("internalId"));
		String invoiceExternalId = "null".equals(String.valueOf(map.get("externalId"))) ? null : String.valueOf(map.get("externalId"));		
		
		ClientPaymentApplyVO clientPaymentVO = clientPaymentService.getInvoiceLastPayment(determineInternalId(invoiceInternalId, invoiceExternalId));
		
		String response = super.convertToJSON(clientPaymentVO);
		
		LOG.info(ex.getExchangeId() + "End ClientPaymentApplyReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}	

	private String determineInternalId(String internalId, String externalId) throws Exception {
		String intId = internalId;
		
		if(internalId == null && externalId != null) {
			ClientInvoiceVO invoice = clientInvoiceService.get(null, externalId);
			intId = invoice == null ? null : invoice.getInternalId();
		}

		return intId;
	}
}

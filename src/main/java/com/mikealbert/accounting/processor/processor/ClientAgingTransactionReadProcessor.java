package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.AgingTransactionService;
import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("clientAgingTransactionReadProcessor")
public class ClientAgingTransactionReadProcessor extends BaseProcessor implements Processor{
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource AgingTransactionService agingTransactionService;

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start ClientAgingTransactionReadProcessor ..." + message);
		
		Map<String, String> map = super.convertJsonToMap(message);

		String internalId = map.get("internalId");
		String externalId = map.get("externalId");
		AgingPeriodEnum agingPeriod = AgingPeriodEnum.valueOf(map.get("agingPeriod"));
		
		List<ReceivableTransactionVO<?, ?>> transactions = agingTransactionService.getAging(internalId, externalId, agingPeriod);
		
		String response = super.convertToJSON(transactions);
		
		LOG.info(ex.getExchangeId() + "End ClientAgingTransactionReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}	
}

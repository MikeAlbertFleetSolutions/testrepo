package com.mikealbert.accounting.processor.processor;

import java.util.List;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportReadMessageVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;

@Component("billingReportReadProcessor")
public class BillingReportReadProcessor extends BaseProcessor implements Processor{
	@Resource BillingReportService billingReportService;
	@Resource ServiceCache serviceCache;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start BillingReportReadProcessor ..." + message);
		
		BillingReportReadMessageVO messageVO = new ObjectMapper().readValue(message, BillingReportReadMessageVO.class);

		BillingReportTypeEnum reportType = messageVO.getReportName() == null ? null : BillingReportTypeEnum.getByReportName(messageVO.getReportName());

		List<AccountingPeriodVO> accountingPeriods = serviceCache.findAccountingPeriodByNameRange(messageVO.getStartingAccountingPeriod(), messageVO.getEndingAccountingPeriod());
		List<BillingReportTransactionVO> transactions = billingReportService.get(messageVO.getClientExternalId(), accountingPeriods, reportType);
		transactions = billingReportService.filterReportWorthy(messageVO.getClientExternalId().replace("1C", ""), transactions);

		billingReportService.validate(transactions, false);
				
		String response = super.convertToJSON(transactions);
		
		LOG.info(ex.getExchangeId() + "End BillingReportReadProcessor ...");
		
		ex.getIn().setBody(response);		
	}
	


}

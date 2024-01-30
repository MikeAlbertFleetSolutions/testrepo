package com.mikealbert.accounting.processor.processor;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.service.AccountingPeriodService;
import com.mikealbert.accounting.processor.service.BillingReportService;
import com.mikealbert.accounting.processor.service.MessageLogService;
import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;


@Component("billingReportRefreshQueueProcessor")
public class BillingReportRefreshQueueProcessor extends BaseProcessor implements Processor {	
	@Resource BillingReportService billingReportService;
	@Resource AccountingPeriodService accountingPeriodService;
	@Resource ServiceCache serviceCache;
	@Resource MessageLogService messageLogService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE BillingReportRefreshQueueProcessor... ");

		String message = (String)ex.getMessage().getBody();		
		BillingReportRefreshMessageVO messageVO = new ObjectMapper().readValue(message, BillingReportRefreshMessageVO.class);

		if(!isBeingProcessed(messageVO)) {
			try {
			    messageLogService.start(EventEnum.REFRESH_BILLING_REPORT, formatMessageId(messageVO, ex.getExchangeId()));
    
			    List<AccountingPeriodVO> accountingPeriodVOs =  serviceCache.findAccountingPeriodByNameRange(messageVO.getStartPeriod(), messageVO.getEndPeriod());
    
				BillingReportTypeEnum reportType = messageVO.getReportName() == null ? null : BillingReportTypeEnum.getByReportName(messageVO.getReportName());

			    List<BillingReportTransactionVO> transactions = billingReportService.get(messageVO.getAccountCode(), accountingPeriodVOs, reportType);
				transactions = billingReportService.filterReportWorthy(messageVO.getAccountCode(), transactions);
    
				billingReportService.validate(transactions, false);

			    billingReportService.upsertInternalStore(transactions, messageVO.isForce());
    
			    billingReportService.mergeInternalStore(messageVO.getAccountCode(), accountingPeriodVOs, messageVO.isForce());

			} finally {
				messageLogService.end(EventEnum.REFRESH_BILLING_REPORT, formatMessageId(messageVO, ex.getExchangeId()));			
			}
		}
														               
		ex.getIn().setBody(message);

        LOG.info(ex.getExchangeId() + " POST BillingReportRefreshQueueProcessor completed ... " + message);
	}
	
	public boolean isBeingProcessed(BillingReportRefreshMessageVO messageVO) {
		String messageIdPrefix = String.format("%s|%s", messageVO.getAccountCode(), messageVO.getStartPeriod());
		boolean isProcessed = false;

		List<MessageLog> messageLogs = messageLogService.findWithPartialMessageId(EventEnum.REFRESH_BILLING_REPORT, messageIdPrefix).stream()
		    .filter(msg -> (msg.getEndDate() == null))
			.collect(Collectors.toList());

		if(!messageLogs.isEmpty()) {
			isProcessed = true;
		}

		return isProcessed;
	}

	private String formatMessageId(BillingReportRefreshMessageVO messageVO, String exchangeId) {
		return String.format("%s|%s|%s", messageVO.getAccountCode(), messageVO.getStartPeriod(), exchangeId);
	}
}
 
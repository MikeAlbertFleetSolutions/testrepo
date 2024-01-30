package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.entity.QuotationModel;
import com.mikealbert.accounting.processor.exception.NoDataFoundException;
import com.mikealbert.accounting.processor.exception.RetryableException;
import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.vo.AccountingEventMessageVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component("leaseInterestUpdateProcessor")
public class LeaseInterestUpdateProcessor extends BaseProcessor implements Processor {
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource LeaseService leaseService;
	@Resource QuotationModelDAO quotationModelDAO;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start LeaseInterestUpdateProcessor... ");
        
		String body = (String)ex.getMessage().getBody();

		ObjectMapper mapper = new ObjectMapper();
		AccountingEventMessageVO message  = mapper.readValue(body, AccountingEventMessageVO.class);				

		Integer redeliveryCounter = (Integer)ex.getMessage().getHeader(Exchange.REDELIVERY_COUNTER);
		
		LeaseVO lease = null;
		
		try {
			QuotationModel qmd = quotationModelDAO.findByQmdId(Long.valueOf(message.getEntityId()))
					.orElseThrow(() -> new NoDataFoundException("No Quotation Model found for qmdId: " + message.getEntityId()));

			lease = leaseService.getExternalLease(String.valueOf(qmd.getQuotation().getQuoId()), false).stream()
					.findFirst()
					.orElseThrow(() -> new NoDataFoundException("No Lease found for externalId: " + String.format("%s-%s", qmd.getQuotation().getQuoId(), qmd.getRevisionNo())));

			lease.setInterestRate(qmd.getInterestRate() == null ? null : qmd.getInterestRate().doubleValue());

			leaseService.updateInterestRate(lease);			
		} catch(NoDataFoundException e) {
			if(redeliveryCounter != null && redeliveryCounter.compareTo(retryMax - 1) == 0) {
				LOG.warn(e.getMessage());				
			} else {
				throw new RetryableException(e);
			}			
		}
        		
        LOG.info(ex.getExchangeId() + " POST LeaseInterestUpdateProcessor... " + lease);
        
		ex.getIn().setBody(lease);
	}
}

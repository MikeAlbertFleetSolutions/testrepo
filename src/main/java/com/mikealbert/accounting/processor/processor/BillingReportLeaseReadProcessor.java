package com.mikealbert.accounting.processor.processor;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.service.ServiceCache;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("billingReportLeaseReadProcessor")
public class BillingReportLeaseReadProcessor extends BaseProcessor implements Processor {
	@Resource ServiceCache serviceCache;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void process(Exchange ex) throws Exception {
		String message = (String)ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + "Start BillingReportLeaseReadProcessor ..." + message);
		
		Map<String, Object> map = super.convertJsonToObjectMap(message);
		
		String unitInternalId = String.valueOf(map.get("unitInternalId"));
		Date effectiveDate = new Date((Long)map.get("effectiveDate"));

		BillingReportLeaseVO billingReportLeaseVO = serviceCache.findBillingReportLeaseByUnitInternalIdAndEffectiveDate(unitInternalId, effectiveDate);

		String response = super.convertToJSON(billingReportLeaseVO);

		ex.getIn().setBody(response);

		LOG.info(ex.getExchangeId() + "End BillingReportLeaseReadProcessor ...");		
				
	}	
}

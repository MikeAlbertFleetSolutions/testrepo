package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerPaymentSuiteTalkService;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("clientPaymentService")
public class ClientPaymentServiceImpl extends BaseService implements ClientPaymentService {
	@Resource CustomerPaymentSuiteTalkService customerPaymentSuiteTalkService;
	@Resource TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;

	private final Logger LOG = LogManager.getLogger(this.getClass());	

	/**
	 * Retrieves the last payment transaction applied to the client's invoice.
	 * 
	 * @param internalId The internal Id of the invoice
	 * @return ClientPaymentVO The last payment applied to the invoice
	 */
	@Override
	public ClientPaymentApplyVO getInvoiceLastPayment(String internalId) throws Exception {
		ClientPaymentApplyVO clientPaymentVO = null;

		List<Map<String, Object>> payments = transactionSuiteAnalyticsService.findPaymentsByInvoice(internalId, null);
		
		Map<String, Object> lastPayment = lastPayment(payments);


	    if(lastPayment != null) {
			clientPaymentVO = customerPaymentSuiteTalkService.getPaymentApply(internalId, (String)lastPayment.get("internal_id"), null, (String)lastPayment.get("transaction_type"));			
		}
			
		LOG.info("POST getinvoiceLastPaymen {}", clientPaymentVO);
		
		return clientPaymentVO;
	}

	@Override
	public ClientPaymentVO getClientLastPayment(String clientInternalId, String clientExternalId) throws Exception {
		return customerPaymentSuiteTalkService.getLastPayment(clientInternalId, clientExternalId);
	}

	private Map<String, Object> lastPayment(List<Map<String, Object>> lastPayments) {
		return lastPayments.stream()
		    .sorted((a, b) -> ((Date)b.get("trandate")).compareTo((Date)a.get("trandate")))
			.findFirst()
			.orElse(null);
	}
	
}

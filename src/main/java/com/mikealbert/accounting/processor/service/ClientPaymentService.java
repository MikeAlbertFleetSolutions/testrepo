package com.mikealbert.accounting.processor.service;

import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;

public interface ClientPaymentService {		
	public ClientPaymentApplyVO getInvoiceLastPayment(String internalId) throws Exception;

	public ClientPaymentVO getClientLastPayment(String clientInternalId, String clientExternalId) throws Exception;
}

package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;

public interface CustomerPaymentSuiteTalkService  {
	public static final String PAYABLE_ACCOUNT = "20010 Total Accounts Payable : Accounts Payable";
		
	public void create(ClientPaymentVO paymentVO) throws Exception;

	public void delete(ClientPaymentVO paymentVO) throws Exception;

	public void update(ClientPaymentVO paymentVO) throws Exception;

	public ClientPaymentVO getPayment(String internalId, String externalId) throws Exception;

	public ClientPaymentApplyVO getPaymentApply(String invoiceInternalId, String paymentInternalId, String paymentExternalId, String paymentType) throws Exception;	

	public ClientPaymentVO getLastPayment(String clientInternalId, String clientExternalId) throws Exception;
}

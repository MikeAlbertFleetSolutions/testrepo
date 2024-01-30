package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;

import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.netsuite.webservices.transactions.sales_2023_2.Invoice;

public interface InvoiceSuiteTalkService  {
	public static final String PAYABLE_ACCOUNT = "20010 Total Accounts Payable : Accounts Payable";
		
	public void create(ClientInvoiceVO clientInvoiceVO) throws Exception;

	public void delete(ClientInvoiceVO clientInvoiceVO) throws Exception;

	public ClientInvoiceVO convertToClientInvoiceVO(Invoice invoice) throws Exception;

	public ClientInvoiceVO get(String internalId, String externalId) throws Exception;

	public List<ClientInvoiceVO> findOustanding(String clientInternalId, String clientExternalId, String maType) throws Exception;
}

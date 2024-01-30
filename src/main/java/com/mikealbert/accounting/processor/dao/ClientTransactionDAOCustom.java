package com.mikealbert.accounting.processor.dao;

import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;

public interface ClientTransactionDAOCustom {
	public void processCreditMemo(ClientCreditMemoVO clientCreditMemoVO) throws Exception;

	public void processTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception;

	public void processTransactionGroupComplete() throws Exception;	

	public void processInvoiceDeposit(ClientInvoiceDepositVO clientInvoiceDepositVO) throws Exception;

	public void processDisposalInvoiceInfo(DisposalInvoiceVO disposalInvoiceVO) throws Exception;
}

package com.mikealbert.accounting.processor.service;

import java.util.List;

import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;

public interface ClientInvoiceDepositService {		
	public List<ClientInvoiceDepositVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO ) throws Exception;	

	public void process(List<ClientInvoiceDepositVO> clientInvoiceDepositVOs) throws Exception;

	public void process(ClientInvoiceDepositVO clientInvoiceDepositVO) throws Exception;
	
	public List<ClientInvoiceDepositVO> deposits(List<ClientInvoiceVO> clientInvoiceVO) throws Exception;
}

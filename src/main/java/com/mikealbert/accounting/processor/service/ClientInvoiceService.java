package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

public interface ClientInvoiceService {
		
	public ClientInvoiceVO get(String internalId, String externalId) throws Exception;

	public List<ClientInvoiceVO> findOutStanding(String clientInternalId, String clientExternalId, ControlCodeEnum  controlCode) throws Exception;

	public BigDecimal sumBalance(List<ClientInvoiceVO> clientInvoiceVOs) throws Exception;

	public BigDecimal sumLinePaidAmount(ClientInvoiceVO clientInvoiceVO) throws Exception;

	public List<ClientInvoiceVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO ) throws Exception;		

	public boolean hasDocOnLine(ClientInvoiceVO clientInvoiceVO);

	public List<ClientInvoiceVO> filterInternalInvoices(List<ClientInvoiceVO> clientInvoiceVOs);
}

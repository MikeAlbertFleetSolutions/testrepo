package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suiteanalytics.InvoiceSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.InvoiceSuiteTalkService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ApplicationEnum;

@Service("clientInvoiceService")
public class ClientInvoiceServiceImpl extends BaseService implements ClientInvoiceService {
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	@Resource InvoiceSuiteAnalyticsService invoiceSuiteAnalyticsService;
	
	/**
	 * Retrieves the client invoice from the external system.
	 *
	 * @param internalId of the client invoice
	 * @param externalId of the client invoice 
	 *
	 * @return ClientInvoiceVO of the found client invoice
	 */
	@Override
	public ClientInvoiceVO get(String internalId, String externalId) throws Exception {
		return invoiceSuiteTalkService.get(internalId, externalId);
	}

	/**
	 * Searches for outstanding invoices based on client and MA Type
	 * 
	 * @param clientInternalId Accounting system's client id
	 * @param clientExternalId Mike Albert's client id
	 * @return A list of ClientInvoiceVO
	 * 
	 */
	@Override
	public List<ClientInvoiceVO> findOutStanding(String clientInternalId, String clientExternalId, ControlCodeEnum controlCode) throws Exception {
		return invoiceSuiteTalkService.findOustanding(clientInternalId, clientExternalId, controlCode.name());
	}

	/**
	 * Sums up the unpaid balance 
	 * 
	 * @param clientInvoiceVOs client invoices
	 * @return total balance of all the invoices 
	 */
	@Override
	public BigDecimal sumBalance(List<ClientInvoiceVO> clientInvoiceVOs) throws Exception {
		return clientInvoiceVOs.stream()
		        .map(clientInvoice -> clientInvoice.getBalance() == null ? BigDecimal.ZERO : clientInvoice.getBalance())
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public BigDecimal sumLinePaidAmount(ClientInvoiceVO clientInvoiceVO) throws Exception {
		return clientInvoiceVO.getLines().stream()
		        .map(line -> line.getLinePaidAmount() == null ? BigDecimal.ZERO : line.getLinePaidAmount())
		        .collect(Collectors.reducing(BigDecimal.ZERO, BigDecimal::add));
	}

	@Override
	public List<ClientInvoiceVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception {
		List<Map<String, Object>> invoiceMaps = invoiceSuiteAnalyticsService.findGroupedInvoices(clientTransactionGroupVO.getClientInternalId(), clientTransactionGroupVO.getClientExternalId(), clientTransactionGroupVO.getAccountingPeriodId(), clientTransactionGroupVO.getGroupNumber());

		List<ClientInvoiceVO> invoiceVOs = invoiceMaps.stream().parallel()
		        .map(m -> {
					try{
					    return invoiceSuiteTalkService.get((String)m.get("internalId"), null);
					}catch(Exception e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList());
		
		return invoiceVOs == null ? new ArrayList<>(0) : invoiceVOs;
	}

	@Override
	public boolean hasDocOnLine(ClientInvoiceVO clientInvoiceVO) {
		if(clientInvoiceVO.getLines().isEmpty()) return false;

		List<ClientInvoiceLineVO> lines = clientInvoiceVO.getLines().stream()
		    .filter(l -> l.getDocId() != null && l.getDocLineId() != null ? true : false)
			.collect(Collectors.toList());

		return lines.size() == clientInvoiceVO.getLines().size() ? true : false;
	}

	@Override
	public List<ClientInvoiceVO> filterInternalInvoices(List<ClientInvoiceVO> clientInvoiceVOs) {
		return clientInvoiceVOs.stream()
				.filter(i -> i.getOrigin() == ApplicationEnum.WILLOW)
				.collect(Collectors.toList());
	}	
}

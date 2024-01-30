package com.mikealbert.accounting.processor.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suiteanalytics.InvoiceSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.DepositApplicationSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.InvoiceSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.enumeration.ClientInvoiceDepositFieldEnum;
import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.util.data.DateUtil;

@Service("clientInvoiceDepositService")
public class ClientInvoiceDepositServiceImpl extends BaseService implements ClientInvoiceDepositService {
	@Resource InvoiceSuiteAnalyticsService invoiceSuiteAnalyticsService;
	@Resource ClientTransactionDAO clientTransactionDAO;
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	@Resource DepositApplicationSuiteTalkService depositApplicationSuiteTalkService;
	@Resource ClientInvoiceService clientInvoiceService; 

	private final Logger LOG = LogManager.getLogger(this.getClass());	

	/** 
	 * Retrieves the client's grouped invoice deposit applications
	 * 
	 * @param clientTransactionGroupVO contains the clients' grouping detail needed to get the applied deposits
	 * @return List of ClientInvoiceDepositVO, each containing minimum key details of the invoice deposit transactions 
	*/	
	@Override
	public List<ClientInvoiceDepositVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception {
		List<Map<String, Object>> clientInvoiceDepositMaps = invoiceSuiteAnalyticsService.findGroupedInvoicesWithDepositApplication(
			    clientTransactionGroupVO.getClientInternalId(), clientTransactionGroupVO.getAccountingPeriodId().toString(), clientTransactionGroupVO.getGroupNumber());

		return clientInvoiceDepositMaps.stream().parallel()
		    .map(clientInvoiceDepositMap -> {
				return new ClientInvoiceDepositVO()
				        .setDepositApplicationInternalId((String)clientInvoiceDepositMap.get(ClientInvoiceDepositFieldEnum.DEPOSIT_APPLICATION_INTERNAL_ID.getScriptId()))
				        .setDepositApplicationTranId((String)clientInvoiceDepositMap.get(ClientInvoiceDepositFieldEnum.DEPOSIT_APPLICATION_TRANID.getScriptId()))
				        .setInvoiceInternalId((String)clientInvoiceDepositMap.get(ClientInvoiceDepositFieldEnum.INVOICE_INTERNAL_ID.getScriptId()))
						.setInvoiceTranId((String)clientInvoiceDepositMap.get(ClientInvoiceDepositFieldEnum.INVOICE_TRANID.getScriptId())); })
			.collect(Collectors.toList());
	}		

	@Override
	public void process(List<ClientInvoiceDepositVO> clientInvoiceDepositVOs) throws Exception {
		for(ClientInvoiceDepositVO clientInvoiceDeposiVO : clientInvoiceDepositVOs) {
			process(clientInvoiceDeposiVO);
		}
	}

	/**
	 * Processes clients' grouped invoice deposit transactions
	 * 
	 * @param invoiceDepositMap invoice deposit transaction detail
	 */	
	@Override
	public void process(ClientInvoiceDepositVO clientInvoiceDepositVO) throws Exception {
		String jsonClientDepositInvoice = null;
							
		jsonClientDepositInvoice = new ObjectMapper().setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME)).writerWithDefaultPrettyPrinter().writeValueAsString(clientInvoiceDepositVO);

		clientTransactionDAO.processInvoiceDeposit(clientInvoiceDepositVO);	

		LOG.info("Processed Client's grouped invoice deposit {}", jsonClientDepositInvoice);
	}

	/**
	 * Intializes a ClientInvoiceDepositVO based on the internal originated invoice. 
	 * 
	 * Note: The doc id and doc line id on the line are used when the two fields exists on all the lines.
	 *       Otherwise, the doc id and doc line id on the header is used instead.
	 * 
	 * Note: When Doc Id and Doc Line Id are on the line level, a client invoice deposit 
	 *       is created for each line (doc id, doc line id, and line paid amount).
	 */
	@Override
	public List<ClientInvoiceDepositVO> deposits(List<ClientInvoiceVO> clientInvoiceVOs) throws Exception {
		List<ClientInvoiceDepositVO> deposits = new ArrayList<>(0);

		List<ClientInvoiceVO> internalInvoices = clientInvoiceService.filterInternalInvoices(clientInvoiceVOs);

		for(ClientInvoiceVO invoice : internalInvoices) {
			if(clientInvoiceService.hasDocOnLine(invoice)) {
				deposits = invoice.getLines().stream()
				        .map(line -> {
				        	    try{
				        	    	return new ClientInvoiceDepositVO(line.getDocId(),  line.getDocLineId(), line.getLinePaidAmount());
				        	    }catch(Exception e) {
				        	    	throw new RuntimeException(e);
				        	    } })
				        .collect(Collectors.toList());
			} else {
				deposits = clientInvoiceVOs.stream()
				        .map(header -> {
				        	    try{
				        	    	return new ClientInvoiceDepositVO(header.getDocId(),  header.getDocLineId(), clientInvoiceService.sumLinePaidAmount(header));
				        	    }catch(Exception e) {
				        	    	throw new RuntimeException(e);
				        	    } })
				        .collect(Collectors.toList());
			}
		}
		
		return deposits;
	}
		
}

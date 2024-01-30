package com.mikealbert.accounting.processor.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suiteanalytics.CreditMemoSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CreditMemoSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.enumeration.ClientCreditMemoFieldEnum;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.util.data.DateUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("clientCreditMemoService")
public class ClientCreditMemoServiceImpl extends BaseService implements ClientCreditMemoService {
	@Resource CreditMemoSuiteAnalyticsService creditMemoSuiteAnalyticsService;
	@Resource CreditMemoSuiteTalkService creditMemoSuiteTalkService;
	@Resource ClientTransactionDAO clientTransactionDAO;

	private final Logger LOG = LogManager.getLogger(this.getClass());
		
	/** 
	 * Retrieves the ungrouped credit memeos from the external system that have been updated
	 * between the specified from and to dates.
	 * 
	 * @param Date from start date
	 * @param Date to end date 
	 * @return List of maps, each containing identifiers to the ungrouped credit memo transactions 
	*/
	@Override
	public List<Map<String, Object>> findUpdatedUngroupedCreditMemos(Date from, Date to) throws Exception {
		return creditMemoSuiteAnalyticsService.findUpdatedUngroupedCreditMemos(from, to);
	}

	/** 
	 * Retrieves the credit memeos from the external system that have been appled to an invoice group
	 * 
	 * @param clientTransactionGroupVO Invoice group
	 * @return List of ClientCreditMemoVO that have been applied to the invoice group
	*/	
	@Override
	public List<ClientCreditMemoVO> findByTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception {
		List<Map<String, Object>> creditMemoMaps = creditMemoSuiteAnalyticsService.findByCustomerAndAccountingPeriod(clientTransactionGroupVO.getClientInternalId(), clientTransactionGroupVO.getAccountingPeriodId().toString());

		return creditMemoMaps.stream()
		       .map(creditMemoMap -> {
				   return new ClientCreditMemoVO()
				           .setInternalId((String)creditMemoMap.get(ClientCreditMemoFieldEnum.INTERNAL_ID.getScriptId()))
						   .setExternalId((String)creditMemoMap.get(ClientCreditMemoFieldEnum.EXTERNAL_ID.getScriptId()))
						   .setTranId((String)creditMemoMap.get(ClientCreditMemoFieldEnum.TRAN_ID.getScriptId())); })
			   .collect(Collectors.toList());
	}

	/**
	 * Process a list of client's credit memo updates
	 * 
	 * @param clientCreditMemoVOs List of updated client's credit memos
	 */
	@Override
	public void process(List<ClientCreditMemoVO> clientCreditMemoVOs) throws Exception {
		for(ClientCreditMemoVO clientCreditMemoVO : clientCreditMemoVOs) {
			process(clientCreditMemoVO);
		}
	}

	/**
	 * Proccess the client's credit memo update(s)
	 * 
	 * @param clientCreditMemoVOs contains the updated credit memo detail
	 */
	@Override
	public void process(ClientCreditMemoVO clientCreditMemoVO) throws Exception {	
		String jsonClientCreditMemo = null;
							
		String transactionNumber = clientCreditMemoVO.getTransactionNumber();
		clientCreditMemoVO = creditMemoSuiteTalkService.get(clientCreditMemoVO.getInternalId(), clientCreditMemoVO.getExternalId());
		clientCreditMemoVO.setTransactionNumber(transactionNumber); //Transaction number is not available via the API, next release may have it
			
		jsonClientCreditMemo = new ObjectMapper().setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME)).writerWithDefaultPrettyPrinter().writeValueAsString(clientCreditMemoVO);

		if(!clientCreditMemoVO.isGrouped()
		        || (clientCreditMemoVO.isGrouped() && StringUtils.hasText(clientCreditMemoVO.getGroupNumber()))) {
			clientTransactionDAO.processCreditMemo(clientCreditMemoVO);
		} else {
			LOG.warn("Skipping an updated to a client credit memo that is set to be grouped, but does not a group number. {}", jsonClientCreditMemo);
		}
		
		LOG.info("Processed Client's credit memeo {}", jsonClientCreditMemo);		
	}

}

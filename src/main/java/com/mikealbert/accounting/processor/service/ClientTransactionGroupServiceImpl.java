package com.mikealbert.accounting.processor.service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.client.suiteanalytics.InvoiceSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.enumeration.ClientTransactionGroupFieldEnum;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.util.data.DateUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("clientTransactionGroupService")
public class ClientTransactionGroupServiceImpl extends BaseService implements ClientTransactionGroupService {
	@Resource InvoiceSuiteAnalyticsService invoiceSuiteAnalyticsService;
	@Resource ClientTransactionDAO clientTransactionDAO;
	@Resource MessageLogService messageLogService;
	@Resource TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	@Resource AccountingPeriodService accountingPeriodService;

	@Value("${mafs.accounts.receivable.email}")
	private String emailTo;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());	

	/** 
	 * Retrieves the update clients' transaction groups from the external system. 
	 * 
	 * @param Date from start date
	 * @param Date to end date 
	 * @return List of maps, each containing the transaction groups
	*/
	@Override
	public List<ClientTransactionGroupVO> getUpdates(Date from, Date to) throws Exception {
		return convertToTransactionGroupVOs(transactionSuiteAnalyticsService.findUpdatedClientTransactionGroups(from, to));
	}	

	/**
	 * Processes clients' transaction groups from the external system.
	 * 
	 * @param clientTransactionGroup identifiers to the clients' transaction group that will be processed 
	 */	
	@Override
	public void process(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception {
		String jsonClientTransactionGroup = null;
							
		jsonClientTransactionGroup = new ObjectMapper().setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME)).writerWithDefaultPrettyPrinter().writeValueAsString(clientTransactionGroupVO);

		clientTransactionDAO.processTransactionGroup(clientTransactionGroupVO);

		LOG.info("Processed Client Invoice Group {}", jsonClientTransactionGroup);
	}

	/**
	 * Invokes Dist Complete. At the time of writing, this should be called only when all the invoice groups have been processed.
	 * 
	 */
	@Override
	public void complete() throws Exception {
		clientTransactionDAO.processTransactionGroupComplete();
	}

	/**
	 * Send an email to accounting notifying them that the distribution of the group invoices is complete
	 * 
	 * @param accountingPeriods List of accounting periods that wherein all grouped invoices have been distributed
	 */
	@Override
	public void emailComplete(List<String> accountingPeriods) throws Exception {
		for(String accountingPeriod : accountingPeriods) {			
			String endDate = DateUtil.convertToString(accountingPeriodService.get(accountingPeriod).getEnd(), DateUtil.PATTERN_YEAR_MONTH);
			super.sendtextEmail(
					emailTo, 
					"Group Invoice Distribution Completed", 
					String.format("Distribution has been completed on all invoices for the period of %s.", endDate) );
		}
	}

	/**
	 * Determines based on a period of time whether the internal invoice grouping processing has been completed.
	 * When an update is made to a message log end date, it is retrieved in addition to all message logs within the same accounting period.
	 * Each message log is checked for the presence of an end date. If all message logs within the same accounting preriod have an end date, 
	 * then the internal invoice grouping process has completed. Otherwise, the process has not been completed. 
	 * 
	 * @param from the start of the time window
	 * @param to the end of the time window
	 * 
	 * @return true when end date exists for all message log within the accounting period, otherwise, false.
	 */
	@Override
	public boolean hasInternalGroupingBeenCompleted(Date from, Date to) throws Exception {
		boolean isComplete = false;

		List<String> accountingPeriods = distinctAccountingPeriods(messageLogService.find(EventEnum.GROUP_TRANSACTION, from, to));

		if(accountingPeriods.size() > 1) throw new Exception(String.format("Invoice grouping completed within too many accounting periods %s", accountingPeriods));

		List<MessageLog> messageLogs = accountingPeriods.stream()
		        .map(accountingPeriod -> messageLogService.findWithPartialMessageId(EventEnum.GROUP_TRANSACTION, accountingPeriod))
				.flatMap(Collection::stream)
				.filter(messageLog -> messageLog.getEndDate() == null)				
				.collect(Collectors.toList());
		     
		if(!accountingPeriods.isEmpty() && messageLogs.isEmpty()) isComplete =  true;

		return isComplete;
	}

	/**
	 * Gets the accounting periods of the completed internal groups between dates.
	 * 
	 * @param from the start of the time window
	 * @param to the end of the time window
	 * 
	 * @return Distinct list of accounting periods
	 */
	@Override
	public List<String> completedInternalGroupAccountingPeriods(Date from, Date to) throws Exception {
		return distinctAccountingPeriods(messageLogService.find(EventEnum.GROUP_TRANSACTION, from, to));
	}

	@Override
	public List<String> formatMessageIds(List<ClientTransactionGroupVO> clientTransactionGroupVOs) {
		return clientTransactionGroupVOs.stream()
		        .map(clientTransactionGroupVO -> formatMessageId(clientTransactionGroupVO))
				.collect(Collectors.toList());
	}

	@Override
	public String formatMessageId(ClientTransactionGroupVO clientTransactionGroupVO) {
		return String.format("%s|%s", clientTransactionGroupVO.getAccountingPeriodId(), clientTransactionGroupVO.getClientExternalId());
	}

	@Override
	public List<ClientTransactionGroupVO> convertToTransactionGroupVOs(List<Map<String, Object>> clientTransactionGroupMaps) {
		return clientTransactionGroupMaps.stream()
		        .map(clientTransactionGroupMap -> convertToTransactionGroupVO(clientTransactionGroupMap))
				.collect(Collectors.toList());
	}

	@Override
	public ClientTransactionGroupVO convertToTransactionGroupVO(Map<String, Object> clientTransactionGroupMap) {
		return new ClientTransactionGroupVO()
				.setAccountingPeriodId((String)clientTransactionGroupMap.get(ClientTransactionGroupFieldEnum.ACCOUNTING_PERIOD_ID.getScriptId()))
				.setAccountingPeriodDate((Date)clientTransactionGroupMap.get(ClientTransactionGroupFieldEnum.ACCOUNTING_PERIOD_DATE.getScriptId()))
				.setClientInternalId((String)clientTransactionGroupMap.get(ClientTransactionGroupFieldEnum.CUSTOMER_INTERNAL_ID.getScriptId()))
				.setClientExternalId((String)clientTransactionGroupMap.get(ClientTransactionGroupFieldEnum.CUSTOMER_EXTERNAL_ID.getScriptId()))
				.setGroupNumber((String)clientTransactionGroupMap.get(ClientTransactionGroupFieldEnum.GROUP_NUMBER.getScriptId()));
	}

	@Override
	public List<Map<String, Object>> findGroupTransactions(String accountingPeriodId, String clientExternalId) throws Exception{
		return transactionSuiteAnalyticsService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(accountingPeriodId, clientExternalId);
	}

	@Override
	public List<Map<String, Object>> findGroupTransactions(String accountingPeriodId, String clientExternalId, boolean isGrouped) throws Exception{
		return transactionSuiteAnalyticsService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(accountingPeriodId, clientExternalId).stream()
		    .filter(transaction -> isGrouped ? transaction.get("group_number") != null : transaction.get("group_number") == null)
			.collect(Collectors.toList());
	}

	@Override
	public List<ClientTransactionGroupVO> findAllClientTransactiongGroupsByAccountingPeriod(List<String> accountingPeriodIds) throws Exception {
		return convertToTransactionGroupVOs(transactionSuiteAnalyticsService.findAllClientTransactionGroupsByAccountingPeriod(accountingPeriodIds));
	}

	private List<String> distinctAccountingPeriods(List<MessageLog> messageLogs) {
		return messageLogs.stream()
		        .map(messageLog -> messageLog.getMessageId().split("\\|")[0])
		        .distinct()
		        .collect(Collectors.toList());
	}

	@Override
	public List<String> distinctAccountingPeriodId(List<ClientTransactionGroupVO> clientTransactionGroupVOs) {
		return clientTransactionGroupVOs.stream()
		        .map(clientTransactionGroupVO -> clientTransactionGroupVO.getAccountingPeriodId())
				.distinct()
				.collect(Collectors.toList());
	}

}

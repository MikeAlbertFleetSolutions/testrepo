package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.client.suitetalk.AgingTransactionSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

import org.springframework.stereotype.Service;

@Service("agingTransactionService")
public class AgingTransactionServiceImpl extends BaseService implements AgingTransactionService {

	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource AgingTransactionSuiteTalkService agingTransactionSuiteTalkService;

	/**
	 * Retrieve the client's open AR transactions that have aged (days past the due/transaction date) into a specified period/bucket.
	 * 
	 * @param clientInternalId The accounting system's identifier for the client
	 * @param clientExternalId Our internal identifier for the client
	 * @param agingPeriod The aging period/bucket 
	 *  
	 * @return List of {@link ReceivableTransactionVO} within the client's aging period
	 */
	@Override
	public List<ReceivableTransactionVO<?, ?>> getAging(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) throws Exception {
		return agingTransactionSuiteTalkService.getAging(clientInternalId, clientExternalId, agingPeriod);
	}	

	/**
	 * Groups the receivable transactions by aging periods. The age of a transaction is determined by the number of days it is overdue, i.e. days
	 * past the due date. 
	 * 
	 * @param transactionVOs List of aging receivable transactions
	 * 
	 * @return Map of grouped transaction. The key will be the AgingPeriodEnum.
	 */
	@Override
	public Map<AgingPeriodEnum, List<ReceivableTransactionVO<?, ?>>> groupByAgingPeriod(List<ReceivableTransactionVO<?, ?>> transactionVOs) {
		return transactionVOs.stream()
				.collect(Collectors.groupingBy(transaction -> AgingPeriodEnum.getAgingPeriod(transaction.getDaysOverdue())));
	}

}

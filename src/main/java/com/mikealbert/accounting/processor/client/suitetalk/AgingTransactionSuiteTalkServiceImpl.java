package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.ClientInvoiceFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomBodyEnum;
import com.mikealbert.accounting.processor.enumeration.GroupInvoiceFieldEnum;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchRowBasic;
import com.netsuite.webservices.platform.common_2023_2.TransactionSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.TransactionSearchRowBasic;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDoubleCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchDateField;
import com.netsuite.webservices.platform.core_2023_2.SearchDoubleField;
import com.netsuite.webservices.platform.core_2023_2.SearchEnumMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchLongField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchRow;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchDateFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchDoubleFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchEnumMultiSelectFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchLongFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearch;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearchAdvanced;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearchRow;
import com.netsuite.webservices.transactions.sales_2023_2.types.TransactionStatus;
import com.netsuite.webservices.transactions.sales_2023_2.types.TransactionType;

@Service("agingTransactionSuiteTalkService")
public class AgingTransactionSuiteTalkServiceImpl extends BaseSuiteTalkService implements AgingTransactionSuiteTalkService {
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	/**
	 * Retrieve the client's AR transactions that have aged (past the due/transaction date) into a specific aging period.
	 * 
	 * @param clientInternalId The accounting system's identifier for the client
	 * @param clientExternalId MAFS identifier for the client
	 * @param accountingPeriod Aging periods/buckets 
	 * 
	 * @return List of {@link #ReceivableTransactionVO()} that are within the specified aging period
	 */	
	public List<ReceivableTransactionVO<?, ?>> getAging(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) throws Exception {
		List<ReceivableTransactionVO<?, ?>> invoiceTransactions = convertToReceivableTransactions(super.service.searchAdvancedX(convertToInvoiceTransactionSearch(clientInternalId, clientExternalId, agingPeriod)));
		List<ReceivableTransactionVO<?, ?>> creditAndPaymentTransactions = convertToReceivableTransactions(super.service.searchAdvancedX(convertToCreditAndPaymentTransactionSearch(clientInternalId, clientExternalId, agingPeriod)));	
		
		List<ReceivableTransactionVO<?, ?>> transactions = new ArrayList<>(0);
		transactions.addAll(invoiceTransactions);
		transactions.addAll(creditAndPaymentTransactions);

		return transactions;
	}	
	
	private TransactionSearchAdvanced convertToInvoiceTransactionSearch(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) {
		TransactionSearchAdvanced tsa = new TransactionSearchAdvanced();
		tsa.setSavedSearchScriptId("customsearch_ma_aging_transactions");

		TransactionSearch ts = new TransactionSearch();
		TransactionSearchBasic tsb = new TransactionSearchBasic();
		
		SearchEnumMultiSelectField transType = new SearchEnumMultiSelectField();
		transType.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		transType.setSearchValue(new String[] {TransactionType.__invoice});
		tsb.setType(transType);

		SearchEnumMultiSelectField statusSearchField = new SearchEnumMultiSelectField();
		statusSearchField.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		statusSearchField.setSearchValue(new String[]{TransactionStatus.__invoiceOpen});
		tsb.setStatus(statusSearchField);

		SearchMultiSelectField clientSearchField = new SearchMultiSelectField();
		clientSearchField.setOperator(SearchMultiSelectFieldOperator.anyOf);
		clientSearchField.setSearchValue(new RecordRef[]{new RecordRef(null, clientInternalId, clientExternalId, RecordType.customer)});
		tsb.setEntity(clientSearchField);
		
		SearchLongField daysOverdue = new SearchLongField();
		if(agingPeriod == null) {
			daysOverdue.setOperator(SearchLongFieldOperator.greaterThan);
			daysOverdue.setSearchValue(0L);
		} else {
			daysOverdue.setOperator(SearchLongFieldOperator.between);
			daysOverdue.setSearchValue(agingPeriod.getMin());
			daysOverdue.setSearchValue2(agingPeriod.getMax());			
		}
		tsb.setDaysOverdue(daysOverdue);
		
		ts.setBasic(tsb);
		tsa.setCriteria(ts);

		return tsa;
	}

	private TransactionSearchAdvanced convertToCreditAndPaymentTransactionSearch(String clientInternalId, String clientExternalId, AgingPeriodEnum agingPeriod) {
		TransactionSearchAdvanced tsa = new TransactionSearchAdvanced();
		tsa.setSavedSearchScriptId("customsearch_ma_aging_transactions");

		TransactionSearch ts = new TransactionSearch();
		TransactionSearchBasic tsb = new TransactionSearchBasic();

		SearchEnumMultiSelectField transType = new SearchEnumMultiSelectField();
		transType.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		transType.setSearchValue(new String[] {TransactionType.__creditMemo, TransactionType.__customerPayment});
		tsb.setType(transType);

		SearchEnumMultiSelectField statusSearchField = new SearchEnumMultiSelectField();
		statusSearchField.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		statusSearchField.setSearchValue(new String[]{TransactionStatus.__creditMemoOpen, TransactionStatus.__paymentDeposited, TransactionStatus.__paymentNotDeposited});
		tsb.setStatus(statusSearchField);

		SearchMultiSelectField clientSearchField = new SearchMultiSelectField();
		clientSearchField.setOperator(SearchMultiSelectFieldOperator.anyOf);
		clientSearchField.setSearchValue(new RecordRef[]{new RecordRef(null, clientInternalId, clientExternalId, RecordType.customer)});
		tsb.setEntity(clientSearchField);		

		SearchDoubleField applied = new SearchDoubleField();
		applied.setOperator(SearchDoubleFieldOperator.greaterThan);
		applied.setSearchValue(0D);
		tsb.setAmountRemaining(applied);

		Calendar start = Calendar.getInstance();		
		start.add(Calendar.DATE, -1 * agingPeriod.getMax().intValue());

		Calendar end = Calendar.getInstance();	
		end.add((Calendar.DATE), -1 * agingPeriod.getMin().intValue());	

	    SearchDateField tranDate = new SearchDateField();
	    tranDate.setOperator(SearchDateFieldOperator.within);
	    tranDate.setSearchValue(start);
		tranDate.setSearchValue2(end);
	    tsb.setTranDate(tranDate);

		ts.setBasic(tsb);
		tsa.setCriteria(ts);
		
		return tsa;
	}	

	private List<ReceivableTransactionVO<?, ?>> convertToReceivableTransactions(List<SearchRow> searchRows) throws Exception{
		List<ReceivableTransactionVO<?, ?>> transactionVOs = new ArrayList<>(0);

		for(SearchRow searchRow : searchRows) {
			ReceivableTransactionVO<?, ?> receivableTransactionVO = null;
			TransactionSearchRow row = (TransactionSearchRow)searchRow;
			TransactionSearchRowBasic basic = row.getBasic();

			String internalId = basic.getInternalId()[0].getSearchValue().getInternalId();
			String externalId = basic.getExternalId() == null ? null : basic.getExternalId()[0].getSearchValue().getExternalId();			
			String type = basic.getType()[0].getSearchValue();
			Date tranDate = basic.getTranDate()[0].getSearchValue().getTime();
			String tranId = basic.getTranId()[0].getSearchValue();
			String groupNumber = basic.getCustomFieldList() == null ? null : super.getSearchColumnCustomFieldValue(basic.getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_NUMBER);
			Date dueDate = basic.getDueDate() == null ? null : basic.getDueDate()[0].getSearchValue().getTime();
			String memo = basic.getMemoMain() == null ? null : basic.getMemoMain()[0].getSearchValue();
			BigDecimal invoicedTotal = parseInvoicedTotal(row, GroupInvoiceFieldEnum.INVOICED_TOTAL);
			BigDecimal applied = basic.getAmountPaid() == null ? BigDecimal.ZERO : new BigDecimal(basic.getAmountPaid()[0].getSearchValue());
			BigDecimal balance = basic.getAmountRemaining() == null ? BigDecimal.ZERO : new BigDecimal(basic.getAmountRemaining()[0].getSearchValue());
			String clientInternalId = row.getCustomerMainJoin().getInternalId()[0].getSearchValue().getInternalId();
			String clientExternalId = row.getCustomerMainJoin().getExternalId()[0].getSearchValue().getExternalId();
			Long daysOverdue = basic.getDaysOverdue() ==  null ? calculateDaysOverdue(tranDate) : basic.getDaysOverdue()[0].getSearchValue();
			Date asOfDate = new Date();
			String maType = basic.getCustomFieldList() == null ? null : super.getSearchColumnCustomFieldValue(basic.getCustomFieldList(), ClientInvoiceFieldEnum.MA_TYPE);
		
			switch(type) {
				case TransactionType.__invoice:
				    receivableTransactionVO = new ClientInvoiceVO(internalId, externalId)
							.setType(TransactionTypeEnum.CLIENT_INVOICE)					
							.setTranDate(tranDate)
							.setTranId(DataUtil.nvl(groupNumber, tranId))
							.setGroupNumber(groupNumber)
							.setMaType(maType)							
							.setDueDate(dueDate)
							.setMemo(memo)
							.setTotal(invoicedTotal)
							.setApplied(applied)
							.setBalance(balance)
							.setClientInternalId(clientInternalId)
							.setClientExternalId(clientExternalId)
							.setDaysOverdue(daysOverdue)
							.setAsOfDate(asOfDate);	
				    break;
				case TransactionType.__creditMemo:
					receivableTransactionVO = new ClientCreditMemoVO(internalId, externalId)
							.setType(TransactionTypeEnum.CREDIT_MEMO)					
							.setTranDate(tranDate)
							.setTranId(DataUtil.nvl(groupNumber, tranId))
							.setGroupNumber(groupNumber)
							.setMaType(maType)
							.setDueDate(dueDate)
							.setMemo(memo)
							.setTotal(invoicedTotal.abs().negate())
							.setApplied(applied)
							.setBalance(balance.abs().negate())
							.setClientInternalId(clientInternalId)
							.setClientExternalId(clientExternalId)
							.setDaysOverdue(daysOverdue)
							.setAsOfDate(asOfDate);	
					break;
				case TransactionType.__customerPayment:
					receivableTransactionVO = new ClientCreditMemoVO(internalId, externalId)
							.setType(TransactionTypeEnum.CLIENT_PAYMENT)					
							.setTranDate(tranDate)
							.setTranId(DataUtil.nvl(groupNumber, tranId))
							.setGroupNumber(groupNumber)
							.setMaType(maType)														
							.setDueDate(dueDate)
							.setMemo(memo == null ? "PAYMENT" : memo)
							.setTotal(invoicedTotal.abs().negate())
							.setApplied(applied)
							.setBalance(balance.abs().negate())
							.setClientInternalId(clientInternalId)
							.setClientExternalId(clientExternalId)
							.setDaysOverdue(daysOverdue)
							.setAsOfDate(asOfDate);
					break;
				default:
				   LOG.warn("Record transaction type {} is not supported", type);
			}

			transactionVOs.add(receivableTransactionVO);							
		}

		return transactionVOs;
	}		

	private Long calculateDaysOverdue(Date dueDate) throws Exception {
		Date now = DateUtil.now(DateUtil.PATTERN_DATE);
		Long daysOverdue = now.getTime() - dueDate.getTime();

		return TimeUnit.DAYS.convert(daysOverdue, TimeUnit.MILLISECONDS);
	}

	private BigDecimal parseInvoicedTotal(TransactionSearchRow tsr, GroupInvoiceFieldEnum groupInvoiceFieldEnum) {
		BigDecimal retVal = null;	

		if(tsr.getCustomSearchJoin() == null) return BigDecimal.ZERO;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(tsr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(CustomBodyEnum.GROUP_INVOICE_LINK.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {

			retVal = Arrays.asList(crsrb.getCustomFieldList().getCustomField()).stream()
				.filter(r -> r.getScriptId().equalsIgnoreCase(groupInvoiceFieldEnum.getScriptId()))
				.map(r ->  (SearchColumnDoubleCustomField)r)
				.map(r -> new BigDecimal(r.getSearchValue()))
				.findFirst()
				.orElse(BigDecimal.ZERO);
		}
			
		return retVal;
	}	
}
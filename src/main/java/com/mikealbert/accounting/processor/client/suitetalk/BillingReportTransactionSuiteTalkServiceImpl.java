package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.enumeration.ClientInvoiceFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomBodyEnum;
import com.mikealbert.accounting.processor.enumeration.GroupInvoiceFieldEnum;
import com.mikealbert.accounting.processor.enumeration.TransactionFieldEnum;
import com.mikealbert.accounting.processor.enumeration.UnitFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionAmountVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.SavedSearchEnum;
import com.netsuite.webservices.platform.common_2023_2.AccountingPeriodSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchRowBasic;
import com.netsuite.webservices.platform.common_2023_2.CustomerSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.TransactionSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDoubleCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnStringCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchRow;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearch;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearchAdvanced;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearchRow;
import com.netsuite.webservices.transactions.sales_2023_2.types.TransactionType;

@Service("billingReportTransactionSuiteTalkService")
public class BillingReportTransactionSuiteTalkServiceImpl extends BaseSuiteTalkService implements BillingReportTransactionSuiteTalkService {
	@Resource AccountingPeriodSuiteTalkService accountingPeriodSuiteTalkService;
	@Resource SuiteTalkCacheService suiteTalkCacheService;

	private final Logger LOG = LogManager.getLogger(this.getClass());	
	
	public List<BillingReportTransactionVO> get(String customerExternalId, List<String> accountingPeriodInternalIds, BillingReportTypeEnum reportType) throws Exception {
		try {
			List<SearchRow> response = super.service.searchAdvancedX(convertToTransactionSearchAdvanced(customerExternalId, accountingPeriodInternalIds));
			return convertToBillingReports(filterSupportedReport(filterGroupedAndUngrouped(response), reportType));
		} catch(Exception e) {
			throw new SuiteTalkException(String.format("customerExternalId=%s, accountingPeriodInternalIds=%s, mayType=%s", customerExternalId, accountingPeriodInternalIds, reportType), e);
		}
	}

	private TransactionSearchAdvanced convertToTransactionSearchAdvanced(String customerExternalId, List<String> accountingPeriodInternalIds) throws Exception{
		TransactionSearchAdvanced tsa = new TransactionSearchAdvanced();
		tsa.setSavedSearchScriptId(SavedSearchEnum.MA_CLIENT_TRANSACTION.getId());

		TransactionSearch ts = new TransactionSearch();

		ts.setAccountingPeriodJoin(new AccountingPeriodSearchBasic());
		if(accountingPeriodInternalIds.size() == 1) {
			ts.getAccountingPeriodJoin().setInternalId(new SearchMultiSelectField(new RecordRef[]{new RecordRef(null, accountingPeriodInternalIds.get(0), null, RecordType.accountingPeriod)}, SearchMultiSelectFieldOperator.anyOf)); 
		} else {
			ts.getAccountingPeriodJoin().setInternalId(new SearchMultiSelectField(convertToRecordRefArray(accountingPeriodInternalIds), SearchMultiSelectFieldOperator.anyOf)); 			
		}
		
		ts.setCustomerJoin(new CustomerSearchBasic());
		ts.getCustomerJoin().setExternalId(new SearchMultiSelectField(new RecordRef[]{new RecordRef(null, null, customerExternalId, RecordType.customer)}, SearchMultiSelectFieldOperator.anyOf));
		
		TransactionSearchBasic tsb = new TransactionSearchBasic();

		ts.setBasic(tsb);
		tsa.setCriteria(ts);

		return tsa;
	}

	private RecordRef[] convertToRecordRefArray(List<String> internalIds) {
		return internalIds.stream()
		    .map(intId -> new RecordRef(null, intId, null, RecordType.accountingPeriod))
			.toArray(RecordRef[]::new);
	}

	private List<BillingReportTransactionVO> convertToBillingReports(List<SearchRow> searchResult) {		
		return searchResult.stream().parallel()
		    .map(sr -> (TransactionSearchRow)sr)
			.map(tsr -> {
				return new BillingReportTransactionVO()
				    .setTranInternalId(tsr.getBasic().getInternalId()[0].getSearchValue().getInternalId())
					.setTranExternalId(tsr.getBasic().getExternalId() == null ? null : tsr.getBasic().getExternalId()[0].getSearchValue().getExternalId())
					.setMaTransactionDate(parseMaTransactionDate(tsr))
				    .setType(toTransactionTypeEnum(TransactionType.fromValue(tsr.getBasic().getType()[0].getSearchValue())))
					.setStatus(toTransactionStatusEnum(tsr))
					.setGrouped(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_NUMBER) != null ? true : false)
				    .setAccountCode(tsr.getCustomerMainJoin().getExternalId()[0].getSearchValue().getExternalId().replace(CustomerSuiteTalkService.CUSTOMER_EXTERNAL_ID_PREFIX, ""))					
				    .setAccountName(tsr.getCustomerMainJoin().getCompanyName()[0].getSearchValue())
					.setTranDate(parseGroupInvoiceFields(tsr, GroupInvoiceFieldEnum.CREATED_DATE) == null ? tsr.getBasic().getTranDate()[0].getSearchValue().getTime() : parseGroupInvoiceFields(tsr, GroupInvoiceFieldEnum.CREATED_DATE))
					.setDueDate(tsr.getBasic().getDueDate() == null ? null : tsr.getBasic().getDueDate()[0].getSearchValue().getTime())
					.setReportType(parseExpenseCategoryFields(tsr, TransactionFieldEnum.MA_TYPE) == null ? null : BillingReportTypeEnum.getByMaType(parseExpenseCategoryFields(tsr, TransactionFieldEnum.MA_TYPE)))
					.setDocId(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.MA_DOC_ID) == null ? null : Long.valueOf(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.MA_DOC_ID)))
					.setTransactionNumber(DataUtil.nvl(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_NUMBER), tsr.getBasic().getTransactionNumber()[0].getSearchValue()))
					.setLineId(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.MA_LINE_ID) == null ? null : Long.valueOf(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.MA_LINE_ID)))					
					.setLineNo(tsr.getBasic().getLineSequenceNumber()[0].getSearchValue())
					.setDescription(tsr.getBasic().getMemo() == null ? null : tsr.getBasic().getMemo()[0].getSearchValue())
					.setInvoiceNote(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.INVOICE_NOTE))
					.setExpenseCategory(parseExpenseCategoryFields(tsr, TransactionFieldEnum.REPORT_CATEGORY))
					.setExpenseSubCategory(parseExpenseCategoryFields(tsr, TransactionFieldEnum.REPORT_SUB_CATEGORY))
					.setAnalysisCodeDescription(parseExpenseCategoryFields(tsr, TransactionFieldEnum.LINE_DESCRIPTION))
					.setAccountingPeriod(tsr.getAccountingPeriodJoin().getPeriodName()[0].getSearchValue())
					.setMonthServiceDate(parseMonthServiceDate(tsr))
					.setQty(tsr.getBasic().getQuantity() == null ? null : tsr.getBasic().getQuantity()[0].getSearchValue())
					.setUnitInternalId(parseUnitInternalId(tsr))
					.setUnitExternalId(parseUnitExternalId(tsr))					
					.setUnit(parseUnitNo(tsr))
					.setUnitVin(parseUnitDetail(tsr, UnitFieldEnum.VIN))					
					.setUnitYear(parseUnitDetail(tsr, UnitFieldEnum.YEAR))
					.setUnitMake(parseUnitDetail(tsr, UnitFieldEnum.MAKE))
					.setUnitModel(parseUnitDetail(tsr, UnitFieldEnum.MODEL))
					.setDriverId(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_ID) == null ? null : Long.valueOf(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_ID)))
					.setDriverName(parseDriverName(tsr))
					.setDriverAddressState(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_STATE))
					.setDriverCostCenterCode(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_COST_CENTER))
					.setDriverCostCenterDescription(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_COST_CENTER_DESCRIPTION))
					.setRechargeCode(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_RECHARGE_CODE))
					.setFleetRefNo(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_FLEET_REF_NO))
					.setLinePaidAmount(adjustSign(tsr, parseCustomAmountField(tsr, TransactionFieldEnum.LINE_PAID_AMOUNT)))
					.setBaseNetAmount(new BillingReportTransactionAmountVO(
						    adjustSign(tsr, tsr.getBasic().getRate() == null || BigDecimal.valueOf(tsr.getBasic().getRate()[0].getSearchValue()).compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.valueOf(tsr.getBasic().getNetAmount()[0].getSearchValue()) : BigDecimal.valueOf(tsr.getBasic().getRate()[0].getSearchValue())), 
							adjustSign(tsr, parseTax(tsr)),
							adjustSign(tsr, BigDecimal.valueOf(tsr.getBasic().getNetAmount()[0].getSearchValue())).add(adjustSign(tsr, parseTax(tsr))))
					);
			})
			.collect(Collectors.toList());
	}

	private List<SearchRow> filterGroupedAndUngrouped(List<SearchRow> searchResult) {
		List<SearchRow> grouped = searchResult.stream().parallel()
				.map(tsr -> (TransactionSearchRow)tsr)
				.filter(tsr -> (DataUtil.convertToBoolean(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_INVOICE)) && (super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_NUMBER) != null)) ? true : false)
				.collect(Collectors.toList());

		List<SearchRow> ungrouped = searchResult.stream().parallel()
				.map(tsr -> (TransactionSearchRow)tsr)
				.filter(tsr -> !DataUtil.convertToBoolean(super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_INVOICE)))
				.collect(Collectors.toList());

		List<SearchRow> filteredSearchRows = new ArrayList<>(0);
		grouped.stream()
		        .forEach(sr -> filteredSearchRows.add(sr));
		ungrouped.stream()
		        .forEach(sr -> filteredSearchRows.add(sr));

		return filteredSearchRows;
	}

	private List<SearchRow> filterSupportedReport(List<SearchRow> searchResult, BillingReportTypeEnum reportType) {
		if(reportType == null) return searchResult;

		return searchResult.stream().parallel()
		    .map(tsr -> (TransactionSearchRow)tsr)
			.filter(tsr -> reportType.getMaType().equalsIgnoreCase(parseExpenseCategoryFields(tsr, TransactionFieldEnum.MA_TYPE)))
			.collect(Collectors.toList());
	} 
	
	private String parseUnitInternalId(TransactionSearchRow tsr) {
		if(tsr.getCustomSearchJoin() == null) return null;

		return  Arrays.asList(tsr.getCustomSearchJoin()).stream()
		    .filter(r -> r.getCustomizationRef().getScriptId().equals(TransactionFieldEnum.UNIT.getScriptId()))		
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.map(r -> r.getInternalId()[0].getSearchValue().getInternalId())
			.findFirst()
			.orElse(null);
	}	

	private String parseUnitExternalId(TransactionSearchRow tsr) {
		if(tsr.getCustomSearchJoin() == null) return null;
				
		return  Arrays.asList(tsr.getCustomSearchJoin()).stream()
		    .filter(r -> r.getCustomizationRef().getScriptId().equals(TransactionFieldEnum.UNIT.getScriptId()))		
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.map(r -> r.getExternalId()[0].getSearchValue().getExternalId())
			.findFirst()
			.orElse(null);
	}	

	private String parseUnitNo(TransactionSearchRow tsr) {
		String unitNo = null;		

		if(tsr.getCustomSearchJoin() == null) return unitNo;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(tsr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(TransactionFieldEnum.UNIT.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {
			unitNo = crsrb.getName()[0].getSearchValue();
		}
			
		return unitNo;
	}	

	private String parseUnitDetail(TransactionSearchRow tsr, UnitFieldEnum unitFieldEnum) {
		String retVal = null;	

		if(tsr.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(tsr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(TransactionFieldEnum.UNIT.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {

			retVal = Arrays.asList(crsrb.getCustomFieldList().getCustomField()).stream()
				.filter(r -> r.getScriptId().equalsIgnoreCase(unitFieldEnum.getScriptId()))
				.map(r ->  (SearchColumnStringCustomField)r)
				.map(r -> r.getSearchValue())
				.findFirst()
				.orElse(null);
		}
			
		return retVal;
	}
	
	private String parseExpenseCategoryFields(TransactionSearchRow tsr, TransactionFieldEnum transactionFieldEnum) {
		String retVal = null;	

		if(tsr.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(tsr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(TransactionFieldEnum.INVOICE_DESCRIPTION.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {

			if(transactionFieldEnum == TransactionFieldEnum.LINE_DESCRIPTION) {
				retVal = crsrb.getName()[0].getSearchValue();
			} else {
				retVal = Arrays.asList(crsrb.getCustomFieldList().getCustomField()).stream()
				.filter(r -> r.getScriptId().equalsIgnoreCase(transactionFieldEnum.getScriptId()))
				.map(r ->  (SearchColumnStringCustomField)r)
				.map(r -> r.getSearchValue())
				.findFirst()
				.orElse(null);				
			}

		}
			
		return retVal;
	}	

	private Date parseGroupInvoiceFields(TransactionSearchRow tsr, GroupInvoiceFieldEnum groupInvoiceFieldEnum) {
		Date retVal = null;	

		if(tsr.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(tsr.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(CustomBodyEnum.GROUP_INVOICE_LINK.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

		if(crsrb != null) {

			retVal = Arrays.asList(crsrb.getCustomFieldList().getCustomField()).stream()
				.filter(r -> r.getScriptId().equalsIgnoreCase(groupInvoiceFieldEnum.getScriptId()))
				.map(r ->  (SearchColumnDateCustomField)r)
				.map(r -> r.getSearchValue().getTime())
				.findFirst()
				.orElse(null);
		}
			
		return retVal;
	}	

	private Date parseMonthServiceDate(TransactionSearchRow tsr) {
		Date retVal = null;
		String date = null;

		try {
			date = super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList() , TransactionFieldEnum.MONTH_SERVICE_DATE);
			if(date != null) {
				retVal = DateUtil.convertToDate(date, DateUtil.PATTERN_DATE);
			}
		
		} catch(Exception e) {
			LOG.error("Error converting Month/Service date from the external accounting system", e);
		}

		return retVal;
	}

	private Date parseMaTransactionDate(TransactionSearchRow tsr) {
		Date retVal = null;
		String date = null;

		try {
			date = super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList() , TransactionFieldEnum.MA_TRANSACTION_DATE);
			if(date != null) {
				retVal = DateUtil.convertToDate(date, DateUtil.PATTERN_DATE);
			}
		
		} catch(Exception e) {
			LOG.error("Error parsing MA Transaction Date from the external accounting system", e);
		}

		return retVal;
	}	

	private BigDecimal parseCustomAmountField(TransactionSearchRow tsr, TransactionFieldEnum customColumnEnum) {
		return Arrays.asList(tsr.getBasic().getCustomFieldList().getCustomField()).stream()
		  .filter(r -> r.getScriptId().equalsIgnoreCase(customColumnEnum.getScriptId()))
		  .map(r -> (SearchColumnDoubleCustomField)r)
		  .map(r -> r.getSearchValue())
		  .map(d -> BigDecimal.valueOf(d))
		  .findFirst()
		  .orElse(BigDecimal.ZERO);
	}

	private BigDecimal adjustSign(TransactionSearchRow tsr, BigDecimal amount) {
		Double qty = tsr.getBasic().getQuantity()[0].getSearchValue();
		BigDecimal retAmount;

		if(TransactionType.fromValue(tsr.getBasic().getType()[0].getSearchValue()).equals(TransactionType._creditMemo)) {
			retAmount = amount.compareTo(BigDecimal.ZERO) < 0  ? amount : amount.negate();
		} else {
			retAmount = qty.compareTo(0D) < 0  ? amount.abs() : amount;
		}

		return retAmount;
	}

	private BigDecimal parseTax(TransactionSearchRow tsr) {
		return tsr.getBasic().getTaxAmount() ==  null ? BigDecimal.ZERO : BigDecimal.valueOf(tsr.getBasic().getTaxAmount()[0].getSearchValue());
	}

	private TransactionTypeEnum toTransactionTypeEnum(TransactionType transactionType) {
		TransactionTypeEnum txnTypeEnum = null;
		switch(transactionType.getValue()) {
			case TransactionType.__invoice:
			    txnTypeEnum = TransactionTypeEnum.CLIENT_INVOICE;
				break;
			case TransactionType.__creditMemo:
			    txnTypeEnum = TransactionTypeEnum.CREDIT_MEMO;
				break;
			case TransactionType.__depositApplication:
			    txnTypeEnum = TransactionTypeEnum.DEPOSIT_APPLICATION;
				break;
			default:
			   txnTypeEnum = null;
		}
		return txnTypeEnum;
	}

	private TransactionStatusEnum toTransactionStatusEnum(TransactionSearchRow tsr) {
		TransactionStatusEnum retVal;
		String status = tsr.getBasic().getStatus()[0].getSearchValue();

		switch(status) {
			case "applied":
			case "fullyApplied":
				retVal = TransactionStatusEnum.FULLY_APPLIED;
				break;
			case "open":
				retVal = TransactionStatusEnum.OPEN;
				break;
			case "paidInFull":
				retVal = TransactionStatusEnum.PAID_IN_FULL;
				break;
			default:
			    LOG.warn(String.format("Transaction Status %s is not mapped", status));
				retVal = null;
				break;
		}

		return retVal;
	}

	private String parseDriverName(TransactionSearchRow tsr) {
		String firstName = super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_FIRST_NAME);
		String lastName = super.getSearchColumnCustomFieldValue(tsr.getBasic().getCustomFieldList(), TransactionFieldEnum.DRIVER_LAST_NAME);
		String name = null;

		if(firstName != null && lastName != null) {
			name = String.format("%s, %s", lastName, firstName.substring(0, 1));
		} else if(lastName != null) {
			name = lastName;
		} else {
			name = firstName;
		}
		
		return name;
	}

}
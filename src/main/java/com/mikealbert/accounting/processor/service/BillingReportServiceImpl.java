package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.BillingReportTransactionSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientBillingTransactionDAO;
import com.mikealbert.accounting.processor.entity.ClientBillingTransaction;
import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.BillingReportRefreshMessageVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionAmountVO;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.mikealbert.constant.enumeration.MonthEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;

@Service("billingReportService")
public class BillingReportServiceImpl extends BaseService implements BillingReportService {
	@Resource BillingReportTransactionSuiteTalkService billingReportTransactionSuiteTalkService;
	@Resource ClientBillingTransactionDAO clientBillingTransactionDAO;
	@Resource TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	@Resource ServiceCache serviceCache;

	private final Logger LOG = LogManager.getLogger(this.getClass());	

	public List<BillingReportTransactionVO> get(String accountCode, List<AccountingPeriodVO> periods, BillingReportTypeEnum reportType) throws Exception {
		List<BillingReportTransactionVO> transactions = billingReportTransactionSuiteTalkService.get("1C" + accountCode, convertToInternalIds(periods), reportType);

		return transactions.stream().parallel()
			.map(txn -> transformFields(txn))
			.map(txn -> calcNet(txn))
			.map(txn -> calcApplied(txn))
			.map(txn -> calcSubTotal(txn))
			.map(txn -> populateOrigin(txn))
			.map(txn -> transformDescription(txn))
			.map(txn -> {
					try{
						return transformMaintenanceMonthServiceDate(txn);
					}catch(Exception e){
						throw new RuntimeException(e);
					}
				})
			.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void upsertInternalStore(List<BillingReportTransactionVO> billingReportTransactionVOs, boolean force) throws Exception {
		List<ClientBillingTransaction> cbts = billingReportTransactionVOs.stream().parallel()
		    .map(txn -> toClientBillingTransaction(txn, force))
			.collect(Collectors.toList());
		
			clientBillingTransactionDAO.saveAll(cbts);
	}
	
	@Transactional
	@Override
	public void mergeInternalStore(String accountCode, List<AccountingPeriodVO> periods, boolean force) throws Exception {
		periods.stream()
	            .forEach(p -> {
					try {
						clientBillingTransactionDAO.mergeInternalData(accountCode, p.getName().replace(" ", "-").toUpperCase(), force);
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});
	}	

	@Transactional
	@Override
	public void deleteFromInternalStore(String accountCode, List<AccountingPeriodVO> periods) throws Exception {
		List<ClientBillingTransaction> txnsToBeDeleted = periods.stream()
		    .map(p -> clientBillingTransactionDAO.findByAccountCodeAndAccountingPeriod(accountCode, p.getName().toUpperCase().replace(" ", "-")))
			.flatMap(Collection::stream)
			.collect(Collectors.toList());

		if(txnsToBeDeleted != null) {
			clientBillingTransactionDAO.deleteAll(txnsToBeDeleted);
		}		
	}

	@Override
	public List<BillingReportRefreshMessageVO> findAndDispatchUpdates(Date base, Date from, Date to) throws Exception {
		List<BillingReportRefreshMessageVO> messages = new ArrayList<>(0);
		
		List<BillingReportRefreshMessageVO> ungroupedUpdates = transactionSuiteAnalyticsService.findUpdatedUngroupedClientBillingTransactions(base, from, to).stream() 
		    .map(r -> {
				return new BillingReportRefreshMessageVO()
				        .setAccountCode(( (String)r.get("customer_external_id")).replace("1C", ""))
						.setStartPeriod(((String)r.get("period_name")).replace(" ", "-").toUpperCase())
						.setEndPeriod(((String)r.get("period_name")).replace(" ", "-").toUpperCase())
						.setReportName(null);
			})
			.collect(Collectors.toList());

			
		List<BillingReportRefreshMessageVO> groupedUpdates = transactionSuiteAnalyticsService.findUpdatedGroupedClientBillingTransactions(base, from, to).stream()
		    .map(r -> {
				return new BillingReportRefreshMessageVO()
				        .setAccountCode(( (String)r.get("customer_external_id")).replace("1C", ""))
						.setStartPeriod(((String)r.get("period_name")).replace(" ", "-").toUpperCase())
						.setEndPeriod(((String)r.get("period_name")).replace(" ", "-").toUpperCase())
						.setReportName(null);
			})
			.collect(Collectors.toList());

		messages.addAll(ungroupedUpdates);
		messages.addAll(groupedUpdates);

		return messages.stream()
				.distinct()
				.collect(Collectors.toList());
	}

	private ClientBillingTransaction toClientBillingTransaction(BillingReportTransactionVO billingReportTransactionVO, boolean force) {
		ClientBillingTransaction cbt = clientBillingTransactionDAO.findByTranIntIdAndLineNo(billingReportTransactionVO.getTranInternalId(), billingReportTransactionVO.getLineNo())
		    .orElse(new ClientBillingTransaction());
         
		if(cbt.getCbtId() == null || force || !DataUtil.convertToBoolean(cbt.getLockYN())) {
			
			cbt = cbt
		            .setTranIntId(billingReportTransactionVO.getTranInternalId())
		            .setTranExtId(billingReportTransactionVO.getTranExternalId())
		            .setAcountName(billingReportTransactionVO.getAccountName())
		            .setInvoiceDate(billingReportTransactionVO.getTranDate())
		            .setInvoiceDueDate(billingReportTransactionVO.getDueDate())
		            .setInvoiceType(initCap(billingReportTransactionVO.getReportType().name()))
		            .setReportName(billingReportTransactionVO.getReportType().getReportName())
		            .setAnalysisCodeDesc(billingReportTransactionVO.getAnalysisCodeDescription())
		            .setReportCategory(billingReportTransactionVO.getExpenseCategory())
		            .setReportSubCategory(billingReportTransactionVO.getExpenseSubCategory())
		            .setCostcenter(billingReportTransactionVO.getDriverCostCenterCode())
		            .setCostcenterDescription(billingReportTransactionVO.getDriverCostCenterDescription())
		            .setInvoiceNo(billingReportTransactionVO.getTransactionNumber())
		            .setLineId(billingReportTransactionVO.getLineId())
		            .setLineNo(billingReportTransactionVO.getLineNo())
		            .setUnitNo(billingReportTransactionVO.getUnit())
		            .setFleetRef(billingReportTransactionVO.getFleetRefNo())
		            .setDriverName(billingReportTransactionVO.getDriverName())
		            .setRechargeCode(billingReportTransactionVO.getRechargeCode())
		            .setModelYear(billingReportTransactionVO.getUnitYear())
		            .setMake(billingReportTransactionVO.getUnitMake())
		            .setModel(billingReportTransactionVO.getUnitModel())
		            .setPoNumber(billingReportTransactionVO.getPoTransactionNumber())
		            .setLicenseNO(null)
		            .setServiceCenter(null)
		            .setMileage(null)
		            .setMaintTaskQty(billingReportTransactionVO.getQty() == null ? null : billingReportTransactionVO.getQty())
		            .setMaintCatCode(null)
		            .setMaintCode(null)
		            .setAccountingPeriod(billingReportTransactionVO.getAccountingPeriod().replace(" ", "-").toUpperCase())					
		            .setBillingPeriod(billingReportTransactionVO.getMonthServiceDate())
		            .setLineAmount(billingReportTransactionVO.getNetAmount().getAmount())
		            .setLineTax(billingReportTransactionVO.getNetAmount().getTax())
		            .setLineTotal(billingReportTransactionVO.getNetAmount().getGross())
		            .setAllocAmtNet(billingReportTransactionVO.getAppliedAmount().getAmount())
		            .setAllocAmtTax(billingReportTransactionVO.getAppliedAmount().getTax())
		            .setAllocAmtGross(billingReportTransactionVO.getAppliedAmount().getGross())
		            .setTotAmtNet(billingReportTransactionVO.getGrossAmount().getAmount())
		            .setTotAmtTax(billingReportTransactionVO.getGrossAmount().getTax())
		            .setTotAmtGross(billingReportTransactionVO.getGrossAmount().getGross())
		            .setFromDate(billingReportTransactionVO.getSearchFrom())
		            .setToDate(billingReportTransactionVO.getSearchTo())
		            .setFmsId(billingReportTransactionVO.getUnitExternalId() == null ? null : Long.valueOf(billingReportTransactionVO.getUnitExternalId()))
		            .setDrvId(billingReportTransactionVO.getDriverId())
		            .setAccountCode(billingReportTransactionVO.getAccountCode())
		            .setDocId(billingReportTransactionVO.getDocId())
		            .setClientPoNumber(null)
		            .setVin(billingReportTransactionVO.getUnitVin())
		            .setLineNarrative(null)
					.setLockYN("N")
					.setOrigin(billingReportTransactionVO.getOrigin().name())
					.setLineDescription(billingReportTransactionVO.getDescription())
					.setDriverState(billingReportTransactionVO.getDriverAddressState());		
		} 

		return cbt;
	}	

	private List<String> convertToInternalIds(List<AccountingPeriodVO> accountingPeriodVOs) {
		return accountingPeriodVOs.stream()
		   .map(a -> a.getInternalId())
		   .collect(Collectors.toList());
	}

	private BillingReportTransactionVO transformFields(BillingReportTransactionVO billingReportTransactionVO) {
	 	return billingReportTransactionVO
			.setAccountingPeriod(billingReportTransactionVO.getAccountingPeriod().replace(" ", "-"))			
			.setQty(billingReportTransactionVO.getReportType().equals(BillingReportTypeEnum.MAINTENANCE) ? Math.abs(billingReportTransactionVO.getQty()) : null);
	 }

	private String initCap(String value) {
		String retVal = value;

		if(value == null || value.isBlank() || value.isEmpty()) return retVal;

		return StringUtils.capitalize(value.toLowerCase());
	}

	private BillingReportTransactionVO calcNet(BillingReportTransactionVO billingReportTransactionVO) {
		BigDecimal amount = billingReportTransactionVO.getBaseNetAmount().getAmount();
		BigDecimal tax = billingReportTransactionVO.getBaseNetAmount().getTax();
		BigDecimal gross = billingReportTransactionVO.getBaseNetAmount().getGross();
	
		return billingReportTransactionVO.setNetAmount(new BillingReportTransactionAmountVO(amount, tax, gross));
	}

	private BillingReportTransactionVO calcApplied(BillingReportTransactionVO billingReportTransactionVO) {
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal gross = BigDecimal.ZERO;

		//Added this condition to handle the case where the line paid amount has been specified, see LAFS-10385
		if(billingReportTransactionVO.getType() == TransactionTypeEnum.CLIENT_INVOICE
				&& billingReportTransactionVO.getLinePaidAmount() != null) {
			amount = billingReportTransactionVO.getLinePaidAmount().negate();
			tax = BigDecimal.ZERO;
			gross = amount.add(tax);
		}

		if(!billingReportTransactionVO.isGrouped()
				&& billingReportTransactionVO.getType() == TransactionTypeEnum.CREDIT_MEMO
				&& billingReportTransactionVO.getStatus() == TransactionStatusEnum.FULLY_APPLIED ) {
			amount = billingReportTransactionVO.getBaseNetAmount().getAmount().abs();
			tax = billingReportTransactionVO.getBaseNetAmount().getTax().abs();
			gross = amount.add(tax);
		}

		return billingReportTransactionVO.setAppliedAmount(new BillingReportTransactionAmountVO(amount, tax, gross));
	}

	private BillingReportTransactionVO calcSubTotal(BillingReportTransactionVO billingReportTransactionVO) {
		BigDecimal amount = billingReportTransactionVO.getBaseNetAmount().getAmount().add(billingReportTransactionVO.getAppliedAmount().getAmount());
		BigDecimal tax = billingReportTransactionVO.getBaseNetAmount().getTax().add(billingReportTransactionVO.getAppliedAmount().getTax());
		BigDecimal gross = billingReportTransactionVO.getBaseNetAmount().getGross().add(billingReportTransactionVO.getAppliedAmount().getGross());

		//LAFS-10350 The invoice detail report expects the amound and tax to be base and not net.
		if(billingReportTransactionVO.getType() == TransactionTypeEnum.CLIENT_INVOICE
				&& billingReportTransactionVO.getLinePaidAmount() != null) {
			amount = billingReportTransactionVO.getBaseNetAmount().getAmount();
			tax =  billingReportTransactionVO.getBaseNetAmount().getTax();
			gross = amount.add(tax);	

		}	

		//LAFS-10350 The reports cherry pick the fields and some perform their own calculations. For this scenario we need to setthe gross amount to the net.
		if(!billingReportTransactionVO.isGrouped() 
				&& billingReportTransactionVO.getType() == TransactionTypeEnum.CREDIT_MEMO
				&& billingReportTransactionVO.getStatus() == TransactionStatusEnum.FULLY_APPLIED ) {
			amount = billingReportTransactionVO.getBaseNetAmount().getAmount();
			tax = billingReportTransactionVO.getBaseNetAmount().getTax();
			gross = amount.add(tax);
		}		

		return billingReportTransactionVO.setGrossAmount(new BillingReportTransactionAmountVO(amount, tax, gross));
	}
	
	public void validate(List<BillingReportTransactionVO> billingReportTransactionVOs, boolean suppress) {
		billingReportTransactionVOs.stream().parallel()
				.forEach(txn -> {
					try{
						super.validate(txn);
					} catch(Exception e) {
						if(suppress) {
						    LOG.warn(e.getMessage());
						} else {
						    throw new RuntimeException(e);
						}
					}
				});
	}

	private BillingReportTransactionVO populateOrigin(BillingReportTransactionVO billingReportTransactionVO) {
		if(billingReportTransactionVO.getTranExternalId() != null) { 
			billingReportTransactionVO.setOrigin(ApplicationEnum.WILLOW);
		} else {
			billingReportTransactionVO.setOrigin(ApplicationEnum.NETSUITE);
		}
		
		return billingReportTransactionVO;
	}	
	
	private BillingReportTransactionVO transformMaintenanceMonthServiceDate(BillingReportTransactionVO billingReportTransactionVO) throws Exception {
		if(billingReportTransactionVO.getReportType() != BillingReportTypeEnum.MAINTENANCE) { return billingReportTransactionVO; }

		String delimiter = billingReportTransactionVO.getAccountingPeriod().contains("-") ? "-" : " ";
		String[] monthYear = billingReportTransactionVO.getAccountingPeriod().split(delimiter);
		String yearMonth = String.format("%s-%s", monthYear[1], MonthEnum.getMonthByShortName(monthYear[0]).getPosition());
		
		return billingReportTransactionVO.setMonthServiceDate(DateUtil.convertToDate(yearMonth, DateUtil.PATTERN_YEAR_MONTH));
	}

	private BillingReportTransactionVO transformDescription(BillingReportTransactionVO billingReportTransactionVO) {
		String description = null;
		
		if(billingReportTransactionVO.getAnalysisCodeDescription() == null) {
			description = billingReportTransactionVO.getDescription();			
		} else {
			description = billingReportTransactionVO.getAnalysisCodeDescription();
		}

		description = description == null ? "" : description;

		if(billingReportTransactionVO.getInvoiceNote() != null) {
			description += " " + billingReportTransactionVO.getInvoiceNote();
		}

		billingReportTransactionVO.setDescription(description.trim());

		return billingReportTransactionVO;
	}

	@Override
	public List<BillingReportTransactionVO> filterReportWorthy(String accountCode, List<BillingReportTransactionVO> billingReportTransactionVOs) throws Exception {
		List<BillingReportTransactionVO> result = new ArrayList<>();		

		Map<AccountingPeriodVO, List<BillingReportTransactionVO>> periodTxnMap = billingReportTransactionVOs.stream()
				.collect(Collectors.groupingBy(txn -> { 
						try {
							return serviceCache.findAccountingPeriodByNameRange(txn.getAccountingPeriod(), txn.getAccountingPeriod()).stream().findAny().orElseThrow();
						} catch(Exception e) {
							throw new RuntimeException(e);
						}
				}));


		for(Map.Entry<AccountingPeriodVO, List<BillingReportTransactionVO>> entry : periodTxnMap.entrySet()) {
			boolean isAllTxnGrouped = transactionSuiteAnalyticsService.isGroupInvoiceDone(entry.getKey().getInternalId(), "", "1C" + accountCode);

			result.addAll(
					entry.getValue().stream()
					.filter(txn -> (isAllTxnGrouped && txn.isGrouped()) || !txn.isGrouped() ? true : false)
					.collect(Collectors.toList())
			);			
		}

		return result;
	}


}

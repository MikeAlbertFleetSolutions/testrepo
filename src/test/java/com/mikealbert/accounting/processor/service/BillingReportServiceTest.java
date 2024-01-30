package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.AccountingPeriodSuiteTalkService;
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
import com.mikealbert.util.data.DateUtil;

@SpringBootTest
@DisplayName("Given a request")
public class BillingReportServiceTest extends BaseTest{
	@Resource BillingReportService billingReportService;

	@MockBean BillingReportTransactionSuiteTalkService billingReportTransactionSuiteTalkService;
	@MockBean AccountingPeriodSuiteTalkService accountingPeriodSuiteTalkService;
	@MockBean ClientBillingTransactionDAO clientBillingTransactionDAO;
	@MockBean TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	@MockBean ServiceCache serviceCache;

	@BeforeEach
	void up() throws Exception {}
	
	@Test
	@DisplayName("when request is to retrieve a client's billing report within a specific billing period, then the correct parameters are passed to the method that returns a grouped Open invoice transaction for the given client and period")
	public void testGetGroupedOpenInvoice() throws Exception {
		final String ACCOUNT_CODE = "nnnnnnnn";		
		final String CUSTOMER_EXT_ID = "1C" + ACCOUNT_CODE;
		final String ACCOUNTING_PERIOD_NAME = "Jan-2023";
		final String ACCOUNTING_PERIOD_INTERNAL_ID = "0";
		final List<String> ACCOUNTING_PERIOD_INTERNAL_IDs = Arrays.asList(new String[]{"0"});		
		final Date TRAN_DATE = new Date();
		final String DESCRIPTION = "DESCRIPTION";
		final String INVOICE_NOTE = "INVOICE_NOTE";

		final Date MONTH_SERVICE_DATE = DateUtil.convertToDate("2023-01", DateUtil.PATTERN_YEAR_MONTH);
		final BillingReportTypeEnum REPORT_TYPE = BillingReportTypeEnum.MAINTENANCE;

		final BillingReportTransactionAmountVO EXPECTED_BASE_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(1.00D), new BigDecimal(1.00D), new BigDecimal(2.00D));
		final BillingReportTransactionAmountVO EXPECTED_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(1.00D), new BigDecimal(1.00D), new BigDecimal(2.00D));
		final BillingReportTransactionAmountVO EXPECTED_APPLIED_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), BigDecimal.ZERO, new BigDecimal(-1.00D));
		final BillingReportTransactionAmountVO EXPECTED_GROSS_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(1.00D), new BigDecimal(1.00D), new BigDecimal(2.00D));

		final String EXPECTED_DESCRIPTION = DESCRIPTION + " " + INVOICE_NOTE;
		
		List<AccountingPeriodVO> accountingPeriodVOs = new ArrayList<>(0);
		AccountingPeriodVO accountingPeriodVO = new AccountingPeriodVO().setName(ACCOUNTING_PERIOD_NAME).setInternalId(ACCOUNTING_PERIOD_INTERNAL_ID);
		accountingPeriodVOs.add(accountingPeriodVO);

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setTranDate(TRAN_DATE)
			    .setType(TransactionTypeEnum.CLIENT_INVOICE)
				.setStatus(TransactionStatusEnum.OPEN)
				.setGrouped(true)
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setDescription(DESCRIPTION)
				.setInvoiceNote(INVOICE_NOTE)
				.setMonthServiceDate(MONTH_SERVICE_DATE)
				.setQty(-1D) 
				.setBaseNetAmount(EXPECTED_BASE_NET_AMOUNT)
				.setLineId(1L)
				.setLineNo(2L)
				.setLinePaidAmount(EXPECTED_APPLIED_AMOUNT.getGross().abs()) );

		when(billingReportTransactionSuiteTalkService.get(any(), any(), any())).thenReturn(mockBillingReportTransactionVOs);

		List<BillingReportTransactionVO> actualBillingReportTransactionVOs =  billingReportService.get(ACCOUNT_CODE, accountingPeriodVOs, REPORT_TYPE);

		verify(billingReportTransactionSuiteTalkService, times(1)).get(eq(CUSTOMER_EXT_ID), eq(ACCOUNTING_PERIOD_INTERNAL_IDs), eq(REPORT_TYPE));

		assertEquals(ACCOUNTING_PERIOD_NAME, actualBillingReportTransactionVOs.get(0).getAccountingPeriod());
		assertEquals(actualBillingReportTransactionVOs.get(0).getLineId(), actualBillingReportTransactionVOs.get(0).getLineId());
		assertEquals(1D, actualBillingReportTransactionVOs.get(0).getQty());
		assertEquals(EXPECTED_BASE_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getBaseNetAmount());
		assertEquals(EXPECTED_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getNetAmount());
		assertEquals(EXPECTED_APPLIED_AMOUNT, actualBillingReportTransactionVOs.get(0).getAppliedAmount());				
		assertEquals(EXPECTED_GROSS_AMOUNT, actualBillingReportTransactionVOs.get(0).getGrossAmount());
		assertEquals(MONTH_SERVICE_DATE, actualBillingReportTransactionVOs.get(0).getMonthServiceDate());
		assertEquals(EXPECTED_DESCRIPTION, actualBillingReportTransactionVOs.get(0).getDescription());
	}	

	@Test
	@DisplayName("when request is to retrieve a client's billing report within a specific billing period, then the correct parameters are passed to the method that returns an ungrouped current Fully Applied Credit Memo transaction that is for the given client and period")
	public void testGetFullyAppliedUngroupedCredit() throws Exception {
		final String ACCOUNT_CODE = "nnnnnnnn";		
		final String CUSTOMER_EXT_ID = "1C" + ACCOUNT_CODE;
		final String ACCOUNTING_PERIOD_NAME = "Jan-2023";
		final String ACCOUNTING_PERIOD_INTERNAL_ID = "0";
		final List<String> ACCOUNTING_PERIOD_INTERNAL_IDs = Arrays.asList(new String[]{"0"});		
		final Double QTY = 1D;
		final Date TRAN_DATE = new Date();
		final Date MONTH_SERVICE_DATE = DateUtil.convertToDate("2023-01", DateUtil.PATTERN_YEAR_MONTH);
		final BillingReportTypeEnum REPORT_TYPE = BillingReportTypeEnum.MAINTENANCE;

		final BillingReportTransactionAmountVO EXPECTED_BASE_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), new BigDecimal(-1.00D), new BigDecimal(-2.00D));
		final BillingReportTransactionAmountVO EXPECTED_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), new BigDecimal(-1.00D), new BigDecimal(-2.00D));
		final BillingReportTransactionAmountVO EXPECTED_APPLIED_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(1.00D), new BigDecimal(1.00D), new BigDecimal(2.00D));
		final BillingReportTransactionAmountVO EXPECTED_GROSS_AMOUNT = EXPECTED_BASE_NET_AMOUNT;

		List<AccountingPeriodVO> accountingPeriodVOs = new ArrayList<>(0);
		AccountingPeriodVO accountingPeriodVO = new AccountingPeriodVO().setName(ACCOUNTING_PERIOD_NAME).setInternalId(ACCOUNTING_PERIOD_INTERNAL_ID);
		accountingPeriodVOs.add(accountingPeriodVO);

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setTranDate(TRAN_DATE)
			    .setType(TransactionTypeEnum.CREDIT_MEMO)
				.setStatus(TransactionStatusEnum.FULLY_APPLIED)
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setGrouped(false)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setMonthServiceDate(MONTH_SERVICE_DATE)
				.setQty(-1D) 
				.setBaseNetAmount(EXPECTED_BASE_NET_AMOUNT)
				.setLineId(1L)
				.setLineNo(2L) );

		when(billingReportTransactionSuiteTalkService.get(any(), any(), any())).thenReturn(mockBillingReportTransactionVOs);

		List<BillingReportTransactionVO> actualBillingReportTransactionVOs =  billingReportService.get(ACCOUNT_CODE, accountingPeriodVOs, REPORT_TYPE);

		verify(billingReportTransactionSuiteTalkService, times(1)).get(eq(CUSTOMER_EXT_ID), eq(ACCOUNTING_PERIOD_INTERNAL_IDs), eq(REPORT_TYPE));

		assertEquals(ACCOUNTING_PERIOD_NAME, actualBillingReportTransactionVOs.get(0).getAccountingPeriod());
		assertEquals(actualBillingReportTransactionVOs.get(0).getLineId(), actualBillingReportTransactionVOs.get(0).getLineId());
		assertEquals(QTY, actualBillingReportTransactionVOs.get(0).getQty());
		assertEquals(EXPECTED_BASE_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getBaseNetAmount());
		assertEquals(EXPECTED_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getNetAmount());
		assertEquals(EXPECTED_APPLIED_AMOUNT, actualBillingReportTransactionVOs.get(0).getAppliedAmount());				
		assertEquals(EXPECTED_GROSS_AMOUNT, actualBillingReportTransactionVOs.get(0).getGrossAmount());
		assertEquals(MONTH_SERVICE_DATE, actualBillingReportTransactionVOs.get(0).getMonthServiceDate());
	}	

	@Test
	@DisplayName("when request is to retrieve a client's billing report within a specific billing period, then the correct parameters are passed to the method that returns a Partial Applied  (Open) Credit Memo transaction for the given client and period")
	public void testGetPartiallyAppliedCredit() throws Exception {
		final String ACCOUNT_CODE = "nnnnnnnn";		
		final String CUSTOMER_EXT_ID = "1C" + ACCOUNT_CODE;
		final String ACCOUNTING_PERIOD_NAME = "Jan-2023";
		final String ACCOUNTING_PERIOD_INTERNAL_ID = "0";
		final List<String> ACCOUNTING_PERIOD_INTERNAL_IDs = Arrays.asList(new String[]{"0"});		
		final Double QTY = 1D;
		final Date TRAN_DATE = new Date();
		final Date MONTH_SERVICE_DATE = DateUtil.convertToDate("2023-01", DateUtil.PATTERN_YEAR_MONTH);		
		final BillingReportTypeEnum REPORT_TYPE = BillingReportTypeEnum.MAINTENANCE;

		final BillingReportTransactionAmountVO EXPECTED_BASE_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), new BigDecimal(-1.00D), new BigDecimal(-2.00D));
		final BillingReportTransactionAmountVO EXPECTED_NET_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), new BigDecimal(-1.00D), new BigDecimal(-2.00D));
		final BillingReportTransactionAmountVO EXPECTED_APPLIED_AMOUNT = new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		final BillingReportTransactionAmountVO EXPECTED_GROSS_AMOUNT = new BillingReportTransactionAmountVO(new BigDecimal(-1.00D), new BigDecimal(-1.00D), new BigDecimal(-2.00D));

		List<AccountingPeriodVO> accountingPeriodVOs = new ArrayList<>(0);
		AccountingPeriodVO accountingPeriodVO = new AccountingPeriodVO().setName(ACCOUNTING_PERIOD_NAME).setInternalId(ACCOUNTING_PERIOD_INTERNAL_ID);
		accountingPeriodVOs.add(accountingPeriodVO);

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setTranDate(TRAN_DATE)
			    .setType(TransactionTypeEnum.CREDIT_MEMO)
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setStatus(TransactionStatusEnum.OPEN)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setMonthServiceDate(MONTH_SERVICE_DATE)
				.setQty(-1D) 
				.setBaseNetAmount(EXPECTED_BASE_NET_AMOUNT)
				.setLineId(1L)
				.setLineNo(2L) );

		when(billingReportTransactionSuiteTalkService.get(any(), any(), any())).thenReturn(mockBillingReportTransactionVOs);

		List<BillingReportTransactionVO> actualBillingReportTransactionVOs =  billingReportService.get(ACCOUNT_CODE, accountingPeriodVOs, REPORT_TYPE);

		verify(billingReportTransactionSuiteTalkService, times(1)).get(eq(CUSTOMER_EXT_ID), eq(ACCOUNTING_PERIOD_INTERNAL_IDs), eq(REPORT_TYPE));

		assertEquals(ACCOUNTING_PERIOD_NAME, actualBillingReportTransactionVOs.get(0).getAccountingPeriod());
		assertEquals(actualBillingReportTransactionVOs.get(0).getLineId(), actualBillingReportTransactionVOs.get(0).getLineId());
		assertEquals(QTY, actualBillingReportTransactionVOs.get(0).getQty());
		assertEquals(EXPECTED_BASE_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getBaseNetAmount());
		assertEquals(EXPECTED_NET_AMOUNT, actualBillingReportTransactionVOs.get(0).getNetAmount());
		assertEquals(EXPECTED_APPLIED_AMOUNT, actualBillingReportTransactionVOs.get(0).getAppliedAmount());				
		assertEquals(EXPECTED_GROSS_AMOUNT, actualBillingReportTransactionVOs.get(0).getGrossAmount());
		assertEquals(MONTH_SERVICE_DATE, actualBillingReportTransactionVOs.get(0).getMonthServiceDate());
	}	
	
	@Test
	@DisplayName("when request is to create a new billing transaction into the internal store, then the transaction is persisted to the DB")
	public void testUpsertInternalStoreCreate() throws Exception {
		final String ACCOUNT_CODE = "00000000";
		final String ACCOUNTING_PERIOD_NAME = "JAN-2022";
		final String INVOICE_TYPE = "Maintenance";
		final String REPORT_NAME = "FLINR501";
		final String TRANS_INT_ID = "0";
		final Long LINE_NO = 1L;
		final String LOCK_YN = "N";
		final String DRIVER_STATE="OH";

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setOrigin(ApplicationEnum.WILLOW)
			    .setTranInternalId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setAccountCode(ACCOUNT_CODE)
			    //.setReportName(INVOICE_TYPE)  
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setQty(1D) 
				.setBaseNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setAppliedAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setGrossAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)) 
				.setDriverAddressState(DRIVER_STATE));

		List<ClientBillingTransaction> mockClientBillingTransactions = new ArrayList<>(0);
		ClientBillingTransaction mockClientBillingTransaction = new ClientBillingTransaction()
		        .setOrigin(ApplicationEnum.WILLOW.name())
		        .setTranIntId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setInvoiceType(INVOICE_TYPE)
				.setReportName(REPORT_NAME)
			    .setAccountCode(ACCOUNT_CODE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace(" ", "-")) 
				.setMaintTaskQty(1D) 
				.setLineAmount(BigDecimal.ZERO)
				.setLineTax(BigDecimal.ZERO)
				.setLineTotal(BigDecimal.ZERO)
				.setAllocAmtNet(BigDecimal.ZERO)
				.setAllocAmtTax(BigDecimal.ZERO)
				.setAllocAmtGross(BigDecimal.ZERO)
				.setTotAmtNet(BigDecimal.ZERO)
				.setTotAmtTax(BigDecimal.ZERO)
				.setTotAmtGross(BigDecimal.ZERO)
				.setLockYN(LOCK_YN)
				.setDriverState(DRIVER_STATE);
				
		mockClientBillingTransactions.add(mockClientBillingTransaction);

		when(clientBillingTransactionDAO.findByTranIntIdAndLineNo(any(), any())).thenReturn(Optional.empty());
		when(clientBillingTransactionDAO.saveAll(any())).thenReturn(null);

		billingReportService.upsertInternalStore(mockBillingReportTransactionVOs, true);

		verify(clientBillingTransactionDAO, times(1)).findByTranIntIdAndLineNo(TRANS_INT_ID, LINE_NO);
		verify(clientBillingTransactionDAO, times(1)).saveAll(eq(mockClientBillingTransactions));
		
	}

	@Test
	@DisplayName("when request is to update an unlocked billing transaction in the internal store, then the transaction in the internal store is updated")
	public void testUpsertInternalStoreUpsertUnlocked() throws Exception {
		final String ACCOUNT_CODE = "00000000";
		final String ACCOUNTING_PERIOD_NAME = "JAN-2022";
		final String INVOICE_TYPE = "Maintenance";
		final String REPORT_NAME = "FLINR501";
		final String TRANS_INT_ID = "0";
		final Long LINE_NO = 1L;
		final String LOCK_YN = "N";
		final String DRIVER_STATE="OH";

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setOrigin(ApplicationEnum.WILLOW)
			    .setTranInternalId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setAccountCode(ACCOUNT_CODE)
			    //.setReportName(INVOICE_TYPE)  
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setQty(1D) 
				.setBaseNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setAppliedAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setGrossAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setDriverAddressState(DRIVER_STATE) ); 


		List<ClientBillingTransaction> mockClientBillingTransactions = new ArrayList<>(0);
		ClientBillingTransaction mockClientBillingTransaction = new ClientBillingTransaction()
		        .setOrigin(ApplicationEnum.WILLOW.name())
		        .setTranIntId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setInvoiceType(INVOICE_TYPE)
				.setReportName(REPORT_NAME)
			    .setAccountCode(ACCOUNT_CODE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace(" ", "-")) 
				.setMaintTaskQty(1D) 
				.setLineAmount(BigDecimal.ZERO)
				.setLineTax(BigDecimal.ZERO)
				.setLineTotal(BigDecimal.ZERO)
				.setAllocAmtNet(BigDecimal.ZERO)
				.setAllocAmtTax(BigDecimal.ZERO)
				.setAllocAmtGross(BigDecimal.ZERO)
				.setTotAmtNet(BigDecimal.ZERO)
				.setTotAmtTax(BigDecimal.ZERO)
				.setTotAmtGross(BigDecimal.ZERO)
				.setLockYN(LOCK_YN)
				.setDriverState(DRIVER_STATE);
		mockClientBillingTransactions.add(mockClientBillingTransaction);

		when(clientBillingTransactionDAO.findByTranIntIdAndLineNo(any(), any())).thenReturn(Optional.of(mockClientBillingTransaction));
		when(clientBillingTransactionDAO.saveAll(any())).thenReturn(null);

		billingReportService.upsertInternalStore(mockBillingReportTransactionVOs, false);

		verify(clientBillingTransactionDAO, times(1)).findByTranIntIdAndLineNo(TRANS_INT_ID, LINE_NO);
		verify(clientBillingTransactionDAO, times(1)).saveAll(eq(mockClientBillingTransactions));
		
	}

	@Test
	@DisplayName("when request is to force update a locked billing transaction in the internal store, then the transaction in the internal store is updated")
	public void testUpsertInternalStoreUpserForceLocked() throws Exception {
		final String ACCOUNT_CODE = "00000000";
		final String ACCOUNTING_PERIOD_NAME = "JAN-2022";
		final String INVOICE_TYPE = "Maintenance";
		final String REPORT_NAME = "FLINR501";
		final String TRANS_INT_ID = "0";
		final Long LINE_NO = 1L;
		final String LOCK_YN = "Y";
		final String DRIVER_STATE="OH";

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setOrigin(ApplicationEnum.WILLOW)
			    .setTranInternalId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setAccountCode(ACCOUNT_CODE)
			    //.setReportName(INVOICE_TYPE)  
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setQty(1D) 
				.setBaseNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setAppliedAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setGrossAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setDriverAddressState(DRIVER_STATE) ); 


		List<ClientBillingTransaction> mockClientBillingTransactions = new ArrayList<>(0);
		ClientBillingTransaction mockClientBillingTransaction = new ClientBillingTransaction()
		        .setOrigin(ApplicationEnum.WILLOW.name())
		        .setTranIntId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setInvoiceType(INVOICE_TYPE)
				.setReportName(REPORT_NAME)
			    .setAccountCode(ACCOUNT_CODE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace(" ", "-")) 
				.setMaintTaskQty(1D) 
				.setLineAmount(BigDecimal.ZERO)
				.setLineTax(BigDecimal.ZERO)
				.setLineTotal(BigDecimal.ZERO)
				.setAllocAmtNet(BigDecimal.ZERO)
				.setAllocAmtTax(BigDecimal.ZERO)
				.setAllocAmtGross(BigDecimal.ZERO)
				.setTotAmtNet(BigDecimal.ZERO)
				.setTotAmtTax(BigDecimal.ZERO)
				.setTotAmtGross(BigDecimal.ZERO)
				.setLockYN(LOCK_YN)
				.setDriverState(DRIVER_STATE);
		mockClientBillingTransactions.add(mockClientBillingTransaction);

		when(clientBillingTransactionDAO.findByTranIntIdAndLineNo(any(), any())).thenReturn(Optional.of(mockClientBillingTransaction));
		when(clientBillingTransactionDAO.saveAll(any())).thenReturn(null);

		billingReportService.upsertInternalStore(mockBillingReportTransactionVOs, true);

		verify(clientBillingTransactionDAO, times(1)).findByTranIntIdAndLineNo(TRANS_INT_ID, LINE_NO);
		verify(clientBillingTransactionDAO, times(1)).saveAll(eq(mockClientBillingTransactions));
		
	}	

	@Test
	@DisplayName("when request is to update a locked billing transaction in the internal store, then the transaction is not updated")
	public void testUpsertInternalStoreUpserLocked() throws Exception {
		final String ACCOUNT_CODE = "00000000";
		final String ACCOUNTING_PERIOD_NAME = "JAN-2022";
		final String INVOICE_TYPE = "Maintenance";
		final String REPORT_NAME = "FLINR501";
		final String TRANS_INT_ID = "0";
		final Long LINE_NO = 1L;
		final String LOCK_YN = "Y";
		final String DRIVER_STATE="OH";

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>();
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setOrigin(ApplicationEnum.NETSUITE)
			    .setTranInternalId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setAccountCode(ACCOUNT_CODE)
			    //.setReportName(INVOICE_TYPE)  
				.setReportType(BillingReportTypeEnum.MAINTENANCE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace("-", " "))
				.setQty(1D) 
				.setBaseNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setNetAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setAppliedAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setGrossAmount(new BillingReportTransactionAmountVO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
				.setDriverAddressState(DRIVER_STATE) ); 


		List<ClientBillingTransaction> mockClientBillingTransactions = new ArrayList<>(0);
		ClientBillingTransaction mockClientBillingTransaction = new ClientBillingTransaction()
		        .setTranIntId(TRANS_INT_ID)
				.setLineNo(LINE_NO)
			    .setInvoiceType(INVOICE_TYPE)
				.setReportName(REPORT_NAME)
			    .setAccountCode(ACCOUNT_CODE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace(" ", "-")) 
				.setMaintTaskQty(1D) 
				.setLineAmount(BigDecimal.ZERO)
				.setLineTax(BigDecimal.ZERO)
				.setLineTotal(BigDecimal.ZERO)
				.setAllocAmtNet(BigDecimal.ZERO)
				.setAllocAmtTax(BigDecimal.ZERO)
				.setAllocAmtGross(BigDecimal.ZERO)
				.setTotAmtNet(BigDecimal.ZERO)
				.setTotAmtTax(BigDecimal.ZERO)
				.setTotAmtGross(BigDecimal.ZERO)
				.setLockYN(LOCK_YN)
				.setDriverState(DRIVER_STATE);
		mockClientBillingTransactions.add(mockClientBillingTransaction);

		when(clientBillingTransactionDAO.findByTranIntIdAndLineNo(any(), any())).thenReturn(Optional.of(mockClientBillingTransaction));
		when(clientBillingTransactionDAO.saveAll(any())).thenReturn(null);

		billingReportService.upsertInternalStore(mockBillingReportTransactionVOs, false);

		verify(clientBillingTransactionDAO, times(1)).findByTranIntIdAndLineNo(TRANS_INT_ID, LINE_NO);
		verify(clientBillingTransactionDAO, times(1)).saveAll(any());
		
	}		

	@Test
	@DisplayName("when request is to delete client billing transactions from internal store base on account and accounting periods, then only the matching transactions are deleted")
	public void testDeleteFromInternalStore() throws Exception {
		final String ACCOUNT_CODE = "00000000";
		final String ACCOUNTING_PERIOND_INTERNAL_ID = "1";
		final String ACCOUNTING_PERIOD_NAME = "JAN-2022";
		final String INVOICE_TYPE = "FLINR501";

		List<AccountingPeriodVO> mockAccountingPeriodVOs = new ArrayList<>(0);
		mockAccountingPeriodVOs.add(
			new AccountingPeriodVO()
			    .setInternalId(ACCOUNTING_PERIOND_INTERNAL_ID)
				.setName(ACCOUNTING_PERIOD_NAME) );

		List<ClientBillingTransaction> mockClientBillingTransactions = new ArrayList<>(0);
		mockClientBillingTransactions.add(
			new ClientBillingTransaction()
			    .setInvoiceType(INVOICE_TYPE)
			    .setAccountCode(ACCOUNT_CODE)
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME.replace(" ", "-")) );

		when(clientBillingTransactionDAO.findByAccountCodeAndAccountingPeriod(any(), any())).thenReturn(mockClientBillingTransactions);
		doNothing().when(clientBillingTransactionDAO).deleteAll(any());

		billingReportService.deleteFromInternalStore(ACCOUNT_CODE, mockAccountingPeriodVOs);

		verify(clientBillingTransactionDAO, times(1)).findByAccountCodeAndAccountingPeriod(eq(ACCOUNT_CODE), eq(ACCOUNTING_PERIOD_NAME));
		verify(clientBillingTransactionDAO, times(1)).deleteAll(eq(mockClientBillingTransactions));
		
	}	

	@Test
	@DisplayName("when request is for a list of updated client billing periods within a window of time, then the matching list is returned")
	public void testFindUpdates() throws Exception {
		final String CUSTOMER_EXT_ID_KEY = "customer_external_id";
		final String CUSTOMER_EXT_ID_VAL = "1C00000000";		
		final String PERIOD_NAME_KEY = "period_name";
		final String PERIOD_NAME_VAL  = "Jan 2022";
		final Date BASE = new Date();
		final Date FROM = new Date();
		final Date TO = new Date();
		final String EXPECTED_ACCOUNT_CODE = "00000000";
		final String EXPECTED_BILLING_PERIOD  = "JAN-2022";		

		Map<String, Object> mockUpdate = new HashMap<>();
		mockUpdate.put(CUSTOMER_EXT_ID_KEY, CUSTOMER_EXT_ID_VAL);
		mockUpdate.put(PERIOD_NAME_KEY, PERIOD_NAME_VAL);		

		List<Map<String, Object>> mockUpdates = new ArrayList<>(0);
		mockUpdates.add(mockUpdate);

		when(transactionSuiteAnalyticsService.findUpdatedUngroupedClientBillingTransactions(any(), any(), any())).thenReturn(mockUpdates);
		when(transactionSuiteAnalyticsService.findUpdatedGroupedClientBillingTransactions(any(), any(), any())).thenReturn(mockUpdates);		

		List<BillingReportRefreshMessageVO> result = billingReportService.findAndDispatchUpdates(BASE, FROM, TO);

		verify(transactionSuiteAnalyticsService, times(1)).findUpdatedUngroupedClientBillingTransactions(eq(BASE), eq(FROM), eq(TO));
		verify(transactionSuiteAnalyticsService, times(1)).findUpdatedGroupedClientBillingTransactions(eq(BASE), eq(FROM), eq(TO));		

		assertEquals(1, result.size());
		assertEquals(EXPECTED_ACCOUNT_CODE, result.get(0).getAccountCode());
		assertEquals(EXPECTED_BILLING_PERIOD, result.get(0).getStartPeriod());
		assertEquals(EXPECTED_BILLING_PERIOD, result.get(0).getEndPeriod());		
	}

	@Test
	@DisplayName("when request is to filter transactions that a ready for reporting when grouping is completed, then the grouped and ungrouped transactions are returned")
	public void testFilterReportWorthyGroupingCompleted() throws Exception {
		final String ACCOUNTNG_PERIOD_INTERNAL_ID = "1";
		final String ACCOUNTING_PERIOD_NAME = "Jan 2022";
		final String ACCOUNT_CODE = "00000000";
		final boolean GROUPING_COMPLETED = true;

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>(0);
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setGrouped(false) );
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
				.setGrouped(true) );
				
		List<AccountingPeriodVO> mockAccountingPeriodVOs = new ArrayList<>(0);
		mockAccountingPeriodVOs.add(
			new AccountingPeriodVO()
				.setInternalId(ACCOUNTNG_PERIOD_INTERNAL_ID)
				.setName(ACCOUNTING_PERIOD_NAME) );

		when(transactionSuiteAnalyticsService.isGroupInvoiceDone(any(), any(), any())).thenReturn(GROUPING_COMPLETED);
		when(serviceCache.findAccountingPeriodByNameRange(any(), any())).thenReturn(mockAccountingPeriodVOs);

		List<BillingReportTransactionVO> result = billingReportService.filterReportWorthy(ACCOUNT_CODE, mockBillingReportTransactionVOs);

		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("when request is to filter transactions that a ready for reporting when grouping is not completed, then only the ungrouped transactions are returned")
	public void testFilterReportWorthyGroupingNotCompleted() throws Exception {
		final String ACCOUNTNG_PERIOD_INTERNAL_ID = "1";
		final String ACCOUNTING_PERIOD_NAME = "Jan 2022";
		final String ACCOUNT_CODE = "00000000";
		final boolean GROUPING_COMPLETED = false;

		List<BillingReportTransactionVO> mockBillingReportTransactionVOs = new ArrayList<>(0);
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
			    .setAccountingPeriod(ACCOUNTING_PERIOD_NAME)
				.setAccountCode(ACCOUNT_CODE)
			    .setGrouped(false) );
		mockBillingReportTransactionVOs.add(
			new BillingReportTransactionVO()
				.setAccountingPeriod(ACCOUNTING_PERIOD_NAME)			
				.setAccountCode(ACCOUNT_CODE)			
				.setGrouped(true) );
				
		List<AccountingPeriodVO> mockAccountingPeriodVOs = new ArrayList<>(0);
		mockAccountingPeriodVOs.add(
			new AccountingPeriodVO()
			    .setInternalId(ACCOUNTNG_PERIOD_INTERNAL_ID)
				.setName(ACCOUNTING_PERIOD_NAME) );

		when(transactionSuiteAnalyticsService.isGroupInvoiceDone(any(), any(), any())).thenReturn(GROUPING_COMPLETED);
		when(serviceCache.findAccountingPeriodByNameRange(any(), any())).thenReturn(mockAccountingPeriodVOs);

		List<BillingReportTransactionVO> result = billingReportService.filterReportWorthy(ACCOUNT_CODE, mockBillingReportTransactionVOs);

		assertEquals(1, result.size());
		assertEquals(mockBillingReportTransactionVOs.get(0), result.get(0));
	}	
}

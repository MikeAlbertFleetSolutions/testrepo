package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.enumeration.ApplicationEnum;

@SpringBootTest
@DisplayName("Given a billing report transaction")
public class BillingReportTransactionValidatorTest {

    @Resource Validator validator;

	static final Date BILLING_PERIOD = new Date();   
	static final Long DRIVER_ID = 1L;
	static final String DRIVER_STATE = "OH";
	static final String DRIVER_NAME = "John Doe";
	static final String ANALYSIS_CODE_DESC = "Recharge Equipment";
	static final String EXPENSE_CATEGORY = "Recharge";
	static final String EXPENSE_SUB_CATEGORY = "Recharge ya";
	static final String UNIT_NO = "00000000";    

	@Test
	@DisplayName("When validating a GOOD tranaction, no exception is raised")
	public void testValidate() {

		// final Long DRIVER_ID = 1L;
		// final String DRIVER_STATE = "OH";
		// final String DRIVER_NAME = "John Doe";
		
		// BillingReportTransactionVO netsuiteTxnWithValidExpenseData = new BillingReportTransactionVO()
		// 	    .setMonthServiceDate(BILLING_PERIOD)
		// 		.setReportType(BillingReportTypeEnum.MISCELLANEOUS)
		// 		.setAnalysisCodeDescription(ANALYSIS_CODE_DESC)
		// 		.setExpenseCategory(EXPENSE_CATEGORY)
		// 		.setExpenseSubCategory(EXPENSE_SUB_CATEGORY)
		// 		.setOrigin(ApplicationEnum.NETSUITE)
		// 		.setDriverId(DRIVER_ID)
		// 		.setDriverAddressState(DRIVER_STATE)
		// 		.setDriverName(DRIVER_NAME);

		// BillingReportTransactionVO willowTxnWithValidData = new BillingReportTransactionVO()
		// 	    .setMonthServiceDate(BILLING_PERIOD)
		// 		.setReportType(BillingReportTypeEnum.MISCELLANEOUS)
		// 		.setDocId(-1L)
		// 		.setLineId(-2L)
		// 		.setOrigin(ApplicationEnum.WILLOW)
		// 		.setDriverId(DRIVER_ID)
		// 		.setDriverAddressState(DRIVER_STATE)
		// 		.setDriverName(DRIVER_NAME);				

		BillingReportTransactionVO netsuiteTxnWithValidExpenseData = validTransaction(ApplicationEnum.NETSUITE);
		BillingReportTransactionVO willowTxnWithValidData = validTransaction(ApplicationEnum.WILLOW);

        assertEquals(0, validator.validate(netsuiteTxnWithValidExpenseData).size());	
        assertEquals(0, validator.validate(willowTxnWithValidData).size());			
	}

	@Test
	@DisplayName("When validating a violating tranaction, an exception is reaised")
	public void testValidateWithException() {
		final String EXPENSE_CATEGORY = "Rent";	
		final String EXPENSE_SUB_CATEGORY = "Monthly Rental";
		final String ANALYSIS_CODE_DESC = "Monthly Rental";


		BillingReportTransactionVO nullBIllingPeriod = validTransaction(ApplicationEnum.NETSUITE)
				.setMonthServiceDate(null);		

		BillingReportTransactionVO nullReportType = validTransaction(ApplicationEnum.NETSUITE)
				.setReportType(null);

		BillingReportTransactionVO txnWithInvalidExpenseData = validTransaction(ApplicationEnum.NETSUITE)
		 		.setExpenseCategory(EXPENSE_CATEGORY)
		 		.setExpenseSubCategory(EXPENSE_SUB_CATEGORY)
		 		.setAnalysisCodeDescription(ANALYSIS_CODE_DESC);

		BillingReportTransactionVO txnWithNullExpenseData = validTransaction(ApplicationEnum.NETSUITE)
				.setExpenseCategory(EXPENSE_CATEGORY)
				.setExpenseSubCategory(EXPENSE_SUB_CATEGORY)
				.setAnalysisCodeDescription(ANALYSIS_CODE_DESC);

		BillingReportTransactionVO txnWillowWithNoDocRef = validTransaction(ApplicationEnum.WILLOW)
		 		.setDocId(null)
		 		.setLineId(null);

		BillingReportTransactionVO invalidDriver = validTransaction(ApplicationEnum.NETSUITE)
		        .setDriverName(null)
				.setDriverAddressState(null);

        assertEquals(1, validator.validate(nullBIllingPeriod).size());
        assertEquals(1, validator.validate(nullReportType).size());
        assertEquals(1, validator.validate(txnWithInvalidExpenseData).size());
        assertEquals(1, validator.validate(txnWithNullExpenseData).size());
        assertEquals(1, validator.validate(txnWillowWithNoDocRef).size());		
        assertEquals(1, validator.validate(invalidDriver).size());		
	}
	
	private BillingReportTransactionVO validTransaction(ApplicationEnum origin) {
		if(origin == ApplicationEnum.NETSUITE) {
			return new BillingReportTransactionVO()
					.setMonthServiceDate(BILLING_PERIOD)
					.setReportType(BillingReportTypeEnum.MISCELLANEOUS)
					.setAnalysisCodeDescription(ANALYSIS_CODE_DESC)
					.setExpenseCategory(EXPENSE_CATEGORY)
					.setExpenseSubCategory(EXPENSE_SUB_CATEGORY)
					.setOrigin(origin)
					.setDriverId(DRIVER_ID)
					.setDriverAddressState(DRIVER_STATE)
					.setDriverName(DRIVER_NAME)
					.setUnit(UNIT_NO); 
		} else {
			return new BillingReportTransactionVO()
			        .setMonthServiceDate(BILLING_PERIOD)
			        .setReportType(BillingReportTypeEnum.MISCELLANEOUS)
			        .setDocId(-1L)
			        .setLineId(-2L)
			        .setOrigin(ApplicationEnum.WILLOW)
			        .setDriverId(DRIVER_ID)
			        .setDriverAddressState(DRIVER_STATE)
			        .setDriverName(DRIVER_NAME)
					.setUnit(UNIT_NO);
		}
	}
}

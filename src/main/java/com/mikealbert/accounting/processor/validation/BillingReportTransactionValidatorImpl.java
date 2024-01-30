package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.enumeration.ApplicationEnum;

@Component
public class BillingReportTransactionValidatorImpl implements ConstraintValidator<BillingReportTransactionValidator, BillingReportTransactionVO> {
	static final String MESSAGE_TEMPLATE = "Billing Report Transaction %s. %s";

	static final Logger LOG = LogManager.getLogger(VendorValidator.class);
	
	@Override
	public boolean isValid(BillingReportTransactionVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();

			messages = validateNotNullMonthServiceDate(value, messages);
			messages = validateNotNullReportType(value, messages);
			messages = validateExpenseData(value, messages);
			messages = validateNotNullExpenseData(value, messages);
			messages = validateDocRef(value, messages);
			messages = validateDriverDetail(value, messages);
			
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate the Billing Report Transaction. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}

	private List<String> validateNotNullMonthServiceDate(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getMonthServiceDate() == null) {
			messages.add(String.format(MESSAGE_TEMPLATE, "must have a month/service date", messageData(txn)));
		}

		return messages;
	}

	private List<String> validateNotNullReportType(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getReportType() == null) {
			messages.add(String.format(MESSAGE_TEMPLATE, "must have a report type", messageData(txn)));
		}

		return messages;
	}

	private List<String> validateNotNullExpenseData(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getOrigin() == ApplicationEnum.NETSUITE) {
			if(txn.getExpenseCategory() == null 
			        || txn.getExpenseSubCategory() == null 
					|| txn.getAnalysisCodeDescription() == null) {
				messages.add(String.format(MESSAGE_TEMPLATE, "report category, sub category, and description is required", messageData(txn)));
			}
		}

		return messages;
	}

	private List<String> validateExpenseData(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getOrigin() == ApplicationEnum.NETSUITE
		        && "Rent".equalsIgnoreCase(txn.getExpenseCategory())
				&& "Monthly Rental".equalsIgnoreCase(txn.getExpenseSubCategory())
				&& "Monthly Rental".equalsIgnoreCase(txn.getAnalysisCodeDescription())) {
			messages.add(String.format(MESSAGE_TEMPLATE, "unsupported origin for this type of transaction", messageData(txn)));
		}

		return messages;
	}	

	private List<String> validateDocRef(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getOrigin() == ApplicationEnum.WILLOW) {
			if(txn.getDocId() == null || txn.getLineId() == null) {
				messages.add(String.format(MESSAGE_TEMPLATE, "internal transactions require both doc id and doc line id", messageData(txn)));
			}			
		}

		return messages;
	}

	private List<String> validateDriverDetail(BillingReportTransactionVO txn, List<String> messages) {
		if(txn.getUnit() != null && txn.getDriverId() != null) {	
			if(txn.getDriverAddressState() == null) {
				messages.add(String.format(MESSAGE_TEMPLATE, "must have a driver state", messageData(txn)));
			}
			
			if(txn.getDriverName() == null) {
				messages.add(String.format(MESSAGE_TEMPLATE, "must have a driver name (includes last name, first initial)", messageData(txn)));			
			}
	    }
		
		return messages;
	}	

	private String messageData(BillingReportTransactionVO txn) {
		return String.format("intId=%s, extId=%s, transactionNumber=%s, transactionType=%s, monthServiceDate=%s, accountCode=%s, accountingPeriod=%s, origin=%s, reportType=%s", 
		        txn.getTranInternalId(), txn.getTranExternalId(), txn.getTransactionNumber(), txn.getType(), txn.getMonthServiceDate(), 
				txn.getAccountCode(), txn.getAccountingPeriod(), txn.getOrigin(), txn.getReportType());
	}

	
}

package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DisposalInvoiceValidatorImpl implements ConstraintValidator<DisposalInvoiceValidator, DisposalInvoiceVO> {
	static final Logger LOG = LogManager.getLogger(DisposalInvoiceValidator.class);
	
	@Override
	public boolean isValid(DisposalInvoiceVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();

			//Validate input parameters
			if(value.getTransactionId() == null)
				messages.add(String.format("disposalInvoice.transactionId input parameter cannot be null. disposalInvoice: %s", value.toString()));
			
			if(value.getTransactionExtId() == null)
				messages.add(String.format("disposalInvoice.transactionExtId (ExternalId) input parameter cannot be null. disposalInvoice: %s", value.toString()));
			
			if(value.getDocId() == null)
				messages.add(String.format("disposalInvoice.docId input parameter cannot be null. disposalInvoice: %s", value.toString()));
			
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(String.format("Data: %s - ValidationErrorMessage: %s", value.toString(), messages.toString())).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate disposal invoice. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(String.format("Data: %s - ValidationErrorMessage: %s", value.toString(), messages.toString()) ).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}
	
}

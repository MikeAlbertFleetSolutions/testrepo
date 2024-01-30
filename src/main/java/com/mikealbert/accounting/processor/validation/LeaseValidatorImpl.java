package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.LeaseVO;

@Component
public class LeaseValidatorImpl implements ConstraintValidator<LeaseValidator, LeaseVO> {
	static final Logger LOG = LogManager.getLogger(LeaseValidator.class);

	@Override
	public boolean isValid(LeaseVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();
			
			messages = validateRequiredFields(value, messages);		
			messages = validateVariablePayment(value, messages);
			messages = validateVariableRateIndex(value, messages);
			
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(String.format("Lease %s: %s", value.getExternalId(), messages.toString())).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate lease. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}

	private List<String> validateRequiredFields(LeaseVO leaseVO,  List<String> messages){
		if(leaseVO.getExternalId() == null) 
			messages.add("Lease record could not be generated from the quote");
		
		if(leaseVO.getName() == null || leaseVO.getName().isBlank()) 
			messages.add("Name is required");
		
		if(leaseVO.getExternalProductType() == null || leaseVO.getExternalProductType().isBlank()) 
			messages.add("Product Type is required");
		
		if(leaseVO.getCommencementDate() == null) 
			messages.add("Commencement Date is required");
		
		if(leaseVO.getEndDate() == null) 
			messages.add("End Date is required");
		
		if(leaseVO.getTerm() == null) 
			messages.add("Term is required");		

		if(leaseVO.getLeaseAssetFairValue() == null) 
			messages.add("Lease Asset Fair Value is required");
		
		if(leaseVO.getLeaseAssetCostCarrying() == null) 
			messages.add("Lease Asset Cost Carrying is required");		

		if(leaseVO.getCapitalContribution() == null) 
			messages.add("Prepayment value is required");

		if(leaseVO.getResidualValueEstimate() == null) 
			messages.add("Residual Value Estimate is required");

		if(leaseVO.isVariablePayment() == null || leaseVO.isVariablePayment().isBlank()) 
			messages.add("Variable Payment indicator is required");

		if(leaseVO.getUnitNo() == null || leaseVO.getUnitNo().isBlank()) 
			messages.add("Unit No is required");
		
		return messages; 
	}
	
	private List<String> validateVariablePayment(LeaseVO leaseVO,  List<String> messages){
		String value = leaseVO.isVariablePayment();
		
		if(value != null)
			if(! ("False".equalsIgnoreCase(value) || "True".equalsIgnoreCase(value)) )
				messages.add("Varibale Payment indictor value is invalid. Must be 'True' or 'False'");
				
		return messages; 
	}
	
	private List<String> validateVariableRateIndex(LeaseVO leaseVO,  List<String> messages){
		String indicator = leaseVO.isVariablePayment();
		String index = leaseVO.getVariableRateIndex();

		if(indicator != null && indicator.equals("True")) {
			if(index == null || index.isBlank()) 
				messages.add("Variable Rate Index is required'");
		}
		
		return messages; 
	}	
	
}
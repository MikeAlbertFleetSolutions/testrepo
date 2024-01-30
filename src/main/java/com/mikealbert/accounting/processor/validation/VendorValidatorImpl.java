package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.VendorVO;

@Component
public class VendorValidatorImpl implements ConstraintValidator<VendorValidator, VendorVO> {
	static final Logger LOG = LogManager.getLogger(VendorValidator.class);
	
	@Override
	public boolean isValid(VendorVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();
			
			messages = validateRequiredFields(value, messages);		
			
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate Vendor. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}

	private List<String> validateRequiredFields(VendorVO vendorVO,  List<String> messages){
		if(!vendorVO.getcId().equals(1l)) 
			messages.add(String.format("Primary Subsidiary of the Vendor %s needs to be Mike Albert Leasing, Inc. Instead it references to c_id %d", vendorVO.getAccountCode(), vendorVO.getcId()));
		
		return messages; 
	}
	
}

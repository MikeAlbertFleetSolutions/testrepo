package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;

@Component
public class DriverUnitValidatorImpl implements ConstraintValidator<DriverUnitValidator, DriverUnitHistoryUpsertVO> {
	static final Logger LOG = LogManager.getLogger(DriverUnitValidator.class);
	
	@Override
	public boolean isValid(DriverUnitHistoryUpsertVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();

			if(value.getDrvId() == null)
				messages.add("DrvId is null");
			if(value.getUnitNo() == null)
				messages.add("UnitNo is null");
			if(value.getEffectiveDate() == null)
				messages.add("Effective Date is null");
			if(value.getFirstName() == null)
				messages.add("Driver First Name is null");
			if(value.getLastName() == null)
				messages.add("Driver Last Name is null");
			if(value.getDriverAddress() == null)
				messages.add("Driver address is null");
			else {
				if(value.getDriverAddress().getAddressLine1() == null)
					messages.add("Driver address line1 is null");
				if(value.getDriverAddress().getTownDescription() == null)
					messages.add("Driver address city is null");
				if(value.getDriverAddress().getRegionCode() == null)
					messages.add("Driver address state is null");
				if(value.getDriverAddress().getZipCode() == null)
					messages.add("Driver address zip is null");
				if(value.getDriverAddress().getCountryCode() == null)
					messages.add("Driver address country is null");
			}
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(String.format("Data: %s - ValidationErrorMessage: %s", value.toString(), messages.toString())).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate Driver. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(String.format("Data: %s - ValidationErrorMessage: %s", value.toString(), messages.toString()) ).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}
	
}

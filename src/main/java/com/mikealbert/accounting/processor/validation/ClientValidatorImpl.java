package com.mikealbert.accounting.processor.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.accounting.processor.vo.ClientVO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientValidatorImpl implements ConstraintValidator<ClientValidator, ClientVO> {
	static final Logger LOG = LogManager.getLogger(VendorValidator.class);
	
	@Override
	public boolean isValid(ClientVO value, ConstraintValidatorContext context) {
		List<String> messages = new ArrayList<>();
		boolean isValid;
		
		try {
			context.disableDefaultConstraintViolation();

			messages = validateBillingAddressOnUpdate(value, messages);
			
			if(messages.isEmpty()) {
				isValid = true;
			} else {
				context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();
				isValid = false;
			}
		
		} catch(Exception e) {
			LOG.error(e);
			messages.add("Failed to validate Client. Please contact ITS Help Desk");
			context.buildConstraintViolationWithTemplate(messages.toString()).addConstraintViolation();			
			isValid = false;
		}
		
		return isValid;		
	}

	private List<String> validateBillingAddressOnUpdate(ClientVO clientVO, List<String> messages) {
		if(StringUtils.hasText(clientVO.getExternalId())) {
			if(!StringUtils.hasText(clientVO.getAddressInternalId())) {
				messages.add(String.format("Client %s must have a default billing address", clientVO.getAccountCode()));
			}
		}

		return messages;

	}
	
}

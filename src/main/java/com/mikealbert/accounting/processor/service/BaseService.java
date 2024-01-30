package com.mikealbert.accounting.processor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public abstract class BaseService {
	@Value("${spring.profiles.active}")
	protected String activeProfile;
			
	@Resource Validator validator;
	@Resource Environment env;

	@Autowired private JavaMailSender javaMailSender;

	@Value("${mafs.help.desk.email}")
	String emailFrom;

	protected void validate(Object input) throws Exception {
		List<String> messages = new ArrayList<>();
		Set<ConstraintViolation<Object>> violations = validator.validate(input);

		for (ConstraintViolation<Object> violation : violations) {
			messages.add(violation.getMessage());
		}

		if (!messages.isEmpty()) {
			throw new Exception(messages.stream().map(Object::toString).collect(Collectors.joining(", ")));
		}

	}

	/**
	 * Method to send simple text email
	 * 
	 * @param emailTo
	 * @param emailSubject
	 * @param emailBody
	 */
	protected void sendtextEmail(String emailTo, String emailSubject, String emailBody) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(emailFrom);
		mail.setTo(emailTo);
		mail.setSubject(emailSubject);
		mail.setText(emailBody);

		javaMailSender.send(mail);

	}

}
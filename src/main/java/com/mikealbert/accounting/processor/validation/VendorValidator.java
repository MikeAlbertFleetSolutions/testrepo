package com.mikealbert.accounting.processor.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = VendorValidatorImpl.class)
@Target( { TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Documented
public @interface VendorValidator {
	String message() default "Vendor is invalid";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;

@SpringBootTest
@DisplayName("Given a Disposal Invoice")
public class DisposalInvoiceValidatorTest extends BaseTest {
    
    @Resource Validator validator;
    
    private DisposalInvoiceVO generateDisposalInvoiceVO() {
    	return new DisposalInvoiceVO()
		    		.setTransactionId("12345")
		    		.setTransactionExtId("12345")
		    		.setDocId(12345l)
		    		.setStatus(TransactionStatusEnum.OPEN);
    }

    @Test
    @DisplayName("when new disposal invoice with null transactionId field, then the valdiation(s) fail")
    public void testNullTransactionIdValidations() {
        final int EXPECTED_VIOLATIONS = 1;
        DisposalInvoiceVO disposalInv = generateDisposalInvoiceVO().setTransactionId(null);
        Set<ConstraintViolation<Object>> violations = validator.validate(disposalInv);
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when new disposal invoice with null transactionExtId field, then the valdiation(s) fail")
    public void testNullTransactionExtIdValidations() {
        final int EXPECTED_VIOLATIONS = 1;
        DisposalInvoiceVO disposalInv = generateDisposalInvoiceVO().setTransactionExtId(null);
        Set<ConstraintViolation<Object>> violations = validator.validate(disposalInv);
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when new disposal invoice with null docId field, then the valdiation(s) fail")
    public void testNullDocIdValidations() {
        final int EXPECTED_VIOLATIONS = 1;
        DisposalInvoiceVO disposalInv = generateDisposalInvoiceVO().setDocId(null);
        Set<ConstraintViolation<Object>> violations = validator.validate(disposalInv);
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

}

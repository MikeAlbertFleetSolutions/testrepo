package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.ClientVO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Given a client")
public class ClientValidatorTest extends BaseTest {
    
    @Resource Validator validator;

    static int TOTAL_NEW_CLIENT_VALIDATIONS = 8;
    static int TOTAL_UPDATE_CLIENT_VALIDATIONS = 8;

    @Test
    @DisplayName("when new client with bad account code field, then the valdiation(s) fail")
    public void testNewClientAccountCodeValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setAccountCode(null);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when new client does not have an account name field, then the valdiation(s) fail")
    public void testNewClientAccountNameValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setAccountName(null);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }  

    @Test
    @DisplayName("when new client does not have a short name field, then the valdiation(s) fail")
    public void testNewClientShortNameValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setShortName(null);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client has a bad phone number field, then the valdiation(s) fail")
    public void testNewClientPhoneNumberValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setPhoneNumber("xxx-xxx-xxxx xxxxxxxxxxxxxxxxxxxx");

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client has a bad fax number field, then the valdiation(s) fail")
    public void testNewClientFaxNumberValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setFaxNumber("xxx-xxx-xxxx xxxxxxxxxxxxxxxxxxxx");

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client has a no currency field, then the valdiation(s) fail")
    public void testNewClientCurrencyValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setCurrency(null);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client has a bad Credit Score feild, then the valdiation(s) fail")
    public void testNewClientCreditScoreValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setCreditScore("abcdefghijklmnopqrstuvwxyz");

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client with bad credit limit field, then the valdiation(s) fail")
    public void testNewClientCreditLimitValidations() {
        final int EXPECTED_VIOLATIONS = 2;

        ClientVO clientVO = generateClientVO()
                .setCreditLimit1(new BigDecimal("999999999999999999999999999999.99999"))
                .setCreditLimit2(new BigDecimal("-1"));

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when new client with bad capital limit fields, then the valdiation(s) fail")
    public void testNewClientCapitalLimitValidations() {
        final int EXPECTED_VIOLATIONS = 2;

        ClientVO clientVO = generateClientVO()
                .setCapitalLimit1(new BigDecimal("999999999999999999999999999999.99999"))
                .setCapitalLimit2(new BigDecimal("-1"));

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when new client with a bad Purchase Credit Limit field, then the valdiation(s) fail")
    public void testNewClientPurchaseCreditLimitValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setPurchaseCreditLimit(new BigDecimal("999999999999999999999999999999.99999"));

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }    

    @Test
    @DisplayName("when new client with a bad Risk Deposit Amount field, then the valdiation(s) fail")
    public void testNewClientRiskDepositAmountValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setRiskDepositAmount(new BigDecimal("999999999999999999999999999999.99999"));

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }    

        @Test
    @DisplayName("when new client with bad Unit Limit field(s), then the valdiation(s) fail")
    public void testNewClientUnitLimiValidations() {
        final int EXPECTED_VIOLATIONS = 2;

        ClientVO clientVO = generateClientVO()
                .setUnitLimit1(999999L)
                .setUnitLimit2(-1L);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    @Test
    @DisplayName("when update client that does not have a Default Billing address, then the valdiation(s) fail")
    public void testUpdateClientNoBillingAddressValidations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setExternalId("1")
                .setAddressInternalId(null);

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    } 

    @Test
    @DisplayName("when new client has a bad address1 field, then the valdiation(s) fail")
    public void testNewClientAddress1Validations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setAddress1("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx x");

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }     
    
    @Test
    @DisplayName("when new client has a bad address2 field, then the valdiation(s) fail")
    public void testNewClientAddress2Validations() {
        final int EXPECTED_VIOLATIONS = 1;

        ClientVO clientVO = generateClientVO()
                .setAddress2("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx x");

        Set<ConstraintViolation<Object>> violations = validator.validate(clientVO);
        
        assertEquals(EXPECTED_VIOLATIONS, violations.size());
    }

    private ClientVO generateClientVO() {
        return new ClientVO()
                .setCreditScore("9999")
                .setAccountCode("0000000000000000000000000")
                .setAccountName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .setShortName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .setCurrency("USD")
                .setUnitLimit1(99999L)
                .setUnitLimit2(99999L)
                .setCreditLimit1(new BigDecimal("999999999999.99"))
                .setCreditLimit2(new BigDecimal("999999999999.99"))
                .setCapitalLimit1(new BigDecimal("999999999999.99"))
                .setCapitalLimit2(new BigDecimal("999999999999.99"))
                .setPurchaseCreditLimit(new BigDecimal("999999999999.99"))
                .setRiskDepositAmount(new BigDecimal("999999999999.99"));
    }
}

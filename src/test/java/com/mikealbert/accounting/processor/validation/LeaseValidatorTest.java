package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.vo.LeaseVO;

@SpringBootTest
@DisplayName("Given a lease VO")
public class LeaseValidatorTest {
    @Resource Validator validator;

    static final String EXTERNAL_ID = "-1";
    static final String NAME = "Name";
    static final String PRODUCT_TYPE = "CE_LTD";
    static final Date COMMENCEMENT_DATE = new Date();
    static final Date END_DATE = new Date();
    static final BigDecimal TERM = BigDecimal.ZERO;
    static final BigDecimal LEASE_ASSET_FAIR_VALUE = BigDecimal.ZERO;
    static final BigDecimal LEASE_ASSET_COST_CARRYING = BigDecimal.ZERO;
    static final BigDecimal CAPITAL_CONTRIBUTION = BigDecimal.ZERO;
    static final BigDecimal RESIDUAL_VALUE_ESTIMATE = BigDecimal.ZERO;
    static final String VARIABLE_PAYMENT = "False";
    static final String UNIT_NO = "00000000";    

    @Test
    @DisplayName("when the required fields are null, then the validation fails")
    public void testValidateRequiredFields() {

        LeaseVO leaseNullExternalId = new LeaseVO()
                .setExternalId(null)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO);

        LeaseVO leaseNullName = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(null)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO);   

        LeaseVO leaseNullProductType = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(null)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO); 
                
        LeaseVO leaseNullCommencementDate = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(null)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO); 
                
        LeaseVO leaseNullEndDate = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(null)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO); 
                
        LeaseVO leaseNullTerm = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(null)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO);              

        LeaseVO leaseNullVariablePayment = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(null)
                .setUnitNo(UNIT_NO);                 

        LeaseVO leaseNullUnitNo = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(null);                                 

        assertEquals(1, validator.validate(leaseNullExternalId).size());
        assertEquals(1, validator.validate(leaseNullName).size());
        assertEquals(1, validator.validate(leaseNullProductType).size());
        assertEquals(1, validator.validate(leaseNullCommencementDate).size());
        assertEquals(1, validator.validate(leaseNullEndDate).size());
        assertEquals(1, validator.validate(leaseNullTerm).size());
        assertEquals(1, validator.validate(leaseNullVariablePayment).size());
        assertEquals(1, validator.validate(leaseNullUnitNo).size());
    }

    @Test
    @DisplayName("when varaible payment values is not 'True' or 'False', then validation fails")
    public void testValidateVariablePayment() {
        LeaseVO leaseVO = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment("F")
                .setUnitNo(UNIT_NO);        

        assertEquals(1, validator.validate(leaseVO).size());                
    }

    @Test
    @DisplayName("when varaible rate index should not be null, then validation fails")
    public void testValidateVariableRateIndex() {
        final String VARIABLE_PAYMENT = "True";

        LeaseVO leaseVO = new LeaseVO()
                .setExternalId(EXTERNAL_ID)
                .setName(NAME)
                .setExternalProductType(PRODUCT_TYPE)
                .setCommencementDate(COMMENCEMENT_DATE)
                .setEndDate(END_DATE)
                .setTerm(TERM)
                .setLeaseAssetFairValue(LEASE_ASSET_FAIR_VALUE)
                .setLeaseAssetCostCarrying(LEASE_ASSET_COST_CARRYING)
                .setCapitalContribution(CAPITAL_CONTRIBUTION)
                .setResidualValueEstimate(RESIDUAL_VALUE_ESTIMATE)
                .setVariablePayment(VARIABLE_PAYMENT)
                .setUnitNo(UNIT_NO)
                .setVariableRateIndex(null);

        assertEquals(1, validator.validate(leaseVO).size());                
    }    
}

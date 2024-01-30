package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.vo.VendorVO;


@SpringBootTest
@DisplayName("Given a vendor VO")
public class VendorValidatorTest {
    @Resource Validator validator;
        
    @Test
    @DisplayName("when vendor primary subsidiary is not 1, then validation fails")
    public void testValidateRequiredFields() {

        VendorVO vendorVO = new VendorVO()
          .setcId(2L);

        assertEquals(1, validator.validate(vendorVO).size());        
    }
}

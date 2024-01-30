package com.mikealbert.accounting.processor.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.vo.AddressVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;

@SpringBootTest
@DisplayName("Given a driver unit history")
public class DriverUnitValidatorTest {
    @Resource Validator validator;

    static final Long DRV_ID = -1L;
    static final String UNIT_NO = "00000000";
    static final Date EFFECTIVE_DATE = new Date();
    static final String FIRST_NAME = "John";
    static final String LAST_NAME = "Doe";
    static final AddressVO ADDRESS = new AddressVO()
            .setAddressLine1("10340 Evendale Dr.")
            .setTownDescription("Evendale")
            .setRegionCode("OH")
            .setZipCode("45241")
            .setCountryCode("USA");
    

    @Test
    @DisplayName("When required fields are null, then validation fails")
    public void testIsValid() {
        DriverUnitHistoryUpsertVO duhNullDrvId = new DriverUnitHistoryUpsertVO()
               .setDrvId(null)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS);

        DriverUnitHistoryUpsertVO duhNullUnitNo = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(null)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS);               

        DriverUnitHistoryUpsertVO duhNullEffectiveDate = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(null)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS);
               
        DriverUnitHistoryUpsertVO duhNullFirstName = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(null)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS);
               
        DriverUnitHistoryUpsertVO duhNullLastName = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(null)
               .setDriverAddress(ADDRESS);
               
        DriverUnitHistoryUpsertVO duhNullAddress = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(null);    
               
        DriverUnitHistoryUpsertVO duhNullAddressLine1 = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS.setAddressLine1(null));

        DriverUnitHistoryUpsertVO duhNullTownDescription = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS.setTownDescription(null));

        DriverUnitHistoryUpsertVO duhNullRegionCode = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS.setRegionCode(null)); 
               
        DriverUnitHistoryUpsertVO duhNullZipCode = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS.setZipCode(null));

        DriverUnitHistoryUpsertVO duhNullCountry = new DriverUnitHistoryUpsertVO()
               .setDrvId(DRV_ID)
               .setUnitNo(UNIT_NO)
               .setEffectiveDate(EFFECTIVE_DATE)
               .setFirstName(FIRST_NAME)
               .setLastName(LAST_NAME)
               .setDriverAddress(ADDRESS.setCountryCode(null));                   

        assertEquals(1, validator.validate(duhNullDrvId).size());
        assertEquals(1, validator.validate(duhNullUnitNo).size());
        assertEquals(1, validator.validate(duhNullEffectiveDate).size());
        assertEquals(1, validator.validate(duhNullFirstName).size());
        assertEquals(1, validator.validate(duhNullLastName).size());
        assertEquals(1, validator.validate(duhNullAddress).size());
        assertEquals(1, validator.validate(duhNullAddressLine1).size());
        assertEquals(1, validator.validate(duhNullTownDescription).size());
        assertEquals(1, validator.validate(duhNullRegionCode).size());
        assertEquals(1, validator.validate(duhNullZipCode).size());
        assertEquals(1, validator.validate(duhNullCountry).size());

    }
}

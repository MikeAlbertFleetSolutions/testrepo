package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.constant.enumeration.QuoteModelPropertyEnum;

@DataJpaTest
public class QuotationModelDAOTest extends BaseTest{
	@Resource QuotationModelDAO quotationModelDAO;

	@Test
	public void testFetchQuotationModelPropertyValueByFmsId() throws Exception { 
		final Long FMS_ID = 1L;
		final String EXPECTED_VALUE = "Fleet Management Company";

		String actualValue = quotationModelDAO.fetchQuotationModelPropertyValueByFmsId(FMS_ID, QuoteModelPropertyEnum.PLB_TYPE);

		assertEquals(EXPECTED_VALUE, actualValue);	
	}
}

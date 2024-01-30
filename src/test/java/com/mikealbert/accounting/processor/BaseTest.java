package com.mikealbert.accounting.processor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.accounting.processor.vo.UnitVO;

public abstract class BaseTest {
	@Value("${mafs.business.exception.email}")
	protected String emailTo;	
	
	@Value("${mafs.help.desk.email}")
	protected String emailFrom;	

	@Value("${mafs.client-billing-transaction.base-date}")
	protected String clientBillingTransactionBaseDate;	

	protected UnitVO generateMockUnit() {
		String uid = String.valueOf(System.currentTimeMillis());

		return new UnitVO()
				.setFmsId(Long.valueOf("-" + uid))
				.setVin("aaaaaaaaaaaaaaa11")
				.setYear("2024")
				.setMake("BMW")
				.setModel("M8")
				.setUnitNo("TEST" + uid)
				.setModelTypeDesc("M8 - Motor Sport")
				.setFuelType("Gasoline")
				.setGvr(2000L)
				.setHorsePower(new BigDecimal("650"))
				.setNewUsed("New")
				.setCbv(new BigDecimal("150000"));
	}
}

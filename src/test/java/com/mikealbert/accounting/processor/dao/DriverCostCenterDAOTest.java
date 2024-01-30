package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.CostCenterVO;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class DriverCostCenterDAOTest extends BaseTest{
	@Resource DriverCostCenterDAO driverCostCenterDAO;

	final Long CLIENT_ACCOUNT_C_ID = 1L;
	final String CLIENT_ACCOUNT_TYPE = "C";
	final String CLIENT_ACCOUNT_CODE = "00000001";
	final Long DRIVER_ID = 1L;
	
	@Disabled //TODO H2 DB doesn't support Oracle style of trunc.
	@Test
	public void testGetActiveCostCenter() {
		CostCenterVO costCenter;
		costCenter = driverCostCenterDAO.getActiveCostCenter(CLIENT_ACCOUNT_C_ID, CLIENT_ACCOUNT_TYPE, CLIENT_ACCOUNT_CODE, DRIVER_ID, new Date());
		assertNotNull(costCenter, "Driver's cost center code is null");
		assertEquals(costCenter.getDescription(), "C-3PO NOW Description");
	}
}

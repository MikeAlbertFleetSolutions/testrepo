package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;
import com.mikealbert.util.data.DateUtil;

@SpringBootTest
public class DriverSuiteTalkServiceTest {
	
	@Resource DriverSuiteTalkService driverSuiteTalkService;
	
	private DriverUnitHistoryUpsertVO mockDuhVO;
	
	@BeforeEach
	private void setupDriverUnitHistoryUpsertVO() {
		mockDuhVO = new DriverUnitHistoryUpsertVO();
		
		mockDuhVO
			.setDrvId(-123456l)
			.setUnitNo("01018940")
			.setEffectiveDate(DateUtil.convertLocalDateToDate(LocalDate.now()))
			.setFirstName("JunitTestFirstName")
			.setLastName("JunitTestLastName")
			.setAccountCode("00032944");
			
		mockDuhVO
			.getDriverAddress()
				.setAddressLine1("15297 Maple Ridge Drive")
				.setTownDescription("Carmel")
				.setRegionCode("Indiana")
				.setCountyCode("Hamilton")
				.setZipCode("46033")
				.setCountryCode("United States");
		mockDuhVO	
			.getSupplierAddress()
				.setAddressLine1("10340 Evendale Dr")
				.setTownDescription("Evendale")
				.setRegionCode("Ohio")
				.setCountyCode("Hamilton")
				.setZipCode("45241")
				.setCountryCode("United States");
		
	}
		
	@Test
	public void testDriverUnitHistoryCreate() throws Exception {
		assertDoesNotThrow(() -> driverSuiteTalkService.upsertDriverUnitHistory(mockDuhVO));
		assertDoesNotThrow(() -> driverSuiteTalkService.deleteDriverUnitHistory(mockDuhVO));
	}

	//TODO When integration of the driver's recharge code and unit's fleet ref no is in, update this test to create DUH record with the two fields and verify that they can be read
	@Test
	public void readDuhByUnitAndDate() throws Exception {
		final String UNIT_INTERNAL_ID = "449905"; //Unit No: 01040367 

		//Date dt = DateUtil.convertToDate("2021-04", DateUtil.PATTERN_YEAR_MONTH);
		DriverUnitHistoryVO duhVO = driverSuiteTalkService.readDuhByUnitInternalIdAndDate(UNIT_INTERNAL_ID, new Date());

		assertNotNull(duhVO.getInternalId());
		assertNotNull(duhVO.getExternalId());
		assertNotNull(duhVO.getAccountCode());
		assertNotNull(duhVO.getUnitNo());
		assertNotNull(duhVO.getDriverId());
		assertNotNull(duhVO.getDriverFirstName());
		assertNotNull(duhVO.getDriverLastName());
		assertNotNull(duhVO.getCostCenterCode());
		assertNotNull(duhVO.getCostCenterDescription());
		assertNotNull(duhVO.getDriverRechargeCode());
		assertNotNull(duhVO.getDriverFleetNo());
		assertNotNull(duhVO.getEffectiveDate());
	}
	
	@Test
	public void readDuhByUnitAndDate_exception() throws Exception {
		final String UNIT_INTERNAL_ID = "33480"; //Unit No: 01022897
		
		assertThrows(Exception.class, () -> {
			doThrow(new Exception("Service Failed")).when(driverSuiteTalkService).readDuhByUnitInternalIdAndDate(any(), any());
			driverSuiteTalkService.readDuhByUnitInternalIdAndDate(UNIT_INTERNAL_ID, new Date());
		});
	}

	@Disabled
	@Test
	public void readAllDuhs() throws Exception {
		List<DriverUnitHistoryVO> duhVOs = driverSuiteTalkService.readAllDuhs();

		assertTrue(duhVOs.size() > 50_000);
	}

}

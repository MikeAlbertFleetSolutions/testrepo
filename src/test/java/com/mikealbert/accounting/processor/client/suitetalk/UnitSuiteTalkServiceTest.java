package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.webservice.suitetalk.enumeration.VehicleClassificationEnum;

@SpringBootTest
public class UnitSuiteTalkServiceTest extends BaseTest{
	@Resource UnitSuiteTalkService unitSuiteTalkService;
		
	@Test
	public void testCreatedAndDelete() throws Exception {
		String id = null;
		UnitVO mockUnit = generateMockUnit();

		try {
			id = unitSuiteTalkService.putUnit(mockUnit);
		} finally {
			if(id != null) {
				unitSuiteTalkService.deleteUnit(mockUnit);
			}
		}
		
		assertNotNull(id);
	}

	@Test
	public void testGetByExternalId() throws Exception {
		String extId = null;
		UnitVO unit = null;
		UnitVO mockUnit = generateMockUnit().setVehicleClassification(VehicleClassificationEnum.TRAILER.getValue())
				.setPlbType("Fleet Management Company");

		try {
			extId = unitSuiteTalkService.putUnit(mockUnit);
			unit = unitSuiteTalkService.fetchByExternalId(extId);
		} finally {
			if(extId != null) {
				unitSuiteTalkService.deleteUnit(mockUnit);
			}
		}

		assertEquals(mockUnit.getFmsId(), unit.getFmsId());
		assertEquals(mockUnit.getUnitNo(), unit.getUnitNo());
		assertEquals(mockUnit.getVin(), unit.getVin());
		assertEquals(mockUnit.getYear(), unit.getYear());
		assertEquals(mockUnit.getMake(), unit.getMake());
		assertEquals(mockUnit.getModel(), unit.getModel());	
		assertEquals(mockUnit.getModelTypeDesc(), unit.getModelTypeDesc());	
		assertEquals(mockUnit.getFuelType(), unit.getFuelType());
		assertEquals(mockUnit.getGvr(), unit.getGvr());
		assertEquals(mockUnit.getHorsePower(), unit.getHorsePower());		
		assertEquals(mockUnit.getMsrp(), unit.getMsrp());
		assertEquals(mockUnit.getNewUsed(), unit.getNewUsed());

		assertEquals(VehicleClassificationEnum.TRAILER.getValue(), unit.getVehicleClassification());
		assertEquals(mockUnit.getPlbType(), unit.getPlbType());

		assertTrue(mockUnit.getCbv().compareTo(unit.getCbv()) == 0);

	}

}

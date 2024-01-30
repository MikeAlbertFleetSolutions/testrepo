package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.dao.FleetMasterDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.dao.UnitTitleHistoryDAO;
import com.mikealbert.accounting.processor.entity.Product;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.webservice.suitetalk.enumeration.VehicleClassificationEnum;

@SpringBootTest
public class UnitServiceTest extends BaseTest{
		    
	@Resource UnitService unitService;
	
	@MockBean UnitTitleHistoryDAO unitTitleHistoryDAO;	
	@MockBean FleetMasterDAO fleetMasterDAO;
	@MockBean QuotationModelDAO quotationModelDAO;
	
	@Test
	public void testGetUnitInfo() throws Exception {
		final String EXPECTED_PLB_TYPE_VALUE = "Fleet Management Company";
		
		Product product = new Product();
		product.setProductCode(ProductEnum.CE_LTD.name());


		UnitVO expectedUnit = generateMockUnit()
				.setModelTypeDesc("Other");

		when(fleetMasterDAO.getUnitInfo(any())).thenReturn(expectedUnit);
		when(fleetMasterDAO.findProductByUnitNoForLatestContract(any())).thenReturn(product);
		when(quotationModelDAO.fetchQuotationModelPropertyValueByFmsId(any(), any())).thenReturn(EXPECTED_PLB_TYPE_VALUE);

		UnitVO actualUnit = unitService.getUnitInfo(expectedUnit);

		assertEquals(VehicleClassificationEnum.EQUIPMENT.getValue(), actualUnit.getVehicleClassification());
		assertEquals(EXPECTED_PLB_TYPE_VALUE, actualUnit.getPlbType());
	}
	
			
}

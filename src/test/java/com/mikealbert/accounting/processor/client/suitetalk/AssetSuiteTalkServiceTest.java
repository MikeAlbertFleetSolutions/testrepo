package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.UnitVO;

@SpringBootTest
public class AssetSuiteTalkServiceTest extends BaseTest {
	
	@Resource AssetSuiteTalkService assetSuiteTalkService;
	@Resource UnitSuiteTalkService unitSuiteTalkService;
	
	NgAssetVO ngAsset;
	Long assetId;
	AssetCreateVO mockAssetVO;
	UnitVO mockUnit;
	
	@BeforeEach
	private void init() throws Exception {
		ngAsset = new NgAssetVO();
		ngAsset.setNgAssetId(Long.valueOf(25806));
		assetId = Long.valueOf(12345);
		
		mockUnit = generateMockUnit();
		unitSuiteTalkService.putUnit(mockUnit);
		
		String docId = String.valueOf(System.currentTimeMillis());
		
		mockAssetVO = new AssetCreateVO();

		try {
		//mockAssetVO.setActionType("AssetCreate");
		
		mockAssetVO.
			setVin(mockUnit.getVin())
			.setType("Fleet-Open End")
			.setStartDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/01/2020"))
			.setUseFulLife(36l)
			.setResidualValue(BigDecimal.ZERO)
			.setFleetId(Long.valueOf(mockUnit.getFmsId() ))
			.setUnitNo(mockUnit.getUnitNo())
			.setBusinessUnit("Fleet Solutions")
			.setInitialValue(BigDecimal.valueOf(942.14))
			.setProductCode("OE_LTD")
			.setInvoiceApDocId(Long.valueOf(docId))
			.setInvoiceArDocId(Long.valueOf(docId)+1)
			.setInvoiceArLineId(Long.valueOf(1))
			.setDescription(String.format("%s-Holdback (Unit No: %s).", mockUnit.getUnitNo(), mockUnit.getUnitNo()))
			.setDepreciationMethodName("Straight Line")
			.setStatusName("Pending")
			.setcId("1")
			.setUpdateControlCode("OE_LTD")
			.setInvoiceNo("INVTEST");
		}
		catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	@AfterEach
	void cleanUp() throws Exception {
		assetSuiteTalkService.deleteAsset(mockAssetVO);
		unitSuiteTalkService.deleteUnit(mockUnit);
	}
	
	@Disabled(value = "We can not update extId on an asset and then revert it. ExtId also gets added as part of AssetCreateRecord test")
	@Test
	public void testUpdateExtIdOnNgAsset() {
		assertDoesNotThrow(() -> assetSuiteTalkService.updateExtIdOnNgAsset(ngAsset, assetId));
	}

	/*
	@Disabled(value = "We can not place an asset in service and then revert it.")
	@Test
	public void testPutAssetPlaceInServiceRecord() {
		assertDoesNotThrow(() -> assetSuiteTalkService.putAssetPlaceInServiceRecord(mockAssetVO));
	}
	*/
	
	@Test
	public void testAssetCreateRecord() throws Exception {		
		assertDoesNotThrow(() -> assetSuiteTalkService.putAssetCreateRecord(mockAssetVO));		
	}

}
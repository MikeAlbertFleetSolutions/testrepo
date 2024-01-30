package com.mikealbert.accounting.processor.client.suiteanalytics;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;

@SpringBootTest
@DisplayName("Test class for AssetSuiteAnalytics class. Here we read assets from NetGain")
public class AssetSuiteAnalyticsServiceTest extends BaseTest {
	@Resource AssetSuiteAnalyticsService assetSuiteAnalyticsService;
	
	@Disabled("Data/time sensitive test.  This test will fail if the data in NetSuite changes")
	@Test
	@DisplayName("Test to verify connection with NetSuite")
	public void testGetAsset() {
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DATE, -180);		
		Date start = cal.getTime();
		
		cal.add(Calendar.DATE, 60);		
		Date end = cal.getTime();
		
		List<NgAssetsPerUnitVO> result  = assetSuiteAnalyticsService.getAsset(start, end);

		assertTrue(result.size() > 0, "Asset records do not exist");
	}

	@Test
	@DisplayName("when getting an asset record data by external id, then the matching asset data is returned")
	public void testGetAssetByExtId() {
		final Long ASSET_EXT_ID = 10L;

		NgAssetVO result = assetSuiteAnalyticsService.getAssetByExtId(ASSET_EXT_ID);

		assertNotNull(result);
	}

}

package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;

public interface AssetSuiteAnalyticsService {
	List<NgAssetsPerUnitVO> getAsset(Date from, Date to);
	
	NgAssetVO getAssetByExtId(Long externalId);
	
}

package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;

import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;

public interface AssetItemDAOCustom {
	
	public BigDecimal getCurrentValue(Long fmsId, Long qmdId, String productType);
	public BigDecimal getInitialValueOriginal(Long fmsId, Long qmdId);
	public String getNextAddOnSequence(Long fleetId);
	public AssetPlaceInServiceVO getAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetVO);
	public AssetRevalueVO getAssetRevalueRecord(AssetRevalueVO assetVO);
	public BigDecimal getSumInitialValueByFmsId(Long fmsId);
	public String getLeaseStatus(String unitNo);
	public Boolean isVehicalPaid(Long fmsId);
}

package com.mikealbert.accounting.processor.service;

import com.mikealbert.accounting.processor.entity.AssetItem;
import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;

public interface AssetIntegrationService {
	
	public final static String newAsset = "NEW-ASSET";
	public final static String rental = "Rental";
	public final static String fleetSolutions = "Fleet Solutions";
	public final static String mainAssetAddonSeq = "000";
	
	public final static String disposalStatus = "Approved";
	public final static String disposalFormNs = "Standard NG Asset Disposal Form";
	
	public final static String assetReValuationStatus = "Approved";
	public final static String assetReValuationType = "Prospective (No GL Impact)";
	public final static String revalueFormNs = "2";

	public NgAssetsPerUnitVO processNsToWillowAssetsPerUnit(NgAssetsPerUnitVO ngAssetsPerUnitVO) throws Exception;
	
	public AssetItem getAssetById(Long assetId);
	public AssetItem getParentAssetByFleetId(Long fleetId);

	public Boolean isParentAssetExists(String unitNo);
	
	public String getNextAddOnSequence(Long fleetId);
	
	public AssetPlaceInServiceVO getAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetVO) throws Exception;
	
	public AssetItem setAssetTypeHistory(AssetItem assetItem, NgAssetVO ngAsset) throws Exception;
	
	public Boolean isUnitOnContract(String unitNo);
	
	public AssetCreateVO getAssetCreateRecord(AssetCreateVO assetVO) throws Exception;
	
	public AssetTypeUpdateVO getAssetTypeUpdate(AssetTypeUpdateVO assetDataVO) throws Exception;

	public AssetCancelPoToStockVO cancelPoToStock(AssetCancelPoToStockVO assetVO) throws Exception;
	
	public AssetDisposalVO dispose(AssetDisposalVO assetVO) throws Exception;
	
	public AssetRevalueVO revalue(AssetRevalueVO assetVO) throws Exception;

	public String getLeaseType(String unitNo);
	
	public Boolean isVehicalPaid(Long fmsId);
}

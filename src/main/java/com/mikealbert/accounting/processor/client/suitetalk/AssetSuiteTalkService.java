package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.accounting.processor.vo.NgAssetVO;

public interface AssetSuiteTalkService {
	
	public static final String externalId = "ExternalId";
	
	public void updateExtIdOnNgAsset(NgAssetVO ngAsset, Long assetId) throws Exception;
	
	public String putAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetDataVO) throws Exception;

	public String putAssetCreateRecord(AssetCreateVO assetDataVO) throws Exception;
	
	public String putAssetTypeUpdateRecord(AssetTypeUpdateVO assetDataVO) throws Exception;

	public String cancelPoToStock(AssetCancelPoToStockVO assetVO) throws Exception;

	public void deleteAsset(AssetCreateVO assetVO) throws Exception;
	
	public String dispose(AssetDisposalVO assetVO) throws Exception;
	
	public String revalue(AssetRevalueVO assetVO) throws Exception;

}
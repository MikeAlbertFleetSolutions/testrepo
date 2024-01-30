package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.request.AssetRequest;
import com.mikealbert.accounting.processor.service.AssetIntegrationService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;
import com.mikealbert.constant.accounting.enumeration.AssetQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.QueueEnum;

@Component("assetNsToWillowQueueProcessor")
public class AssetNsToWillowQueueProcessor extends BaseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(AssetNsToWillowQueueProcessor.class);
	
	@Resource AssetIntegrationService assetIntegrationService;
	@Resource UnitService fleetMasterService;
	
	@Value("${spring.profiles.active}")
	String currEnv;	
	
	
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start AssetNsToWillowQueueProcessor... ");
		String message = (String)ex.getMessage().getBody();

        ObjectMapper mapper = new ObjectMapper();
        NgAssetsPerUnitVO ngAssetPerUnit = mapper.readValue(message, NgAssetsPerUnitVO.class);
		
        LOG.info(ex.getExchangeId() + " AssetNsToWillowQueueProcessor UnitNo: " + ngAssetPerUnit.getUnitNo());
        
        if(ngAssetPerUnit.getUnitNo() != null && !isClientPurchase(ngAssetPerUnit))
		{


			ngAssetPerUnit = assetIntegrationService.processNsToWillowAssetsPerUnit(ngAssetPerUnit);

			/*
			 * Check to see if this unit is on contract and if we have created any new assets for this unit
			 * If we did we need to place those assets in service
			 */
			if (ngAssetPerUnit.getUnitNo() != null && assetIntegrationService.isUnitOnContract(ngAssetPerUnit.getUnitNo())) {
				for (NgAssetVO ngAsset : ngAssetPerUnit.getListNgAssetVO()) {
					if (ngAsset.isNewAsset()) {
						AssetRequest request = new AssetRequest();
						//Place Asset in Service Queue
						request.setAssetId(ngAsset.getNgAssetExtid());
						this.sendToQueue(QueueEnum.ASSET_WILLOW_TO_NS, String.valueOf(ngAsset.getNgAssetExtid()), request, AssetQueueSelectorEnum.PLACEINSERVICE);
					}
				}
			}

			//Added Toggle currEnv to restrict deployment only for DEV environment - will be removed when planned to move further
			//LAFS-1452 If creating a new child asset and the 000 asset exists and the unit is off lease or Disposed Off, send Place In Service
			if ("DONOTUSE".equalsIgnoreCase(currEnv)) {
				if (ngAssetPerUnit.getUnitNo() != null
						&& !assetIntegrationService.isUnitOnContract(ngAssetPerUnit.getUnitNo())
						&& assetIntegrationService.isParentAssetExists(ngAssetPerUnit.getUnitNo())) {
					Boolean isUnitDisposedOff = fleetMasterService.isVehicleDisposedOff(ngAssetPerUnit.getUnitNo());
					Boolean isUnitOffContract = fleetMasterService.isVehicleOffContract(ngAssetPerUnit.getUnitNo());

					if (isUnitDisposedOff || isUnitOffContract) {
						for (NgAssetVO ngAsset : ngAssetPerUnit.getListNgAssetVO()) {
							if (ngAsset.isNewAsset()) {
								AssetRequest request = new AssetRequest();
								// Place Asset in Service Queue
								request.setAssetId(ngAsset.getNgAssetExtid());
								this.sendToQueue(QueueEnum.ASSET_WILLOW_TO_NS, String.valueOf(ngAsset.getNgAssetExtid()),
										request, AssetQueueSelectorEnum.PLACEINSERVICE);
								// If the unit is disposed, send Asset Disposal for the asset just created.
								if (isUnitDisposedOff) {
									this.sendToQueue(QueueEnum.ASSET_WILLOW_TO_NS,
											String.valueOf(ngAsset.getNgAssetExtid()), request,
											AssetQueueSelectorEnum.DISPOSE);
								}
							}
						}
					}
				}
			}
		}
        
        LOG.info(ex.getExchangeId() + " End AssetNsToWillowQueueProcessor UnitNo: " + ngAssetPerUnit.getUnitNo());
        
		ex.getIn().setBody(ngAssetPerUnit);
	}

	public boolean isClientPurchase(NgAssetsPerUnitVO ngAssetPerUnit )
	{
		  return assetIntegrationService.getLeaseType(ngAssetPerUnit.getUnitNo())
				  .equalsIgnoreCase(CommonConstants.CLIENT_PURCHASE);
	}
}

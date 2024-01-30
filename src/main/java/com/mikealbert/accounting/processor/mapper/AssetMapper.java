package com.mikealbert.accounting.processor.mapper;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.dao.ContractLineDAO;
import com.mikealbert.accounting.processor.dao.TimePeriodDAO;
import com.mikealbert.accounting.processor.entity.AssetItem;
import com.mikealbert.accounting.processor.entity.ContractLine;
import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.entity.TimePeriod;
import com.mikealbert.accounting.processor.service.AssetIntegrationService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.AssetStatusEnum;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DataUtil;

@Component("assetMapper")
public class AssetMapper {
	
	private static final Logger LOG = LogManager.getLogger(AssetMapper.class);
	
	@Resource XRefService xRefService;
	@Resource UnitService fleetMasterService;
	@Resource AssetIntegrationService assetIntegrationService;
	@Resource TimePeriodDAO timePeriodDAO;
	@Resource ContractLineDAO contractLineDAO;

	
	public AssetItem ngAssetToAssetItem(NgAssetVO ngAsset, AssetItem assetItem) throws Exception {
		
		//New Asset
		if(ngAsset.getNgAssetExtid() == null)
			assetItem = setNewAssetUsingNsData(ngAsset, assetItem);
		else
			assetItem = updateAssetItemUsingNsDataEveryTime(ngAsset, assetItem);
		
		return assetItem;
	}
	
	private AssetItem setNewAssetUsingNsData(NgAssetVO ngAsset, AssetItem assetItem) throws Exception {
		AssetItem parentAsset = new AssetItem();
		
		assetItem.setDescription(DataUtil.substr(ngAsset.getNgAssetName(), 0, 80));
		assetItem.setModelNo(ngAsset.getUnitName());
		assetItem.setInvoiceNo(DataUtil.substr(ngAsset.getInvoiceNo(),0,25));
		assetItem.setInvoiceDate(ngAsset.getAcquisitionDate());
		assetItem.setNsInServiceDate(ngAsset.getInserviceDate());
		
		FleetMaster fms = fleetMasterService.getFleetMasterByUnit(ngAsset.getUnitName());
		if (fms == null) {
			throw new Exception(String.format("Unable to find fleet masters record for NgAssetId: %s and UnitNo: %s", ngAsset.getNgAssetId(), ngAsset.getUnitName()));
		}
		assetItem.setFleetId(fms.getFmsId());			
		assetItem.setCode(String.valueOf(assetItem.getFleetId()));
			
		if(ngAsset.isMainAsset()) {
			assetItem.setAddOnSeq(AssetIntegrationService.mainAssetAddonSeq);
		}
		else { //ThirdPartyAsset
			assetItem.setAddOnSeq(assetIntegrationService.getNextAddOnSequence(fms.getFmsId()));
		}
		
		if(ngAsset.getUpdateControlCode() != null)
			assetItem.setCategoryCode(ngAsset.getUpdateControlCode());
		else {
			parentAsset = assetIntegrationService.getParentAssetByFleetId(fms.getFmsId());
			if (parentAsset != null) {
				assetItem.setCategoryCode(parentAsset.getCategoryCode());
			} else {
	        	if(!assetIntegrationService.isVehicalPaid(fms.getFmsId()) || assetIntegrationService.getLeaseType(fms.getUnitNo())
		  				  .equalsIgnoreCase(CommonConstants.CLIENT_PURCHASE)) {
	        		LOG.info("Not processing asset when its parent has not been paid OR asset with client purchase for NgAssetId: "+ngAsset.getNgAssetId()+" using fleetId: "+fms.getFmsId()+", Unit No "+fms.getUnitNo() );
	        		return null;
	        	} 
			}				
		}
		
		if(ProductEnum.INVENTORY.toString().equals(assetItem.getCategoryCode()))
			assetItem.setDepCode("LT_LTD");
		else {
			if(ngAsset.getUpdateControlCode() != null)
				assetItem.setDepCode(assetItem.getCategoryCode()); //When NS UpdateControlCode is not null then use NS UpdateControlCode.
			else {
				if (parentAsset != null)
					assetItem.setDepCode(parentAsset.getDepCode()); //In case NS UpdateControlCode is null then use Main Asset Dep Code.
				else
					throw new RetryableSuiteTalkException("No parent asset assigned for asset:" + assetItem.getAssetId());
			}
		}

		List<TimePeriod> tpList = timePeriodDAO.findByCIdAndApStatus(1l, "O");
		if(tpList != null)
			assetItem.setTpSeqNo(tpList.get(0).getSequenceNo());
		else
			throw new Exception(String.format("Unable to find an open AP Time Period for CId: 1"));
		
		assetItem.setDepValSoyBook(ngAsset.getCapitalizedAssetValueAtIn());
		assetItem.setDepValSoyTax(ngAsset.getCapitalizedAssetValueAtIn());
		assetItem.setPurchaseOrderNo(ngAsset.getUnitName());

		assetItem = assetIntegrationService.setAssetTypeHistory(assetItem, ngAsset);			
				
		assetItem.setStatus(xRefService.getInternalValue(XRefGroupNameEnum.ASSET_STATUS, String.valueOf(ngAsset.getStatusId())));
		assetItem.setCurrentValueBook(ngAsset.getCapitalizedAssetValueAtIn().subtract(ngAsset.getAccumulatedDepreciation()));
		assetItem.setCurrentValueTax(assetItem.getCurrentValueBook());
		assetItem.setInitialValue(ngAsset.getCapitalizedAssetValueAtIn());
		assetItem.setInitialValueTax(ngAsset.getCapitalizedAssetValueAtIn());
		assetItem.setResidualValue(ngAsset.getResidualValueEstimate());
		assetItem.setDateCapitalised(ngAsset.getAcquisitionDate());
		assetItem.setDateCapitalisedTax(ngAsset.getAcquisitionDate());
		assetItem.setPostedDate(ngAsset.getAcquisitionDate());

		Float months = Float.valueOf(ngAsset.getUsefulLifeAtInservice())/12;
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		months = Float.valueOf(decimalFormat.format(months));
				
		assetItem.setLifeBook(months);
		assetItem.setLifeTax(months);

		List<ContractLine> listCln = contractLineDAO.findContractLineByFmsIdAndStartDateNotNull(assetItem.getFleetId()); 
		assetItem.setCostAddInt((listCln != null && listCln.size() > 0) ? "Y" : null );

		return assetItem;
	}
	
	private AssetItem updateAssetItemUsingNsDataEveryTime(NgAssetVO ngAsset, AssetItem assetItem) throws Exception {
		
		assetItem.setOpCode(CommonConstants.NETSUITE_WILLOW_USER);
		assetItem.setDescription(DataUtil.substr(ngAsset.getNgAssetName(), 0, 80));
		assetItem.setNsInServiceDate(ngAsset.getInserviceDate());
		assetItem.setStatus(xRefService.getInternalValue(XRefGroupNameEnum.ASSET_STATUS, String.valueOf(ngAsset.getStatusId())));

		assetItem = assetIntegrationService.setAssetTypeHistory(assetItem, ngAsset);			
		
		/*
		 * It was found per Lafs-1258 that once an asset is Disposed Accumulated Depreciation in NS changes to 0
		 * That's a problem because then CurrentValue Book/Tax is reset to CapitalizedAssetValueAtInservice
		 * Once an asset status changes to Disposed we will not update CurrentValue Book/Tax 
		 */
		if(!AssetStatusEnum.DISPOSED.getStatusValue().toString().equals(assetItem.getStatus())) {
			assetItem.setCurrentValueBook(ngAsset.getCapitalizedAssetValueAtIn().subtract(ngAsset.getAccumulatedDepreciation()));
			assetItem.setCurrentValueTax(assetItem.getCurrentValueBook());
		}
		
		assetItem.setResidualValue(ngAsset.getResidualValueEstimate());
		assetItem.setInitialValue(ngAsset.getCapitalizedAssetValueAtIn());
		assetItem.setInitialValueTax(ngAsset.getCapitalizedAssetValueAtIn());
		
		assetItem.setDateCapitalised(ngAsset.getInserviceDate());
		assetItem.setDateCapitalisedTax(ngAsset.getInserviceDate());
		assetItem.setPostedDate(ngAsset.getAcquisitionDate());
		assetItem.setPurchaseOrderNo(ngAsset.getUnitName());
		assetItem.setModelNo(ngAsset.getUnitName());
		assetItem.setInvoiceDate(ngAsset.getAcquisitionDate());
		assetItem.setDisposalDate(ngAsset.getDisposalDate());
		assetItem.setDisposalProceeds(ngAsset.getDisposalProceeds());

		if(ngAsset.getUpdateControlCode() != null) {
			if(ProductEnum.INVENTORY.toString().equals(ngAsset.getUpdateControlCode()))
				assetItem.setDepCode("LT_LTD");
			else
				assetItem.setDepCode(ngAsset.getUpdateControlCode());
		}

		Float months = Float.valueOf(ngAsset.getUsefulLifeAtInservice())/12;
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		months = Float.valueOf(decimalFormat.format(months));
		assetItem.setLifeBook(months);
		assetItem.setLifeTax(months);
		
		List<ContractLine> listCln = contractLineDAO.findContractLineByFmsIdAndStartDateNotNull(assetItem.getFleetId()); 
		assetItem.setCostAddInt((listCln != null && listCln.size() > 0) ? "Y" : null );
		
		return assetItem;
	}


}

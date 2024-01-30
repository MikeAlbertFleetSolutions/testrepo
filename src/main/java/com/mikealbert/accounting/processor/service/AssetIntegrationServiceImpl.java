package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.client.suitetalk.AssetSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.dao.AssetItemDAO;
import com.mikealbert.accounting.processor.dao.AssetTypeHistoryDAO;
import com.mikealbert.accounting.processor.dao.ContractLineDAO;
import com.mikealbert.accounting.processor.dao.DisposalRequestDAO;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.DriverAllocationDAO;
import com.mikealbert.accounting.processor.dao.DriverCostCenterDAO;
import com.mikealbert.accounting.processor.dao.DriverDAO;
import com.mikealbert.accounting.processor.dao.FleetMasterDAO;
import com.mikealbert.accounting.processor.dao.OfferHistoryDAO;
import com.mikealbert.accounting.processor.dao.QuotationDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.entity.AssetItem;
import com.mikealbert.accounting.processor.entity.AssetTypeHistory;
import com.mikealbert.accounting.processor.entity.ContractLine;
import com.mikealbert.accounting.processor.entity.DisposalRequest;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.entity.Driver;
import com.mikealbert.accounting.processor.entity.DriverAllocation;
import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.entity.OfferHistory;
import com.mikealbert.accounting.processor.entity.Product;
import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.accounting.processor.enumeration.AssetDepreciationMethodEnum;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.helper.BusinessUnitHelper;
import com.mikealbert.accounting.processor.mapper.AssetMapper;
import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.accounting.processor.vo.CostCenterVO;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;
import com.mikealbert.constant.accounting.enumeration.AssetRevalueTypeUpdateEnum;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.DisposalMethodEnum;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DateUtil;


@Service("assetIntegrationService")
public class AssetIntegrationServiceImpl implements AssetIntegrationService{
	
	@Value("${spring.profiles.active}")
	String currEnv;	
	
	@Lazy @Resource AssetMapper assetMapper;
	@Lazy @Resource AssetSuiteTalkService assetSuiteTalkService;
	@Resource AssetItemDAO assetItemDAO;
	@Resource AssetTypeHistoryDAO assetTypeHistoryDAO;
	@Resource DriverDAO driverDAO;
	@Resource DriverCostCenterDAO driverCostCenterDAO;
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource XRefService xRefService;
	@Resource QuotationDAO quotationDAO;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource DocDAO docDAO;
	@Resource FleetMasterDAO fmsDAO;
	@Resource DisposalRequestDAO disposalRequestDAO;
	@Resource OfferHistoryDAO offerHistoryDAO;
	@Resource ContractLineDAO contractLineDAO;
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource UnitService fleetMasterService;
	
			
	private AssetItem initializeNewAsset(AssetItem asset) {
		asset.setcId(1l);
		asset.setAgcCId(1l);
		asset.setLocationCode("CUST");
		asset.setGroupCode("VEH");
		asset.setOpCode(CommonConstants.NETSUITE_WILLOW_USER);
		asset.setNoOfItems(1l);
		asset.setSourceCode("FLEET");
		
		return asset;

	}
	
	public NgAssetsPerUnitVO processNsToWillowAssetsPerUnit(NgAssetsPerUnitVO ngAssetsPerUnitVO) throws Exception {
		
		if (ngAssetsPerUnitVO.getListNgAssetVO() == null) {
			return ngAssetsPerUnitVO;
		}
		
		List<NgAssetVO> listProcessedNgAsset = new ArrayList<>();
		
		for(NgAssetVO ngAsset : ngAssetsPerUnitVO.getListNgAssetVO()) {
			NgAssetVO processedNgAsset = this.integrateAssetNsToWillow(ngAsset);
			if(processedNgAsset != null) {
				listProcessedNgAsset.add(processedNgAsset);
			}
		}
		
		ngAssetsPerUnitVO.setListNgAssetVO(listProcessedNgAsset);
		
		return ngAssetsPerUnitVO;
	}

	@Transactional
	private NgAssetVO integrateAssetNsToWillow(NgAssetVO ngAsset) throws Exception {
		
		AssetItem assetItem = new AssetItem();
		AssetItem persistedAssetItem;
		/*
		 * Check if this is a new asset or an existing Asset Record
		 * When we create an Asset from Willow like a Reclaim Asset we assign it a negative number based on the concatenation of InvoiceArDocId and InvoiceArLineId.
		 * Looking for a negative asset to handle that. 
		 */
		//
		if(ngAsset.getNgAssetExtid() != null && ngAsset.getNgAssetExtid() > 0l) {
			assetItem = this.getAssetById(ngAsset.getNgAssetExtid());
			ngAsset.setNewAsset(Boolean.FALSE);
		}
		else { //Initialize new Willow Asset			
			assetItem = initializeNewAsset(assetItem);
			ngAsset.setNgAssetExtid(null);  //Setting externalId as Null incase it is a negative number. It will be overwritten later with AssetItem.assetId
			ngAsset.setNewAsset(Boolean.TRUE);
		}
			
		assetItem = assetMapper.ngAssetToAssetItem(ngAsset, assetItem);		
		if(assetItem != null) {
			persistedAssetItem = assetItemDAO.save(assetItem);
			
			//This means it is a new asset we need to update ExternalId in NS.
			if (ngAsset.isNewAsset()) {
				assetSuiteTalkService.updateExtIdOnNgAsset(ngAsset, persistedAssetItem.getAssetId());			
			}
			return ngAsset;
		} else 
			return null;	
		
	}
	
	public AssetItem getAssetById(Long assetId) {
		Optional<AssetItem> assets = assetItemDAO.findById(assetId);
		if (assets.isEmpty())
			return null;
		else
			return assets.get();
	}
		
	public String getNextAddOnSequence(Long fleetId) {
		String addOnSeq = assetItemDAO.getNextAddOnSequence(fleetId);
		return ((addOnSeq == null || "null".equalsIgnoreCase(addOnSeq)) ? "001" : addOnSeq);
	}
	
	public AssetItem getParentAssetByFleetId(Long fleetId) {
		return assetItemDAO.getParentAsset(fleetId);
	}
	
	public AssetPlaceInServiceVO getAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetVO) throws Exception {
		
		assetVO = assetItemDAO.getAssetPlaceInServiceRecord(assetVO);
		
		assetVO
			.setResidualValue(updateResidualValue(assetVO.getAddOnSeq(), assetVO.getQmdId()))
			.setDepartment(updateDepartment(assetVO.getProductCode(), assetVO.getFleetId(), assetVO.getStartDate(), null))
			.setBusinessUnit(updateBusinessUnit(assetVO.getProductCode()))
			.setParentAssetId(updateParentAsset(assetVO.getAddOnSeq(), assetVO.getFleetId()))
			.setType(updateType(assetVO.getProductCode()));	
		
		//LAFS-1452 Added Toggle currEnv to restrict deployment only for DEV environment - will be removed when planned to move further 
		if ("DONOTUSE".equalsIgnoreCase(currEnv)) {
			if (this.isParentAssetExists(assetVO.getUnitNo())
					&& (fleetMasterService.isVehicleDisposedOff(assetVO.getUnitNo())
							|| fleetMasterService.isVehicleOffContract(assetVO.getUnitNo()))) {
				assetVO.setStartDate(null);
			}
		}
		
		return assetVO;
	}
	
	private BigDecimal updateResidualValue(String addOnSeq, Long qmdId) {
		if (AssetIntegrationService.mainAssetAddonSeq.equals(addOnSeq))
			return quotationDAO.getResidualByQmdId(qmdId);
		else
			return BigDecimal.ZERO;
	}

	private String updateDepartment(String productCode, Long fleetId, Date startDate, Long invoiceApDocId) throws Exception {
		String department = "";
		
		if (ProductEnum.ST.toString().equals(productCode)) {
			department = AssetIntegrationService.rental;
		}
		else if (ProductEnum.DEMO.toString().equals(productCode)) {
			DriverAllocation da = driverAllocationDAO.findCurrentDriver(fleetId, startDate);
			if(da != null) {
				CostCenterVO costCenter = driverCostCenterDAO.getActiveCostCenter(da.getDriver().getExternalAccount().getId().getCId(), 
						da.getDriver().getExternalAccount().getId().getAccountType(), 
	                    da.getDriver().getExternalAccount().getId().getAccountCode(), 
	                    da.getDriver().getDrvId(), 
	                    startDate);
				
				department = (costCenter == null ? null : costCenter.getDescription());
			}
			else {
				Long drvId = docDAO.findDrvIdByDocId(invoiceApDocId);
					
				Optional<Driver> drv = driverDAO.findById(drvId);
				CostCenterVO costCenter = driverCostCenterDAO.getActiveCostCenter(drv.get().getExternalAccount().getId().getCId(), 
						drv.get().getExternalAccount().getId().getAccountType(), 
						drv.get().getExternalAccount().getId().getAccountCode(), 
						drv.get().getDrvId(), 
						startDate);
				
				department = (costCenter == null ? null : costCenter.getDescription());
			}
		}
		
		//Set NS Value
		if(department != null && department.length() > 0) {
			String legacyDptName = department;
			department = suiteTalkCacheService.searchDepartmentInternalIdByName(department);
			if (department == null)
				throw new Exception(String.format("Unable to find NS InternalId for department %s", legacyDptName));
		}
			

		return department;
	}
	
	private String updateDepartment(String productCode, Long fleetId, Date startDate) throws Exception {
		String department = "";
		
		if (ProductEnum.ST.toString().equals(productCode)) {
			department = AssetIntegrationService.rental;
		}
		else if (ProductEnum.DEMO.toString().equals(productCode)) {
			DriverAllocation da = driverAllocationDAO.findCurrentDriver(fleetId, startDate);
			if(da != null) {
				CostCenterVO costCenter = driverCostCenterDAO.getActiveCostCenter(da.getDriver().getExternalAccount().getId().getCId(), 
						da.getDriver().getExternalAccount().getId().getAccountType(), 
	                    da.getDriver().getExternalAccount().getId().getAccountCode(), 
	                    da.getDriver().getDrvId(), 
	                    startDate);
				
				department = (costCenter == null ? null : costCenter.getDescription());
			}
			else
				throw new Exception(String.format("Unable to find driver for fleetId: %s on date: %s", fleetId, startDate)); 
		}
		else {
			return null;
		}
		
		//Set NS Value
		if(department != null && department.length() > 0) {
			String legacyDptName = department;
			department = suiteTalkCacheService.searchDepartmentInternalIdByName(department);
			if (department == null)
				throw new Exception(String.format("Unable to find NS InternalId for department %s", legacyDptName));
		}

		return department;
	}
	
	private String updateBusinessUnit(String productCode) throws Exception {
		ProductEnum product = ProductEnum.valueOf(productCode);
		return BusinessUnitHelper.resolve(product).getName();		
	}

	private String updateType(String productCode) throws Exception {		
		//Set NS Value
		return xRefService.getExternalValue(XRefGroupNameEnum.INVOICE_ASSET_TYPE, productCode);
	}

	private String updateCompany(String cId) throws Exception {		
		//Set NS Value
		return xRefService.getExternalValue(XRefGroupNameEnum.COMPANY, cId);
	}

	private String updateProductCode(Long invoiceApDocId) {
		Optional<Doc> invoiceApDoc = docDAO.findById(invoiceApDocId);
		String productCode = invoiceApDoc.get().getUpdateControlCode();
		
		if("DE".equals(productCode))
			productCode = ProductEnum.DEMO.name();
		
		return productCode;
	}

	private Long updateParentAsset(String addOnSeq, Long fleetId) {
		if (!AssetIntegrationService.mainAssetAddonSeq.equals(addOnSeq)) 
			return getParentAssetByFleetId(fleetId).getAssetId();
		else
			return null;
	}
	
	public AssetItem setAssetTypeHistory(AssetItem assetItem, NgAssetVO ngAsset) throws Exception {
		
		List<AssetTypeHistory> listATH = new ArrayList<>();
		AssetTypeHistory assetTypeHistory = new AssetTypeHistory();
		String assetType;
		
		List<XRef> xRefs = xRefService.getByGroupNameAndExternalValue(XRefGroupNameEnum.ASSET_DEP, ngAsset.getNgAssetTypeName());
		if(xRefs != null && xRefs.size() >= 1) {
			assetType = xRefs.get(0).getxRefPK().getInternalValue();
			assetType = assetType.substring(0, assetType.indexOf("-"));
		}
		else {
			throw new Exception(String.format("ASSET-DEP not mapped for: %s while processing NgAssetId: %s and AssetId: %s", ngAsset.getNgAssetTypeName(), ngAsset.getNgAssetId(), ngAsset.getNgAssetExtid()));
		}

		//Creating a record in AssetTypeHistory as well
		if(ngAsset.getNgAssetExtid() == null) {
			assetItem.setAssetType(assetType);
			assetTypeHistory
				.setAssetItem(assetItem)
				.setAssetType(assetItem.getAssetType())
				.setStartDate(ngAsset.getAcquisitionDate())
				.setOpCode(assetItem.getOpCode());
			listATH.add(assetTypeHistory);
			assetItem.setAssetTypeHistory(listATH);
			assetItem.setAssetTypeEffDate(ngAsset.getAcquisitionDate());
		}
		//Check to see if AssetType Changed If it did then create a new record in History table
		else if (!assetType.equals(assetItem.getAssetType())){
			//Step 1: Loop Through and set End Date on existing record
			LocalDate now = LocalDate.now();
			int i = 0;
			for(AssetTypeHistory assetTypeHist : assetItem.getAssetTypeHistory()) {
				if(assetTypeHist.getEndDate() == null) {
					assetItem.getAssetTypeHistory().get(i).setEndDate(DateUtil.convertLocalDateToDate(now));
				}
				i++;
			}
			//Step 2: Create a new record with tomorrow's Date
			assetTypeHistory
				.setAssetItem(assetItem)
				.setAssetType(assetType)
				.setOpCode(CommonConstants.NETSUITE_WILLOW_USER)
				.setStartDate(DateUtil.convertLocalDateToDate(now.plusDays(1l)));
			assetItem.getAssetTypeHistory().add(assetTypeHistory);
			
			//Step3: Update AssetType and typeEffectiveDate on assetItem
			assetItem.setAssetType(assetType);
			assetItem.setAssetTypeEffDate(assetTypeHistory.getStartDate());
		}

		return assetItem;
	}
	
	public Boolean isUnitOnContract(String unitNo) {
		return quotationDAO.isUnitOnContract(unitNo);
	}
	
	@Override
	public AssetCreateVO getAssetCreateRecord(AssetCreateVO assetVO) throws Exception {
		assetVO = initializeCreateAsset(assetVO);
		
		assetVO = docDAO.getAssetCreateData(assetVO);
		
		Optional<Doc> doc = docDAO.findById(assetVO.getInvoiceApDocId());
		
		assetVO
			.setInitialValue(assetVO.getInitialValue().multiply(BigDecimal.valueOf(-1))) //Change initial value to negative
			.setProductCode(updateProductCode(assetVO.getInvoiceApDocId()))
			.setType(updateType(assetVO.getProductCode()))
			.setDepartment(updateDepartment(assetVO.getProductCode(), assetVO.getFleetId(), assetVO.getStartDate(), assetVO.getInvoiceApDocId()))
			.setBusinessUnit(updateBusinessUnit(assetVO.getProductCode()))
			.setcId(updateCompany(assetVO.getcId()))
			.setUpdateControlCode(doc.get().getUpdateControlCode())
			.setInvoiceNo(doc.get().getDocNo());
	
		return assetVO;
	}
	
	private AssetCreateVO initializeCreateAsset(AssetCreateVO assetVO) {
		assetVO
			.setDepreciationMethodName(AssetDepreciationMethodEnum.STRAIGHT_LINE.getName())
			.setStatusName("Pending")
			.setResidualValue(BigDecimal.ZERO)
			.setUseFulLife(Long.valueOf(36));
		return assetVO;
	}
	
	@Override
	public AssetTypeUpdateVO getAssetTypeUpdate(AssetTypeUpdateVO assetVO) throws Exception {
		Optional<AssetItem> assetItem = assetItemDAO.findById(assetVO.getAssetId());
		Optional<FleetMaster> fms = fmsDAO.findById(assetItem.get().getFleetId());

		Product prd = fmsDAO.findProductByUnitNoForLatestContract(fms.get().getUnitNo());
		
		assetVO
			.setProductCode(prd.getProductCode())
			.setNewType(updateNewAssetType(assetItem.get(), assetVO.getUpdateContext(), prd.getProductCode()));
		
		if(AssetRevalueTypeUpdateEnum.RE_LEASE.equals(assetVO.getUpdateContext())) {
			ContractLine cln = contractLineDAO.getActiveContractLine(fms.get().getFmsId());
			assetVO
				.setDepartment(updateDepartment(assetVO.getProductCode(), fms.get().getFmsId(), cln.getStartDate()))
				.setBusinessUnit(updateBusinessUnit(assetVO.getProductCode()));
		}
		
		return assetVO;
	}
	
	private String updateNewAssetType(AssetItem assetItem, AssetRevalueTypeUpdateEnum contextType, String productCode) throws Exception {
		if (AssetRevalueTypeUpdateEnum.DESIGNATION_CHANGE.equals(contextType)) {
			if (AssetTypeEnum.UC.toString().equals(assetItem.getAssetType())
					&& ProductEnum.ST.toString().equals(assetItem.getDepCode()))
				return "Long Term Inventory - Rental";
			else
				return "Long Term Inventory";
		} else if (AssetRevalueTypeUpdateEnum.TERMINATE.equals(contextType)) {
			if (ProductEnum.ST.toString().equals(assetItem.getDepCode()))
				return "Used Cars-Rental";
			else
				return "Used Cars";
		} else if (AssetRevalueTypeUpdateEnum.RE_LEASE.equals(contextType)) {
				return updateType(productCode);
		}
		else
			throw new Exception(String.format("Invalid input to updateNewAssetType for AssetId: %s. Context can not be: %s", assetItem.getAssetId(), contextType)); 
	}
		
	@Override
	public AssetCancelPoToStockVO cancelPoToStock(AssetCancelPoToStockVO assetVO) throws Exception {
		
		assetVO.setAssetType("Long Term Inventory")
			   .setPendingLive(Boolean.FALSE)
		       .setUpdateControlCode("INVENTORY");
		
		return assetVO;
	}
	
	@SuppressWarnings("unused")
	@Override
	public AssetDisposalVO dispose(AssetDisposalVO assetVO) throws Exception {
		Optional<AssetItem> assetItem = assetItemDAO.findById(assetVO.getAssetId());
		List<DisposalRequest> disposalReqs = new ArrayList<>();
		DisposalRequest disposalReq = new DisposalRequest();
		
		if(assetItem.get() != null)
			disposalReqs = disposalRequestDAO.findByFmsIdAndSaleDateNotNull(assetItem.get().getFleetId());
		
		if (disposalReqs.size() > 0)
			disposalReq = disposalReqs.get(0);  //Get Latest disposal request 		
		
		assetVO
			.setDisposalFlag(Boolean.TRUE)
			.setDisposalProceeds(updateDisposalProceeds(assetItem.get().getAddOnSeq(), disposalReq))
			.setDisposalDate(getDisposalSaleDate(disposalReq))
			.setDisposalStatus(AssetIntegrationService.disposalStatus)
			.setCustomForm(AssetIntegrationService.disposalFormNs);
		
		return assetVO;
	}
	
	private BigDecimal updateDisposalProceeds(String addOnSeq, DisposalRequest disposalReq) {
		if (AssetIntegrationService.mainAssetAddonSeq.equals(addOnSeq))
			return (disposalReq.getSellPriceActual() == null ? disposalReq.getHammerPrice() : disposalReq.getSellPriceActual());
		else
			return BigDecimal.ZERO;
	}
	
	private Date getDisposalSaleDate(DisposalRequest disposalReq) throws Exception{
		Date saleDate;
		
		if(DisposalMethodEnum.AUCTION.toString().equals(disposalReq.getDisposalMethod())) {
			List<OfferHistory> listOfferHistory = offerHistoryDAO.findByDrqDrqIdAndAcceptInd(disposalReq.getDrqId(), "Y");
			if(listOfferHistory != null && listOfferHistory.size() > 0)
				saleDate =  listOfferHistory.get(0).getOfferDate();
			else
				throw new Exception(String.format("In Asset dispose for FmsId: %s, DisposalRequestId: %s unable to find accepted OfferHistory record", disposalReq.getFmsId(), disposalReq.getDrqId()));
		} else {  //Disposal Method is not AUCTION
			saleDate = disposalReq.getSaleDate();
		}
		
		return saleDate;
		
	}
	
	private String updateRevalueDepreciationMethod(String revalueContext) {
		if (revalueContext.equals(AssetRevalueTypeUpdateEnum.TERMINATE.toString())) {
			revalueContext = "Non-Depreciating";
		} else if (revalueContext.equals(AssetRevalueTypeUpdateEnum.RE_LEASE.toString())) {
			revalueContext = "Straight Line";
		}
		return revalueContext;
	}
	
	@Override
	public AssetRevalueVO revalue(AssetRevalueVO assetVO) throws Exception {
		Optional<AssetItem> assetItem = assetItemDAO.findById(assetVO.getAssetId());

		if (assetItem.get() != null) {
			assetVO.setFmsId(assetItem.get().getFleetId());
			assetVO = assetItemDAO.getAssetRevalueRecord(assetVO);

			assetVO
				.setCustomForm(AssetIntegrationService.revalueFormNs)
				.setRevaluationType(AssetIntegrationService.assetReValuationType)
				.setStatusName(AssetIntegrationService.assetReValuationStatus)
				.setNewAssetType(updateNewAssetType(assetItem.get(), assetVO.getRevalueContext(), assetVO.getProductCode()))
				.setDepreciationMethodName(updateRevalueDepreciationMethod(assetVO.getRevalueContext().toString()));
			
			if(AssetRevalueTypeUpdateEnum.RE_LEASE.equals(assetVO.getRevalueContext())) {
				assetVO.setDepartment(updateDepartment(assetVO.getProductCode(), assetVO.getFmsId(), assetVO.getStartDate()))
					.setBusinessUnit(updateBusinessUnit(assetVO.getProductCode()));
			}
		}

		return assetVO;
	}

	@Override
	public String getLeaseType(String unitNo) {
		return assetItemDAO.getLeaseStatus(unitNo);
	}


	@Override
	public Boolean isParentAssetExists(String unitNo) {
		List<FleetMaster> fmsList = fmsDAO.findByUnitNo(unitNo);
		Boolean parentAssetFound = false;
		if (fmsList != null) {
			AssetItem assetItem = this.getParentAssetByFleetId(fmsList.get(0).getFmsId());
			if (assetItem.getAssetId() != null) {
				parentAssetFound = true;
			} 
		}
		return parentAssetFound;
	}
	
	
	@Override
	public Boolean isVehicalPaid(Long fmsId) {
		return assetItemDAO.isVehicalPaid(fmsId);
	}


	
}
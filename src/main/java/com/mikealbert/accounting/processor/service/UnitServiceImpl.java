package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suiteanalytics.UnitSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.UnitSuiteTalkService;
import com.mikealbert.accounting.processor.dao.AssetItemDAO;
import com.mikealbert.accounting.processor.dao.FleetMasterDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.dao.UnitTitleHistoryDAO;
import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.entity.Product;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.constant.enumeration.QuoteModelPropertyEnum;
import com.mikealbert.util.data.StringUtil;
import com.mikealbert.webservice.suitetalk.enumeration.VehicleClassificationEnum;

@Service("fleetMasterService")
public class UnitServiceImpl extends BaseService implements UnitService {

	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource AssetItemDAO assetItemDAO;
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource XRefService xRefService;
	@Resource UnitSuiteAnalyticsService unitSuiteAnalyticsService;
	@Resource UnitSuiteTalkService unitSuiteTalkService;
	@Resource UnitTitleHistoryDAO unitTitleHistoryDAO;
	@Resource QuotationModelDAO quotationModelDAO;

	final Logger LOG = LogManager.getLogger(this.getClass());	
	
	@Override
	public FleetMaster getFleetMasterByUnit(String unitNo) throws Exception {
		List<FleetMaster> fms = fleetMasterDAO.findByUnitNo(unitNo);

		return (fms == null || fms.isEmpty() ? null : fms.get(0));
	}

	@Override
	public String getFleetStatusCode(String unitNo) throws Exception {
		return fleetMasterDAO.getFleetStatus(unitNo);
	}

	@Override
	public Boolean isVehicleOffContract(String unitNo) throws Exception {
		FleetMaster fms = this.getFleetMasterByUnit(unitNo);
		return fleetMasterDAO.isVehicleOffLease(fms.getFmsId());
	}

	@Override
	public Boolean isVehicleDisposedOff(String unitNo) throws Exception {
		FleetMaster fms = this.getFleetMasterByUnit(unitNo);
		return fleetMasterDAO.isVehicleDisposed(fms.getFmsId());
	}
	
	@Override
	public List<Map<String, Object>> getExternalUnits() {
		return unitSuiteAnalyticsService.getAllExternalUnits();
	}

	@Override
	public List<Map<String, Object>> getExternalUnits(String externalId) {
		return unitSuiteAnalyticsService.getExternalUnitByExternalId(externalId);
	}
	
	@Override
	public UnitVO getUnitInfo(UnitVO unit) throws Exception {
		unit = fleetMasterDAO.getUnitInfo(unit)
			.setVehicleClassification(updateVehicleClassification(unit))
			.setPlbType(updatePLBType(unit))
		    .setModelTypeDesc(rephraseModelTypeDesc(unit))
			.setFuelType(updateFuelType(unit.getFuelType()))
			.setMsrp(calculateMsrp(unit.getFmsId()));
		
		unit.setNewUsed("Y".equalsIgnoreCase(quotationModelDAO.fetchQuotationModelPropertyValueByFmsId(unit.getFmsId(), QuoteModelPropertyEnum.USED_VEHICLE_YN)) ? "Used" : "New");

		return unit;
	}

	@Override
	public String upsertUnit(UnitVO unit) throws Exception {
		return unitSuiteTalkService.putUnit(unit);
	}

	private String updateFuelType(String fuelTypeDesc) {
		try {
			String externalFuelTypeDesc = xRefService.getExternalValue(XRefGroupNameEnum.VEHICLE_FUEL_TYPE_DESC,
					StringUtil.toUpperCase(fuelTypeDesc));
			if ("NULL".equalsIgnoreCase(externalFuelTypeDesc)) {
				return null;
			} else {
				return externalFuelTypeDesc;
			}
		} catch (Exception ex) {
			return "Gas";
		}
	}
	
	private BigDecimal calculateMsrp(Long fmsId) {
		BigDecimal msrp = assetItemDAO.getSumInitialValueByFmsId(fmsId);
		if (msrp != null) {
			BigDecimal unPaidPoTot = purchaseOrderService.getUnpaidPOTotalByFmsId(fmsId);
			msrp = msrp.add(unPaidPoTot == null ? BigDecimal.ZERO : unPaidPoTot);
		} else {
			msrp = purchaseOrderService.getUnpaidPOTotalByFmsId(fmsId);
		}

		return msrp;
	}
	
	private String rephraseModelTypeDesc(UnitVO unitInfo) {
		final String MODEL_TYPE_TRAILER = "Trailer";

		String modelTypeDesc = unitInfo.getModelTypeDesc();

		if ("Other".equalsIgnoreCase(unitInfo.getModelTypeDesc())) {
			if (!"Y".equalsIgnoreCase(unitInfo.getEquipmentFlag())) {
				modelTypeDesc = MODEL_TYPE_TRAILER;
			}
		}

		return modelTypeDesc;
	}
	
	private String updateVehicleClassification(UnitVO unitInfo) {
		String classfication = null;
		Product product = null;		

		product = fleetMasterDAO.findProductByUnitNoForLatestContract(unitInfo.getUnitNo());
		if(product == null || product.getProductCode() == null) { 
			LOG.warn("Product can not be found for unit {}", unitInfo);
			return null;
		}

		if(ProductEnum.valueOf(product.getProductCode()) == ProductEnum.OE_EQUIP) {
			classfication = VehicleClassificationEnum.EQUIPMENT.getValue();
		} else {
			switch(unitInfo.getModelTypeDesc()) {
				case "Trailers": 
					classfication = VehicleClassificationEnum.TRAILER.getValue();
					break;
				case "Other":	
					if(unitTitleHistoryDAO.findByFmsFmsId(unitInfo.getFmsId()).size() == 0) {
						classfication = VehicleClassificationEnum.EQUIPMENT.getValue();
					} else {
						classfication = VehicleClassificationEnum.TRAILER.getValue();
					}
					break;
				default:
					classfication = null;
					break;
			}
		}
		
		return classfication;
	}

	private String updatePLBType(UnitVO unitInfo) {		
		return quotationModelDAO.fetchQuotationModelPropertyValueByFmsId(unitInfo.getFmsId(), QuoteModelPropertyEnum.PLB_TYPE);
	}	
}

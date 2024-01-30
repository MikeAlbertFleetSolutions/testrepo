package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;
import com.mikealbert.util.data.DateUtil;

@Service("assetSuiteAnalyticsService")
public class AssetSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements AssetSuiteAnalyticsService{
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<NgAssetsPerUnitVO> getAsset(Date from, Date to) {
		List<Map<String, Object>> assets;
		List<NgAssetsPerUnitVO> listNgAssetsPerUnit = new ArrayList<NgAssetsPerUnitVO>();

		String start, end;
		String temp = "";
		Double tempD;
		if(isGmtConvert.equalsIgnoreCase("true")) {
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else{
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);

		}
    	
		StringBuilder sql = new StringBuilder()
				.append(" SELECT trim(a.id) AS ng_asset_id, trim(a.externalid) AS ng_asset_extid, trim(a.custrecord_fa_ast_subsidiary) AS subsidiary_id, ")
				.append("     a.altname AS ng_asset_name, trim(a.custrecord_fa_ast_status) AS status_id, trim(a.custrecord_fa_ast_type) AS type_id, a.custrecord_fa_ast_orig_capitalized_value AS capitalized_asset_value_at_in, ")
				.append("     a.custrecord_fa_ast_accumdepr AS accumulated_depreciation, a.custrecord_fa_ast_resid_value_estimate AS residual_value_estimate, a.custrecord_fa_ast_acquisition_date AS acquisition_date, a.custrecord_fa_ast_in_service_date AS inservice_date, a.custrecord_ma_disposal_date AS ma_disposal_date, a.custrecord_ma_disposal_proceeds AS ma_disposal_proceeds, ")
				.append("     trim(a.custrecord_fa_ast_source_transaction) AS source_transaction_id, trim(a.custrecord_fa_ast_useful_life) AS useful_life_at_inservice, ")
				.append("     a.custrecord_ma_invoice_number AS ma_invoice_number,  u.name AS unit_name, at.name AS ng_asset_type_name, ")				
				.append("     tran.custbody_ma_main_vehicle AS main_vehicle, a.custrecord_ma_ng_update_control_code update_control_code ")
				.append("   FROM customrecord_fa_asset a ")
				.append("     JOIN customRecord_cseg_mafs_unit u ON (a.cseg_mafs_unit = u.id) ")
				.append("     JOIN customrecord_fa_asset_type at ON (a.custrecord_fa_ast_type = at.id) ")
				.append("     JOIN customlist_fa_asset_status cfas ON (a.custrecord_fa_ast_status = cfas.id) ")
				.append("     LEFT OUTER JOIN transaction tran ON (tran.id = a.custrecord_fa_ast_source_transaction) ")
				.append("   WHERE ( (a.created between %s and %s) OR (a.lastmodified between %s and %s) ) ")
				.append("     AND at.name <> 'Layer' ")  //This is to exclude Lisa Wheelers Tax assets...added by Saket
				.append("     AND cfas.name <> ANY('On Hold') ")
				.append("   ORDER BY nvl(tran.custbody_ma_main_vehicle, 'N') DESC ");
			
		String stmt = String.format(sql.toString(), start, end, start, end);
		System.out.println(stmt);
		
		assets = super.execute(stmt);
		
		if(assets != null && assets.size() > 0) {
			NgAssetsPerUnitVO ngAssetsPerUnitVO = new NgAssetsPerUnitVO();
			for(Map<String, Object> asset : assets) {
				NgAssetVO ngAsset = new NgAssetVO();
				String unitNo = (String)asset.get("unit_name");
				
				if(ngAssetsPerUnitVO.getUnitNo() == null || !unitNo.equals(ngAssetsPerUnitVO.getUnitNo())) {
					if (ngAssetsPerUnitVO.getUnitNo() != null)
						listNgAssetsPerUnit.add(ngAssetsPerUnitVO);
					ngAssetsPerUnitVO = new NgAssetsPerUnitVO();
					ngAssetsPerUnitVO.setUnitNo(unitNo);
				}
				
				ngAsset.setNgAssetId(Long.valueOf((String)asset.get("ng_asset_id")));
				
				temp = (String)asset.get("ng_asset_extid");
				if(temp != null) 
					ngAsset.setNgAssetExtid(Long.valueOf(temp));
				
				ngAsset.setSubsidiaryId(Long.valueOf((String)asset.get("subsidiary_id")));
				ngAsset.setNgAssetName((String)asset.get("ng_asset_name"));
				ngAsset.setStatusId(Long.valueOf((String)asset.get("status_id")));
				ngAsset.setTypeId(Long.valueOf((String)asset.get("type_id")));
				ngAsset.setCapitalizedAssetValueAtIn(BigDecimal.valueOf((Double)asset.get("capitalized_asset_value_at_in")));
				ngAsset.setAccumulatedDepreciation(BigDecimal.valueOf((Double)asset.get("accumulated_depreciation")));
				ngAsset.setResidualValueEstimate(BigDecimal.valueOf((Double)asset.get("residual_value_estimate")));
				ngAsset.setAcquisitionDate((Date)asset.get("acquisition_date"));
				ngAsset.setInserviceDate((Date)asset.get("inservice_date"));
				ngAsset.setDisposalDate((Date)asset.get("ma_disposal_date"));
				
				tempD = (Double)asset.get("ma_disposal_proceeds");
				if(tempD != null)
					ngAsset.setDisposalProceeds(BigDecimal.valueOf((Double)asset.get("ma_disposal_proceeds")));
								
				temp = (String)asset.get("source_transaction_id");
				if(temp != null) 
					ngAsset.setSourceTransactionId(Long.valueOf(temp));
				
				ngAsset.setUsefulLifeAtInservice(Long.valueOf((String)asset.get("useful_life_at_inservice")));
				ngAsset.setInvoiceNo((String)asset.get("ma_invoice_number"));
				ngAsset.setUnitName(unitNo);
				ngAsset.setNgAssetTypeName((String)asset.get("ng_asset_type_name"));
				ngAsset.setMainVehicle((String)asset.get("main_vehicle"));
				ngAsset.setUpdateControlCode((String)asset.get("update_control_code"));
				
				ngAssetsPerUnitVO.getListNgAssetVO().add(ngAsset);
			}
			//Adding last list of assets
			listNgAssetsPerUnit.add(ngAssetsPerUnitVO);
		}		
		
		return listNgAssetsPerUnit;
	}
	
	@Override
	public NgAssetVO getAssetByExtId(Long externalId) {
		NgAssetVO ngAsset = new NgAssetVO();
		List<Map<String, Object>> assets;
		
		StringBuilder stmt = new StringBuilder()
				.append(" SELECT trim(ng.id) AS ng_asset_id , ng.name AS ng_asset_number, ng.altname AS ng_asset_name ")
				.append("   FROM customrecord_fa_asset ng ")
				.append("   WHERE ng.externalid = %s ");

		String sql = String.format(stmt.toString(), String.valueOf(externalId));
		assets = super.execute(sql);
		
		for(Map<String, Object> asset : assets) {
			ngAsset.setNgAssetId(Long.valueOf(String.valueOf(asset.get("ng_asset_id"))));
			ngAsset.setNgAssetNumber(String.valueOf(asset.get("ng_asset_number")));
			ngAsset.setNgAssetName(String.valueOf(asset.get("ng_asset_name")));
		}
		
		return ngAsset;
	}
	
}

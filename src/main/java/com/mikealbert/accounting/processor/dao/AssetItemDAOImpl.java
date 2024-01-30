package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import com.mikealbert.accounting.processor.entity.AssetItem;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.constant.accounting.enumeration.AssetRevalueTypeUpdateEnum;
import com.mikealbert.util.data.DataUtil;

public class AssetItemDAOImpl extends GenericDAOImpl<AssetItem, Long> implements AssetItemDAOCustom{

	private static final long serialVersionUID = 6841861328901285978L;
	
	public BigDecimal getCurrentValue(Long fmsId, Long qmdId, String productType) {
		BigDecimal currentValue = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();

		stmt.append("WITH AI_TOTAL AS( ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("    JOIN quotation_dealer_accessories qda ON ((qda.dac_dac_id = ai.dac_dac_id)) ");
		stmt.append("   WHERE ai.fleet_id = :fmsId");
		stmt.append("     AND qda.qmd_qmd_id = :qmdId");
		stmt.append("     AND ai.invoice_no IS NOT NULL ");
		stmt.append("     AND ai.invoice_date IS NOT NULL ");
		stmt.append("   UNION ALL ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("   WHERE ai.fleet_id = :fmsId ");
		stmt.append("     AND ai.dac_dac_id IS NULL ");
		stmt.append("     AND ai.invoice_no IS NOT NULL ");
		stmt.append("     AND ai.invoice_date IS NOT NULL) ");
		stmt.append(" SELECT sum(current_value) FROM AI_TOTAL");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);
		query.setParameter("productType", productType);
		
		currentValue = (BigDecimal)query.getSingleResult();
		
		return (currentValue == null ? BigDecimal.ZERO : currentValue);
	}

	public BigDecimal getInitialValueOriginal(Long fmsId, Long qmdId) {
		BigDecimal currentValue = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();

		stmt.append("WITH AI_TOTAL AS( ");
		stmt.append("  SELECT ai.initial_value ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("    JOIN quotation_dealer_accessories qda ON (qda.dac_dac_id = ai.dac_dac_id) ");
		stmt.append("   WHERE ai.fleet_id = :fmsId");
		stmt.append("     AND qda.qmd_qmd_id = :qmdId");
		stmt.append("     AND ai.invoice_no IS NOT NULL ");
		stmt.append("     AND ai.invoice_date IS NOT NULL ");
		stmt.append("   UNION ALL ");
		stmt.append("  SELECT ai.initial_value ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("   WHERE ai.fleet_id = :fmsId ");
		stmt.append("     AND ai.dac_dac_id IS NULL ");
		stmt.append("     AND ai.invoice_no IS NOT NULL ");
		stmt.append("     AND ai.invoice_date IS NOT NULL) ");
		stmt.append(" SELECT sum(initial_value) FROM AI_TOTAL");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);
		
		currentValue = (BigDecimal)query.getSingleResult();
		
		return currentValue;
	}
	
	public String getNextAddOnSequence(Long fleetId) {
		String addOnSeq;
		
		StringBuilder stmt = new StringBuilder();
		
		stmt.append(" SELECT lpad(max(to_number(add_on_seq)+1),3,'0') ");
		stmt.append("   FROM asset_item ");
		stmt.append("  WHERE fleet_id = :fleetId ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fleetId", fleetId);
		addOnSeq = String.valueOf(query.getSingleResult());
		
		return addOnSeq;
	}
	
	@SuppressWarnings("unchecked")
	public AssetPlaceInServiceVO getAssetPlaceInServiceRecord(AssetPlaceInServiceVO assetVO) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records; 
		
		
		stmt.append(" SELECT ai.initial_value, ai.add_on_seq, ");
		stmt.append("        fms.fms_id, fms.vin, fms.unit_no, ");
		stmt.append("        cln.start_date, cln.end_date, ");
		stmt.append("        qmd.contract_period, qmd.depreciation_factor, qmd.qmd_id, ");
		stmt.append("        qpr.prd_product_code, prd.product_type   ");
		stmt.append("   FROM asset_item ai ");
		stmt.append("   JOIN fleet_masters fms ON (fms.fms_id = ai.fleet_id) ");
		stmt.append("   JOIN contract_lines cln ON (cln.fms_fms_id = fms.fms_id ) ");
		stmt.append("   JOIN quotation_models qmd ON (cln.qmd_qmd_id = qmd.qmd_id) ");
		stmt.append("   JOIN quotations quo ON (qmd.quo_quo_id = quo.quo_id) ");
		stmt.append("   JOIN quotation_profiles qpr ON (quo.qpr_qpr_id = qpr.qpr_id) ");
		stmt.append("   JOIN products prd ON (qpr.prd_product_code = prd.product_code) ");
		stmt.append("  WHERE ai.asset_id = :assetId ");
		stmt.append("    ORDER BY cln.cln_id DESC ");
		stmt.append("    FETCH FIRST ROW ONLY ");
	     
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("assetId", assetVO.getAssetId());
		
		records = query.getResultList();
		
		//Only expect one record from this query
		for(Object[] record : records) {
			int i=0;
			
			assetVO.setInitialValue((BigDecimal)record[i]);
			assetVO.setAddOnSeq(String.valueOf(record[i+=1]));
			assetVO.setFleetId(Long.valueOf(String.valueOf(record[i+=1])));
			assetVO.setVin(String.valueOf(record[i+=1]));
			assetVO.setUnitNo(String.valueOf(record[i+=1]));
			assetVO.setStartDate((Date)record[i+=1]);
			assetVO.setEndDate((Date)record[i+=1]);
			assetVO.setUseFulLife(Long.valueOf(String.valueOf(record[i+=1])));
			assetVO.setDepreciationFactor((BigDecimal)record[i+=1]);
			assetVO.setQmdId(Long.valueOf(String.valueOf(record[i+=1])));
			assetVO.setProductCode(String.valueOf(record[i+=1]));
			assetVO.setProductType(String.valueOf(record[i+=1]));
		}
		
		return assetVO;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public AssetRevalueVO getAssetRevalueRecord(AssetRevalueVO assetVO) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;

		if (AssetRevalueTypeUpdateEnum.TERMINATE.equals(assetVO.getRevalueContext())) {
			stmt.append("  SELECT TO_DATE('01/'||TO_CHAR(actual_end_date,'MM/YYYY'), 'DD-MM-YYYY') effectiveDate, asset_id||TO_CHAR(SYSDATE, 'MMYYYY') external_id, web_common.get_product_code (qmd_qmd_id) product_code ");
			stmt.append("    FROM (SELECT cln.actual_end_date, ai.asset_id, ai.dep_code, cln.fms_fms_id, cln.qmd_qmd_id ");
			stmt.append("            FROM contract_lines cln, asset_item ai ");
			stmt.append("           WHERE cln.fms_fms_id = :fmsId ");
			stmt.append("             AND cln.fms_fms_id = ai.fleet_id ");
			stmt.append("             and ai.asset_id = :assetId ");
			stmt.append("             AND cln.actual_end_date IS NOT NULL ");
			stmt.append("        ORDER BY cln.con_con_id DESC, cln.rev_no DESC ");
			stmt.append("        FETCH FIRST ROW ONLY) cln ");
			stmt.append("    WHERE NOT EXISTS (SELECT 1 ");
			stmt.append("                        FROM contract_lines cln1, quotation_models qmd ");
			stmt.append("                       WHERE cln1.fms_fms_id = cln.fms_fms_id ");
			stmt.append("                         AND cln1.qmd_qmd_id = qmd.qmd_id ");
			stmt.append("                         AND qmd.quote_status = '6' ");
			stmt.append("                         AND cln1.actual_end_date IS NULL) ");
			
		}else if (AssetRevalueTypeUpdateEnum.RE_LEASE.equals(assetVO.getRevalueContext())) {
			stmt.append("  SELECT fl_general.get_first_date_of_the_month(start_date) effectiveDate, asset_id||TO_CHAR(SYSDATE, 'MMYYYY') external_id, ");
			stmt.append("  		  web_common.get_product_code (qmd_qmd_id) product_code, contract_period, quotation_report_data.get_residual_value(qmd_qmd_id) residual_value, ");
			stmt.append("         end_date, ");
			stmt.append("         CASE WHEN (start_date < fl_general.get_first_date_of_the_month(sysdate)) THEN months_between(fl_general.get_first_date_of_the_month(sysdate), fl_general.get_first_date_of_the_month(start_date)) ELSE 0 END offset_months, ");
			stmt.append("         months_between(end_date+1, ns_in_service_date), start_date ");
			stmt.append("    FROM (SELECT cln.start_date, cln.end_date, ai.asset_id, ai.dep_code, cln.fms_fms_id, cln.qmd_qmd_id, qmd.contract_period, ai.ns_in_service_date ");
			stmt.append("            FROM contract_lines cln, quotation_models qmd, asset_item ai ");
			stmt.append("           WHERE cln.fms_fms_id = :fmsId ");
			stmt.append("             AND ai.asset_id = :assetId ");
			stmt.append("             AND cln.fms_fms_id = ai.fleet_id ");
			stmt.append("             AND cln.qmd_qmd_id = qmd.qmd_id ");			
			stmt.append("             AND qmd.quote_status = '6') ");
		}
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("assetId", assetVO.getAssetId());
		query.setParameter("fmsId", assetVO.getFmsId());

		records = (List<Object[]>) query.getResultList();

		for (Object[] record : records) {
			int i = 0;
			assetVO.setEffectiveDate((Date) record[i]);
			assetVO.setExternalId((String) record[i+=1]);
			assetVO.setProductCode((String) record[i+=1]);
			
			if (AssetRevalueTypeUpdateEnum.RE_LEASE.equals(assetVO.getRevalueContext())) {
				assetVO.setRemainingUsefulLife(((BigDecimal)record[i+=1]).longValue());
				assetVO.setResidualValueEstimate((BigDecimal) record[i+=1]);
				assetVO.setEffectiveTo((Date) record[i+=1]);
				Long offsetMonths = ((BigDecimal)record[i+=1]).longValue();
				assetVO.setRevalueUsefulLife(((BigDecimal)record[i+=1]).longValue() + offsetMonths);
				assetVO.setStartDate((Date)record[i+=1]);
			}
		}

		return assetVO;
	}

	@Override
	public BigDecimal getSumInitialValueByFmsId(Long fmsId) {
		BigDecimal currentValue = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();

		stmt.append(" SELECT sum(initial_value) FROM asset_item ai ");
		stmt.append("   WHERE ai.fleet_id = :fmsId ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		
		currentValue = (BigDecimal)query.getSingleResult();
		
		return (currentValue == null ? BigDecimal.ZERO : currentValue);
	}

	public String getLeaseStatus(String unitNo)
	{
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("transaction_api_wrapper.get_lease_type")
				.registerStoredProcedureParameter("v_unit_no",String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_lease_type",String.class, ParameterMode.OUT);
		query.setParameter("v_unit_no",unitNo);
		query.execute();
		String lease_type = Optional.ofNullable((String) query.getOutputParameterValue("v_lease_type"))
				.filter(Predicate.not(String::isBlank))
				.orElse("");

		return lease_type;
	}
	
	@Override
	public Boolean isVehicalPaid(Long fmsId) {
		StoredProcedureQuery checkPaidQuery = entityManager.createStoredProcedureQuery("fl_supp_prog_wrapper.is_vehicle_paid")
												        .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
												        .registerStoredProcedureParameter(2, String.class, ParameterMode.OUT)
												        .setParameter(1, fmsId);
		
		checkPaidQuery.execute(); 		
		String result = (String)checkPaidQuery.getOutputParameterValue(2);		
		return DataUtil.convertToBoolean(result);
	}	
}

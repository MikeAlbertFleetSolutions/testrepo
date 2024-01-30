package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.entity.Product;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.util.data.DataUtil;

public class FleetMasterDAOImpl extends GenericDAOImpl<FleetMaster, Long> implements FleetMasterDAOCustom {
	
	private static final long serialVersionUID = 6848967328901285978L;
	
	@Value("${spring.profiles.active}")
    protected String activeProfile;
	
	@SuppressWarnings("unchecked")
	public Product findProductByUnitNoForLatestContract(String unitNo) {
		Product prd = new Product();
		List<Object[]> records;
		
		StringBuilder stmt = new StringBuilder(); 
		
		stmt.append("SELECT prd.product_code, prd.product_type, prd.description ");
		stmt.append("  FROM products prd");
		stmt.append("  JOIN quotation_profiles qpr ON (qpr.prd_product_code = prd.product_code) ");
		stmt.append("  JOIN quotations quo ON (quo.qpr_qpr_id = qpr.qpr_id) ");
		stmt.append("  JOIN quotation_models qmd ON (qmd.quo_quo_id = quo.quo_id) ");
		stmt.append("  JOIN contract_lines cln ON (cln.qmd_qmd_id = qmd.qmd_id) ");
		stmt.append("  JOIN fleet_masters fms ON (fms.fms_id = cln.fms_fms_id) ");
		stmt.append(" WHERE fms.unit_no = :unitNo ");
		stmt.append("   AND cln.cln_id = (SELECT max(cln1.cln_id) FROM contract_lines cln1 ");
		stmt.append("                      WHERE cln1.fms_fms_id = fms.fms_id) ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("unitNo", unitNo);
		
		records = query.getResultList();
		
		for(Object[] record : records) {
			int i = 0;
			prd.setProductCode((String)record[i]);
			prd.setProductType((String)record[i+=1]);
			prd.setDescription((String)record[i+=1]);
		}
		
		return prd;
	}

	@Override
	public String getFleetStatus(String unitNo) {
		String fleetStatusCode;

		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT fl_status.fleet_status(fms_id) fleetStatus FROM fleet_masters WHERE unit_no=:unitNo");
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("unitNo", unitNo);

		fleetStatusCode = String.valueOf(query.getSingleResult());

		return fleetStatusCode;
	}

	@Override
	public Boolean isVehicleOffLease(Long fmsId) {
		StringBuilder stmt = new StringBuilder();

		stmt.append("SELECT cln_id FROM contract_lines WHERE cln_id = fl_contract.get_contract_line (:fmsId)");
		stmt.append(" AND out_of_service_date IS NOT NULL ");
		stmt.append(" AND actual_end_date IS NOT NULL ");
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		boolean clnId = DataUtil.convertToBoolean((String)query.getSingleResult());
		
		return clnId;
	}

	@Override
	public Boolean isVehicleDisposed(Long fmsId) {
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT 'XxX' ");
		stmt.append(" FROM fleet_masters fms, disposal_requests dr, asset_item ai, contract_lines cln ");
		stmt.append(" WHERE fms.fms_id = :fmsId ");
		stmt.append(" AND dr.fms_id = ai.fleet_id ");
		stmt.append(" AND ai.fleet_id = fms.fms_id ");
		stmt.append(" AND cln.cln_id = fl_contract.get_contract_line (fms.fms_id) ");
		stmt.append(" AND dr.drq_status = ANY ('C','3','4') ");
		stmt.append(" AND cln.out_of_service_date IS NOT NULL ");
		stmt.append(" FETCH FIRST ROW ONLY ");

		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);

		boolean hasDisposal = DataUtil.convertToBoolean((String) query.getSingleResult());

		return hasDisposal;
	}

	@SuppressWarnings("unchecked")
	public UnitVO getUnitInfo(UnitVO unitVO) {
		BigDecimal tmp;
		List<Object[]> records;
		
		StringBuilder stmt = new StringBuilder();
		stmt.append(" SELECT fms.unit_no, mmy.model_mark_year_desc, mak.make_desc, mrg.make_model_desc, mdl.equipment_flag, ");
		stmt.append("   vin, mt.model_type_desc, ft.fuel_desc, ");
		stmt.append(" 	(SELECT DISTINCT d.sub_acc_code ");
		stmt.append("	    FROM doc d ");
		stmt.append("	    JOIN dist on (d.doc_id = dist.doc_id) ");
		stmt.append("	   WHERE d.doc_type = 'PORDER' ");
		stmt.append("	     AND d.doc_status = 'R' ");
		stmt.append("	     AND d.order_type = 'M' ");
		stmt.append("     AND dist.CDB_CODE_1 = to_char(fms.fms_id)) delivery_account_code, ");
		stmt.append("	(SELECT COUNT(DISTINCT cln.con_con_id) ");
		stmt.append("		    FROM contract_lines cln ");
		stmt.append("		   WHERE cln.fms_fms_id = fms.fms_id) contract_count, ");		
		stmt.append("        fms.gross_veh_wt, mdl.brake_horse_power, fms.retail_price ");
		stmt.append("   FROM fleet_masters fms, models mdl, makes mak, model_mark_years mmy, model_types mt, fuel_types ft, make_model_ranges mrg ");
		stmt.append("  WHERE fms.fms_id = :fmsId ");
		stmt.append("	 AND fms.mdl_mdl_id = mdl.mdl_id ");
		stmt.append("    AND mdl.mak_mak_id = mak.mak_id ");
		stmt.append("    AND mdl.mmy_mmy_id = mmy.mmy_id ");
		stmt.append("    AND mdl.mtp_mtp_id = mt.mtp_id ");
		stmt.append("    AND mdl.ftp_ftp_id = ft.ftp_id ");
		stmt.append("    AND mdl.mrg_mrg_id = mrg.mrg_id ");

		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", unitVO.getFmsId());

		records = query.getResultList();

		for (Object[] record : records) {
			int i = 0;
			unitVO.setUnitNo((String) record[i]);
			unitVO.setYear((String) record[i += 1]);
			unitVO.setMake((String) record[i += 1]);
			unitVO.setModel((String) record[i += 1]);
			unitVO.setEquipmentFlag((String) record[i += 1]);
			unitVO.setVin((String) record[i += 1]);
			unitVO.setModelTypeDesc((String) record[i += 1]);
			unitVO.setFuelType((String) record[i += 1]);
			unitVO.setDeliverAccCode((String) record[i += 1]);
				tmp = ((BigDecimal)record[i+=1]);
			unitVO.setContractCount(tmp == null ? 0 : tmp.intValue());
				tmp = ((BigDecimal)record[i+=1]);
			unitVO.setGvr(tmp == null ? null : tmp.longValue());
			unitVO.setHorsePower((BigDecimal) record[i += 1]);
			unitVO.setMsrp((BigDecimal) record[i += 1]);
			
			if(unitVO.getContractCount() > 1 || CommonConstants.PUR_LEASEBACK_DELIV_DLR.equals(unitVO.getDeliverAccCode())) {
				unitVO.setNewUsed("Used");
			} else {
				unitVO.setNewUsed("New");
			}
		}
			
		return unitVO;
	}
}

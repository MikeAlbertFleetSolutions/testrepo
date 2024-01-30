package com.mikealbert.accounting.processor.dao;

import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.DriverCostCenter;
import com.mikealbert.accounting.processor.vo.CostCenterVO;

public class DriverCostCenterDAOImpl extends GenericDAOImpl<DriverCostCenter, Long> implements DriverCostCenterDAOCustom{
	private static final long serialVersionUID = 6538040234088488317L;

	@Override
	public CostCenterVO getActiveCostCenter(Long cId, String accountType, String accountCode, Long drvId, Date date) {
		StringBuilder stmt;
		Query query;
		Object[] record;
		CostCenterVO costCenter = null;
		
		stmt = new StringBuilder();
		stmt.append("SELECT drcc.cost_centre_code, ccc.description ");
		stmt.append("  FROM driver_cost_centres drcc, cost_centre_codes ccc ");
		stmt.append("  WHERE drcc.cost_centre_code = ccc.cost_centre_code ");
		stmt.append("    AND drcc.cocc_c_id = ccc.ea_c_id ");
		stmt.append("    AND drcc.cocc_account_type = ccc.ea_account_type ");
		stmt.append("    AND drcc.cocc_account_code = ccc.ea_account_code ");		
		stmt.append("    AND drcc.cocc_c_id = :cId ");
		stmt.append("    AND drcc.cocc_account_type = :accountType ");
		stmt.append("    AND drcc.cocc_account_code = :accountCode ");
		stmt.append("    AND drcc.drv_drv_id = :drvId ");
		stmt.append("    AND drcc.effective_to_date IS NOT NULL ");
		stmt.append("    AND trunc(drcc.effective_to_date) <= trunc(:date) ");
		stmt.append("    AND trunc(drcc.effective_to_date) >= trunc(:date) ");
		
		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("drvId", drvId);
		query.setParameter("date", date);		
			
		try {
			record = (Object[]) query.getSingleResult();
		} catch(NoResultException nre) {
			record = null;
		}
		
		if(record == null) {
			stmt = new StringBuilder();
			stmt.append("SELECT drcc.cost_centre_code, ccc.description ");
			stmt.append("  FROM driver_cost_centres drcc, cost_centre_codes ccc ");
			stmt.append("  WHERE drcc.cost_centre_code = ccc.cost_centre_code ");
			stmt.append("    AND drcc.cocc_c_id = ccc.ea_c_id ");
			stmt.append("    AND drcc.cocc_account_type = ccc.ea_account_type ");
			stmt.append("    AND drcc.cocc_account_code = ccc.ea_account_code ");
			stmt.append("    AND drcc.cocc_c_id = :cId");
			stmt.append("    AND drcc.cocc_account_type = :accountType ");
			stmt.append("    AND drcc.cocc_account_code = :accountCode ");
			stmt.append("    AND drcc.drv_drv_id = :drvId ");
			stmt.append("    AND drcc.effective_to_date IS NULL ");
		
			query = entityManager.createNativeQuery(stmt.toString());
			query.setParameter("cId", cId);
			query.setParameter("accountType", accountType);
			query.setParameter("accountCode", accountCode);
			query.setParameter("drvId", drvId);
		
			try {
				record = (Object[]) query.getSingleResult();			
			} catch(NoResultException nre) {
				record = null;
			}
		}
		
		if(record != null) {
			costCenter = new CostCenterVO((String)record[0], (String)record[1]);
		}
		
		return costCenter;
	}

}

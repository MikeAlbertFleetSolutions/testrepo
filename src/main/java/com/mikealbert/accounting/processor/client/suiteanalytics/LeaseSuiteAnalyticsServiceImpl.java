package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("leaseSuiteAnalyticsService")
public class LeaseSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements  LeaseSuiteAnalyticsService {

	/**
	 * @author Saket.Maheshwary
	 * @return A list of map which has details of all the leases.
	 */
	@Override
	public List<Map<String, Object>> getAllExternalLeases() {
		List<Map<String, Object>> leases;

		StringBuilder sql = new StringBuilder()
				.append("SELECT id, externalid, name, custrecord_lma_ls_status, custrecord_ma_termination_flag, ")
				.append("    (SELECT name FROM customrecord_cseg_mafs_unit WHERE id = cseg_mafs_unit) AS unit_no ")
				.append("  FROM customrecord_lma_lease ");
		
		leases = this.execute(sql.toString());
		leases = leases == null ? new ArrayList<Map<String, Object>>(0) : leases; 		
		return leases;
	}
	
	/**
	 * @author Saket.Maheshwary
	 * @return A list of map which has details of a lease retrieved on the basis of externalId.
	 */
	@Override
	public List<Map<String, Object>> getExternalLeaseByExternalId(String externalId) {
		List<Map<String, Object>> leases;
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT * ");
		sql.append("  FROM customrecord_lma_lease ");
		sql.append(" WHERE externalid = '%s' ");
		
		System.out.println(String.format(sql.toString(), externalId));
		
		leases = this.execute(String.format(sql.toString(), externalId));
		leases = leases == null ? new ArrayList<Map<String, Object>>(0) : leases; 		
		return leases;
	}

}

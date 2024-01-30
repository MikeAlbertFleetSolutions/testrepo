package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("unitSuiteAnalyticsService")
public class UnitSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements  UnitSuiteAnalyticsService {

	@Override
	public List<Map<String, Object>> getAllExternalUnits() {
		List<Map<String, Object>> units;

		StringBuilder sql = new StringBuilder()
				.append("SELECT * ")
				.append("  FROM customrecord_cseg_mafs_unit ");
		
		units = this.execute(sql.toString());

		return units == null ? new ArrayList<Map<String, Object>>(0) : units; 		
	}
	
	@Override
	public List<Map<String, Object>> getExternalUnitByExternalId(String externalId) {
		List<Map<String, Object>> units;
		
		StringBuilder sql = new StringBuilder()
				.append("SELECT * ")
		        .append("  FROM customrecord_cseg_mafs_unit ")
		        .append(" WHERE externalid = %s ");
		
		units = this.execute(String.format(sql.toString(), externalId));
		
		return units == null ? new ArrayList<Map<String, Object>>(0) : units; 		
	}

}

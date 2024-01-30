package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("itemSuiteAnalyticsService")
public class ItemSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements ItemSuiteAnalyticsService{
	
	@Override
	public Map<String, Object> get(String name) {
		List<Map<String, Object>> result;
		
		StringBuilder sql = new StringBuilder()		
		       .append(" SELECT to_number(i.id) as type_id  ")
		       .append("   FROM item i ")
		       .append("   WHERE isinactive = 'F' ")
		       .append("     AND i.fullname = '%s' ");	   			   			   			   			   
		
		String stmt = String.format(sql.toString(), name);
		
		result = super.execute(stmt);
		
		return result == null ? null : result.get(0); 
	}

}

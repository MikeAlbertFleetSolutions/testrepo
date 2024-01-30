package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.util.data.DateUtil;

@Service("purchaseOrderSuiteAnalyticsService")
public class PurchaseOrderSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements PurchaseOrderSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<Map<String, Object>> findClosed(Date from, Date to) throws Exception {
		String start,end;
		if(isGmtConvert.equalsIgnoreCase("true")){
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else {
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
    	
		StringBuilder sql = new StringBuilder()
				.append("SELECT txn.externalId ")
				.append("  FROM transaction txn ")
				.append("  WHERE txn.externalId IS NOT NULL ")
				.append("    AND txn.recordType = 'purchaseorder' ")
				.append("    AND txn.status LIKE '%%:H' ")
				.append("    AND txn.lastmodifieddate BETWEEN %s and %s ");				
 	    	
		String stmt = String.format(sql.toString(), start, end); 
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result; 
	}
}

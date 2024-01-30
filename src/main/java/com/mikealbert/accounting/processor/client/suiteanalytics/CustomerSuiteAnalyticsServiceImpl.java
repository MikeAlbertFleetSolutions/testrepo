package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.util.data.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("customerSuiteAnalyticsService")
public class CustomerSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements CustomerSuiteAnalyticsService {

	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	
	@Override
	public List<Map<String, Object>> getCustomers(Date from, Date to) throws Exception {

		String start, end;
    	if(isGmtConvert.equalsIgnoreCase("true"))
		{
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else {
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);

		}

    	
		StringBuilder sql = new StringBuilder()
				.append(" SELECT to_number(c.id) AS internalId, c.externalid AS externalId, c.entityid AS accountCode, c.companyName AS companyName  ")
				.append("   FROM customer c ")
				.append("     WHERE length(c.entityid) <= 9 ")
				.append("       AND ( c.datecreated > %s ")
				.append("           OR c.lastmodifieddate BETWEEN %s and %s  ) ");				
 	    	
		String stmt = String.format(sql.toString(), start, start, end); 
		
		List<Map<String, Object>> customers = super.execute(stmt);
		
		return customers == null ? new ArrayList<Map<String, Object>>(0) : customers; 
	}
}

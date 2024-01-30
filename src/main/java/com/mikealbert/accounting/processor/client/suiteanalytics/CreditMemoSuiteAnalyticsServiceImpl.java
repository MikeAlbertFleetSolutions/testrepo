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

@Service("creditMemoSuiteAnalyticsService")
public class CreditMemoSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements CreditMemoSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<Map<String, Object>> findUpdatedUngroupedCreditMemos(Date from, Date to) throws Exception {
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
    	
		//TODO This query will have to be updated to look at the line id on the line level as part of the Phase 4.1 release
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT to_char(txn.id) AS internalId, txn.tranid, txn.transactionnumber, txn.externalid AS externalId, txn.custbody_ma_group_inv_number as group_number ")
				.append("   FROM transaction txn ")
				.append("     JOIN customlist_ma_type_list cmtl ON cmtl.id = txn.custbody_ma_type AND cmtl.name IN ('FLBILLING', 'FLMISC', 'FLMAINT') ")
				.append("     JOIN accountingPeriod apd ON apd.id = txn.postingperiod ")
				.append("   WHERE txn.recordtype in ('creditmemo') ")
				.append("     AND (txn.custbody_ma_for_group_invoice = 'F' AND txn.custbody_ma_group_inv_number IS NULL) ")
				.append("     AND txn.custbody_ma_doc IS NOT NULL ")
				.append("     AND txn.lastmodifieddate between %s and %s ")
				.append("     AND apd.endDate > SYSDATE - %d ");				
 	    	
		String stmt = String.format(sql.toString(), start, end, TRAILING_THRESHOLD_DAYS); 
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findByCustomerAndAccountingPeriod(String customerInternalId, String accountingPeriodInternalId) throws Exception {
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT to_char(cmo.id) AS internalId, cmo.tranid, cmo.externalid AS externalId  ")
				.append("   FROM transaction cmo ")
				.append("   WHERE cmo.recordtype in ('creditmemo') ")
				.append("     AND cmo.custbody_ma_ci_link IS NOT NULL ")
				.append("     AND cmo.externalid IS NOT NULL ")
				.append("     AND cmo.entity = %s ")
				.append("     AND cmo.postingperiod = %s ");
				
		String stmt = String.format(sql.toString(), customerInternalId, accountingPeriodInternalId); 
		
		List<Map<String, Object>> result = super.execute(stmt);
				
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}
}

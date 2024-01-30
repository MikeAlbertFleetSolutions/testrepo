package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.util.data.DateUtil;

@Service("transactionSuiteAnalyticsService")
public class TransactionSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements TransactionSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<Map<String, Object>> findUpdatedClientTransactionGroups(Date from, Date to) throws Exception {

		String start,end;
		if(isGmtConvert.equalsIgnoreCase("true")){
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else{
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}

		StringBuilder sql = new StringBuilder()
		        .append(" SELECT DISTINCT to_char(apd.id) AS accounting_period_id, apd.enddate AS ending, to_char(txn.entity) AS customer_internal_id, cust.externalid AS customer_external_id, ccih.custrecord_ci_doc_num AS group_number ")
				.append("   FROM transaction txn, customer cust, accountingPeriod apd, customrecord_consolidated_inv_header ccih ")
				.append("   WHERE txn.entity = cust.id ")
				.append("     AND txn.custbody_ma_ci_link = ccih.id ")
				.append("     AND txn.postingperiod = apd.id ")
				.append("     AND txn.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("     AND txn.custbody_ma_for_group_invoice = 'T' ")
				.append("     AND txn.externalid IS NOT NULL ")
				.append("     AND txn.lastmodifieddate between %s and %s ")
				.append("     AND apd.endDate > SYSDATE - %d ")
				.append("     AND NOT EXISTS( SELECT 1 ")
				.append("                       FROM transaction txn2 ")
				.append("                       WHERE txn2.postingperiod = txn.postingperiod ")
				.append("                         AND txn2.entity = txn.entity ")
				.append("                         AND txn2.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("                         AND txn2.custbody_ma_for_group_invoice = txn.custbody_ma_for_group_invoice  ")
				.append("                         AND txn2.custbody_ma_ci_link IS NULL ")
				.append("                         AND txn2.externalid IS NOT NULL ) ");

		String stmt = String.format(sql.toString(), start, end, TRAILING_THRESHOLD_DAYS); 
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}
		
	@Override
	public List<Map<String, Object>> findUpdatedUngroupedClientBillingTransactions(Date base, Date from, Date to) throws Exception {
		String start,end;
		String startPeriod = String.format("to_date('%s', '%s')", DateUtil.convertToString(base, DateUtil.PATTERN_DATE_TIME), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);

		if(isGmtConvert.equalsIgnoreCase("true")){
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else{
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		
				StringBuilder sql = new StringBuilder()
						.append(" SELECT DISTINCT cust.externalid AS customer_external_id, apd.periodname AS period_name")
						.append("   FROM transaction txn ")
						.append("     JOIN customer cust ON cust.id = txn.entity ")
						.append("     JOIN accountingPeriod apd ON apd.id = txn.postingperiod ")
						.append("   WHERE txn.recordtype IN ('creditmemo', 'invoice') ")
						.append("     AND (txn.custbody_ma_for_group_invoice = 'F' AND custbody_ma_group_inv_number IS NULL) ")
						.append("     AND txn.status IN ('CustCred:A', 'CustCred:B', 'CustInvc:A', 'CustInvc:B') ")
						.append("     AND apd.startdate >= %s ")
						.append("     AND txn.lastmodifieddate BETWEEN %s AND %s ")
						.append("     AND apd.endDate > SYSDATE - %d ");
		
				String stmt = String.format(sql.toString(), startPeriod, start, end, TRAILING_THRESHOLD_DAYS); 
				
				List<Map<String, Object>> result = super.execute(stmt);
		
				return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findUpdatedGroupedClientBillingTransactions(Date base, Date from, Date to) throws Exception {
		String start,end;
		String startPeriod = String.format("to_date('%s', '%s')", DateUtil.convertToString(base, DateUtil.PATTERN_DATE_TIME), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);

		if(isGmtConvert.equalsIgnoreCase("true")){
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else{
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}


		StringBuilder sql = new StringBuilder()
				.append(" SELECT DISTINCT cust.externalid AS customer_external_id, apd.periodname AS period_name")
				.append("   FROM transaction txn ")
				.append("     JOIN customer cust ON cust.id = txn.entity ")
				.append("     JOIN accountingPeriod apd ON apd.id = txn.postingperiod ")
				.append("   WHERE txn.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("     AND ( (txn.custbody_ci_invoice_consolidated = 'T' ) OR (txn.custbody_mafs_has_been_emailed   = 'T') ) ")
				.append("     AND apd.startdate >= %s ")
				.append("     AND txn.lastmodifieddate BETWEEN %s AND %s ")
				.append("     AND apd.endDate > SYSDATE - %d ")
				.append("     AND NOT EXISTS( SELECT 1 ")
				.append("                       FROM transaction txn2 ")
				.append("                       WHERE txn2.postingperiod = txn.postingperiod ")
				.append("                         AND txn2.entity = txn.entity ")
				.append("                         AND txn2.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("                         AND txn2.custbody_ma_for_group_invoice = txn.custbody_ma_for_group_invoice  ")
				.append("                         AND txn2.custbody_ma_ci_link IS NULL ")
				.append("                         AND txn2.externalid IS NOT NULL ) ");				

		String stmt = String.format(sql.toString(), startPeriod, start, end, TRAILING_THRESHOLD_DAYS); 
		
		List<Map<String, Object>> result = super.execute(stmt);

		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}
		
	@Override
	public List<Map<String, Object>> findPaymentsByInvoice(String internalId, String externalId) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT to_char(ntll.nextdoc) AS internal_id, txn.trandate, ");
		sql.append("     (SELECT txn2.recordtype FROM transaction txn2 WHERE txn2.id = ntll.nextdoc) AS transaction_type ");
		sql.append("   FROM transaction txn, NextTransactionLineLink ntll");
		
		if(internalId != null) {
			sql.append(" WHERE txn.id = '%s' ");
		} else {
			sql.append(" WHERE txn.externalid = '%s' ");
		}
		
		sql.append("       AND ntll.previousdoc = txn.id ");
		sql.append("       AND ntll.linktype = 'Payment' ");
    	
		String stmt = null;
		if(internalId != null) {
			stmt = String.format(sql.toString(), internalId);
		} else {
			stmt = String.format(sql.toString(), externalId);
		}
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findAllClientTransactionGroupsByAccountingPeriod(List<String> accountingPeriodIds) throws Exception {
		if(accountingPeriodIds.isEmpty()) return new ArrayList<Map<String, Object>>(0);
				
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT DISTINCT to_char(apd.id) AS accounting_period_id, apd.enddate AS ending, to_char(inv.entity) AS customer_internal_id, cust.externalid AS customer_external_id, ccih.custrecord_ci_doc_num AS group_number ")
				.append("   FROM transaction inv ")
				.append("     JOIN customer cust ON cust.id = inv.entity ")
				.append("     JOIN accountingPeriod apd ON apd.id = inv.postingperiod ")
				.append("     LEFT JOIN customrecord_consolidated_inv_header ccih ON ccih.id = inv.custbody_ma_ci_link ")
				.append("   WHERE inv.postingperiod IN (%s) ")
				.append("     AND inv.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("     AND (inv.custbody_ci_invoice_consolidated = 'T' ) OR (inv.custbody_mafs_has_been_emailed   = 'T') ")
				.append("     AND inv.externalid IS NOT NULL ");

		String stmt = String.format(sql.toString(), accountingPeriodIds.stream().map(Object::toString).collect(Collectors.joining(",")));
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findGroupableTransactionsByAccountingPeriodAndClientExternalId(String accountingPeriodId, String clientExternalId) throws Exception {
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT to_char(txn.postingperiod) as accounting_period_id, cust.externalid AS customer_external_id, to_char(txn.id) AS internal_id, txn.externalid AS external_id, txn.tranid, txn.recordtype AS transaction_type, ccih.custrecord_ci_doc_num AS group_number ")
				.append("   FROM transaction txn ")
				.append("     JOIN customer cust ON cust.id = txn.entity  ")
				.append("     LEFT JOIN customrecord_consolidated_inv_header ccih ON ccih.id = txn.custbody_ma_ci_link ")
				.append("   WHERE txn.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("     AND txn.postingperiod = %s ")
				.append("     AND txn.custbody_ma_for_group_invoice = 'T' ")
				.append("     AND txn.externalid IS NOT NULL ")
				.append("     AND cust.id = txn.entity ")
				.append("     AND cust.externalid = '%s' ");

		String stmt = String.format(sql.toString(), accountingPeriodId, clientExternalId);
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findByPeriodAndMaType(String period, String maType) throws Exception {
		StringBuilder sql = new StringBuilder()
		        .append("SELECT txn.* ")
		        .append("  FROM transaction txn, accountingperiod apd, customer cust, customlist_ma_type_list mtl ")
		        .append("    WHERE txn.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")								
				.append("      AND apd.id = txn.postingPeriod ")
		        .append("      AND apd.periodName = '%s' ")
		        .append("      AND cust.id = txn.entity ")
		        .append("      AND mtl.id = txn.custbody_ma_type ");

				if(maType != null) {
					sql.append("      AND mtl.name = '%s' ");
				}

		String stmt = String.format(sql.toString(), period, maType);
				
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public boolean isGroupInvoiceDone(String accountingPeriodId, String clientInternalId, String clientExternalId) throws Exception {
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT COUNT(1) AS cnt")
				.append("   FROM transaction txn  ")
				.append("     JOIN customer cust ON cust.id = txn.entity   ")
				.append("   WHERE txn.postingperiod = '%s' ")
				.append("     AND (txn.entity = '%s' OR cust.externalid = '%s') ")
				.append("     AND txn.recordtype IN ('invoice', 'creditmemo', 'depositapplication') ")
				.append("     AND txn.custbody_ma_for_group_invoice = 'T' ")
				.append("     AND txn.custbody_ma_ci_link IS NULL ")
				.append("     AND txn.externalid IS NOT NULL ");

		String stmt = String.format(sql.toString(), accountingPeriodId, clientInternalId, clientExternalId);
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		if(result.get(0).get("cnt") != null) {
			return Integer.parseInt(result.get(0).get("cnt").toString()) == 0;
		} else {
			throw new Exception("No result found for query: " + stmt);  
		}
	}
}

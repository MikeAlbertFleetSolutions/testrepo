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

@Service("invoiceSuiteAnalyticsService")
public class InvoiceSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements InvoiceSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<Map<String, Object>> getDisposalInvoice(Date from, Date to) throws Exception {
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
		        .append(" SELECT to_char(inv.id) AS transaction_id, to_char(inv.externalid) AS transaction_extid, to_char(inv.custbody_ma_doc) as docid ")
				.append("   FROM transaction inv, customlist_ma_type_list malist, accountingPeriod apd ")
				.append("   WHERE ( (inv.createddate BETWEEN %s and %s) OR (inv.lastmodifieddate BETWEEN %s and %s) ) ")
				.append("     AND inv.custbody_ma_type = malist.id  ")
				.append("     AND malist.name = 'FLDISPOSAL' ")
				.append("     AND inv.recordtype = 'invoice' ")
				.append("     AND inv.postingPeriod = apd.id ");
 	    	
		String stmt = String.format(sql.toString(), start, end, start, end); 

		List<Map<String, Object>> result = super.execute(stmt);

		return result == null ? new ArrayList<Map<String, Object>>(0) : result;		
	}

	@Override
	public List<Map<String, Object>> findGroupedInvoicesWithDepositApplication(String customerInternalId, String accountingPeriodInternalId, String groupNumber) throws Exception {

		//TODO This query will have to be updated to look at the line id on the line level as part of the Phase 4.1 release
		StringBuilder sql = new StringBuilder()
		        .append(" SELECT to_char(dap.id) AS depositApplicationInternalId, dap.tranid AS depositApplicationTranId, to_char(inv.id) AS invoiceInternalId, inv.tranid AS invoiceTranId ")
				.append("   FROM transaction dap, NextTransactionLineLink ntll, transaction inv, customrecord_consolidated_inv_header ccih ")
				.append("   WHERE dap.recordtype = 'depositapplication' ")
				.append("     AND ntll.nextdoc = dap.id ")
				.append("     AND ntll.linktype = 'Payment' ")
				.append("     AND ntll.nexttype = 'DepAppl' ")
				.append("     AND ntll.previoustype = 'CustInvc' ")
				.append("     AND inv.id = ntll.previousdoc ")
				.append("     AND nvl(inv.custbody_ma_for_group_invoice, 'F') = 'T' ")
				.append("     AND inv.custbody_ma_ci_link = ccih.id ")
				.append("     AND inv.externalid IS NOT NULL ")
				.append("     AND inv.custbody_ma_doc IS NOT NULL ")
				.append("     AND inv.custbody_ma_lineid IS NOT NULL ")
				.append("     AND inv.entity = %s ")
				.append("     AND inv.postingperiod = %s ")
				.append("     AND ccih.custrecord_ci_doc_num = '%s' ");

		String stmt = String.format(sql.toString(), customerInternalId, accountingPeriodInternalId, groupNumber); 
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

	@Override
	public List<Map<String, Object>> findGroupedInvoices(String customerInternalId, String customerExternalId, String accountingPeriodInternalId, String groupNumber) throws Exception {
		String customerId;

		StringBuilder sql = new StringBuilder()
		        .append(" SELECT to_char(txn.id) AS internalId, to_char(txn.externalId) AS externalId ")
		        .append("   FROM transaction txn ")
		        .append("   JOIN customer cust ON cust.id = txn.entity ")
		        .append(" WHERE txn.recordtype = 'invoice' ")
		        .append("   AND nvl(txn.custbody_ma_for_group_invoice, 'F') = 'T' ")
		        .append("   AND txn.postingPeriod = '%s' ")
		        .append("   AND txn.custbody_ma_group_inv_number = '%s' ");
				
		if(customerInternalId != null) {
			sql.append(" AND txn.entity = '%s' ");
			customerId = customerInternalId;
		} else {
			sql.append(" AND cust.externalId = '%s' ");			
			customerId = customerExternalId;
		}

		String stmt = String.format(sql.toString(), accountingPeriodInternalId, groupNumber, customerId); 
		
		List<Map<String, Object>> result = super.execute(stmt);
		
		return result == null ? new ArrayList<Map<String, Object>>(0) : result;
	}

}

package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.constant.enumeration.EnvironmentEnum;
import com.mikealbert.util.data.DateUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("vendorBillPaymentSuiteAnalyticsService")
public class VendorBillPaymentSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements VendorBillPaymentSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	@Override
	public List<Map<String, Object>> getVehiclePayments(Date from, Date to) throws Exception {		
		String start, end;

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
				.append(" SELECT po.externalid AS externalId, pm.name AS paymentMethod, pymt.trandate AS paymentDate ")
				.append("   FROM transaction po ")
				.append("     JOIN NextTransactionLineLink po2inv ON po2inv.previousdoc = po.id AND po2inv.previoustype = 'PurchOrd' AND po2inv.nexttype = 'VendBill' AND po2inv.linktype = 'OrdBill' ")
				.append("     JOIN NextTransactionLineLink inv2pyt ON inv2pyt.previousdoc = po2inv.nextdoc  AND inv2pyt.previoustype = po2inv.nexttype AND inv2pyt.nexttype IN ('VendPymt', 'VPrepApp') AND inv2pyt.linktype = 'Payment' ")
				.append("     JOIN transaction inv ON inv.id = inv2pyt.previousdoc AND inv.custbody_ma_main_vehicle = 'T' AND inv.status = 'B' ")
				.append("     JOIN transaction pymt ON pymt.id = inv2pyt.nextdoc AND pymt.recordtype IN ('vendorpayment', 'vendorprepaymentapplication') AND pymt.createddate BETWEEN %s AND %s ")
				.append("     JOIN vendor v ON v.id = pymt.entity ")
				.append("     JOIN customlist194 pm ON pm.id = v.custentity_mafs_default_payment_method ");

		String stmt = String.format(sql.toString(), start, end); 
		
		List<Map<String, Object>> payments = super.execute(stmt);
		
		return payments == null ? new ArrayList<Map<String, Object>>(0) : payments; 
	}

}

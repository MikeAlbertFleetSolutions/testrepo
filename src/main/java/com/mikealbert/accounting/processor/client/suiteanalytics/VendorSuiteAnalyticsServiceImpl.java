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

@Service("vendorSuiteAnalyticsService")
public class VendorSuiteAnalyticsServiceImpl extends BaseSuiteAnalyticsService implements VendorSuiteAnalyticsService {
	@Value("${mafs.gmt.convert:true}")
	private String isGmtConvert;
	
	@Override
	public List<Map<String, Object>> getVendors(Date from, Date to) throws Exception {
		List<Map<String, Object>> vendors;

		String start, end;

		if(isGmtConvert.equalsIgnoreCase("true")) {
			start = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", DateUtil.convertToGMTString(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		else{
			SimpleDateFormat stf = new SimpleDateFormat(CommonConstants.DATE_TIMESTAMP_PATTERN);
			start = String.format("to_date('%s', '%s')", stf.format(from), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
			end = String.format("to_date('%s', '%s')", stf.format(to), CommonConstants.ORACLE_DATE_TIMESTAMP_PATTERN);
		}
		StringBuilder sql = new StringBuilder()
				.append(" SELECT to_number(v.id) AS entityId, v.entityid AS accountCode, v.externalid AS vendorExtId, v.companyname AS accountName ")
				.append("   FROM vendor v ")
				.append("     LEFT JOIN customrecord_2663_entity_bank_details ebd ON ebd.custrecord_2663_parent_vendor = v.id AND ebd.custrecord_2663_entity_bank_type = '1' ")
				.append("   WHERE length(v.entityid) <= 9  ")
				.append("     AND ( ( v.datecreated > %s ")
				.append("               OR ( v.lastmodifieddate is not null and v.lastmodifieddate BETWEEN %s  AND %s ) ) ) ")
				.append("     OR ( (ebd.created > %s ")
				.append("             OR (ebd.lastmodified is not null AND ebd.lastmodified BETWEEN %s AND %s ) ) )");

		String stmt = String.format(sql.toString(), start, start, end, start, start, end); 
		
		vendors = super.execute(stmt);
		vendors = vendors == null ? new ArrayList<Map<String, Object>>(0) : vendors; 
		
		return vendors;
	}
		
	@Override
	public List<Map<String, Object>> getAddresses() throws Exception {
		List<Map<String, Object>> addresses;
		
		StringBuilder sql = generateAddressBaseSelect();
		sql.append("  ORDER BY eab.addressbookaddress ASC ");		
	
		String stmt = sql.toString(); 

		addresses = super.execute(stmt);
 		addresses = addresses == null ? new ArrayList<Map<String, Object>>(0) : addresses; 
		
		return addresses;		
	}
	
	@Override
	public List<Map<String, Object>> getAddresses(String entityId) throws Exception {
		List<Map<String, Object>> addresses;
		
		StringBuilder sql = generateAddressBaseSelect();		
		sql.append("    AND eab.entity = %s ");
		sql.append("  ORDER BY eab.addressbookaddress ASC ");		
	
		String stmt = String.format(sql.toString(),entityId); 

		addresses = super.execute(stmt);
 		addresses = addresses == null ? new ArrayList<Map<String, Object>>(0) : addresses; 
		
		return addresses;		
	}	
	
	@Override
	public List<Map<String, Object>> getAddressesByExternalId(String externalId) throws Exception {
		List<Map<String, Object>> addresses;
		
		StringBuilder sql = generateAddressBaseSelect();
		sql.append("    AND ea.custrecord_ma_external_id = %s ");		
		sql.append("  ORDER BY eab.addressbookaddress ASC ");		
	
		String stmt = String.format(sql.toString(), externalId);

		addresses = super.execute(stmt);
 		addresses = addresses == null ? new ArrayList<Map<String, Object>>(0) : addresses; 
		
		return addresses;		
	}
	
	private StringBuilder generateAddressBaseSelect() {
		StringBuilder sql = new StringBuilder()
				.append(" SELECT eab.defaultbilling AS isDefaulBillAddress, ea.addr1 AS addressLine1, ea.addr2 AS addressLine2, ")
				.append("     ea.addr3 AS addressLine3, ea.country, ea.state, ea.custrecord_ma_county AS county, ")
				.append("     ea.city, ea.zip, ea.custrecord_ma_child_vendor AS childVendor, ea.custrecord_ma_external_id AS externalId, to_number(eab.internalid) AS internalId, ")
				.append("     ea.attention ")
				.append("   FROM EntityAddressbook eab, EntityAddress ea ")
				.append("   WHERE ea.recordowner = eab.addressbookaddress ");
		
		return sql;
	}
}

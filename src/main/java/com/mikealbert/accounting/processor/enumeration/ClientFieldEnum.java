package com.mikealbert.accounting.processor.enumeration;

public enum ClientFieldEnum implements FieldEnum {	
	ACCOUNT_CODE("accountCode"),
	ADDRESS_COUNTY("custrecord_ma_county"),	
	ADDRESS_EXTERNAL_ID("custrecord_ma_external_id"),
	ADDRESS_VERTEX_COUNTRY("custrecord_ma_vertex_country"),	
	BANKRUPT_INDICATOR("custentity_mafs_bankrupt_ind"),
	CAPITAL_LIMIT_1("custentity_mafs_cap_limit_1"),
	CAPITAL_LIMIT_2("custentity_mafs_cap_limit_2"),	
	COLLECTOR("custentity_mafs_client_collector"),		
	COLLECTION_STATUS("custentity_mafs_client_collection_status"),
	CREDIT_LIMIT_1("custentity_mafs_credit_limit_1"),
	CREDIT_LIMIT_2("custentity_mafs_credit_limit_2"),
	CREDIT_MANAGEMENT_TYPE("custentity_mafs_cr_mgmt_type"),		
	CREDIT_SCORE("custentity_mafs_credit_score"),
	CREDIT_STATUS("custentity_mafs_client_cr_stat"),
	EXTERNAL_ID("externalId"),
	INCORPORATE_STATE("custentity_mafs_incorp_state"),		
	INTERIM_FINANCE_CHARGE("custentity_mafs_interim_finance_charge"),	
	INTERNAL_ID("internalId"),
	LAST_CREDIT_CHECK_DATE("custentity_mafs_cr_date_chk"),
	LAST_PAYMENT_AMOUNT("custentity_ma_last_payment_amount"),		
	LAST_PAYMENT_DATE("custentity_ma_last_payment_date"),
	PURCHASE_CREDIT_LIMIT("custentity_mafs_pur_credit_limit"),	
	RISK_DEPARTMENT_AMOUNT("custentity_mafs_risk_dep_amount"),
	SUPPRESS_FINANCE_CHARGE("custentity_mafs_suppress_finance_charge"),	
	SUPPRESS_STATEMENT("custentity_mafs_suppress_statement"),
	UNAPPLIED_BALANCE("custentity_ma_unapplied_funds"),	
	UNIT_LIMIT_1("custentity_mafs_unit_limit_1"),
	UNIT_LIMIT_2("custentity_mafs_unit_limit_2");
	
	private final String scriptId;

	private ClientFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
	
	public static ClientFieldEnum getField(String scriptId) {
		for(ClientFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
				return field;
			}
		}
		throw new IllegalArgumentException("Unknown Scrip Id: " + scriptId);
	}
	
	public static boolean isField(String scriptId) {
		boolean retVal = false;
		for(ClientFieldEnum field : values()) {
			if(field.getScriptId().equals(scriptId)) {
		      retVal = true;
		      break;
			}
		}		
		return retVal;
	}
	
	@Override
	public String getScriptId() {
		return scriptId;
	}
}

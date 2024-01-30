package com.mikealbert.accounting.processor.enumeration;

public enum TransactionFieldEnum implements FieldEnum {
	DRIVER_ID("custcol_ma_drv_id"),
	DRIVER_FIRST_NAME("custcol_ma_driver_first_name_line"),
	DRIVER_LAST_NAME("custcol_ma_driver_last_name_line"),
	DRIVER_STATE("custcol_ma_driver_state"),
	DRIVER_COST_CENTER("custcol_ma_cc_line"),
	DRIVER_COST_CENTER_DESCRIPTION("custcol_ma_cost_center_desc"),
	DRIVER_RECHARGE_CODE("custcol_ma_driver_recharge_code"),
	DRIVER_FLEET_REF_NO("custcol_ma_flt_rf_num_line"),	
	INVOICE_DESCRIPTION("custcol_ma_invdesc_trn"),
	INVOICE_NOTE("custcol4"),
	LINE_DESCRIPTION("name"),
	LINE_MA_TYPE("custcol_ma_type"),	
	LINE_PAID_AMOUNT("custcol_ma_line_paid_amount"),
	MA_TRANSACTION_DATE("custcol_ma_trx_dt_line"),
	MA_TYPE("custrecord_ma_type"),
	MONTH_SERVICE_DATE("custcol_mon_serv_date"),
	REPORT_CATEGORY("custrecord_mareportcategory"),
	REPORT_SUB_CATEGORY("custrecord_mareportsubcategory"),
	TAX_DETAIL_OVERRIDE("taxDetailsOverride"),
	UNIT("custcol_cseg_mafs_unit");

	private final String scriptId;
	
	private TransactionFieldEnum(String scriptId) {
		this.scriptId = scriptId;
	}
		
	@Override
	public String getScriptId() {
		return scriptId;
	}
		
}

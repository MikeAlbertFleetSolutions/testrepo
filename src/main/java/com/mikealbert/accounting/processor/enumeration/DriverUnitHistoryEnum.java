package com.mikealbert.accounting.processor.enumeration;

public enum DriverUnitHistoryEnum implements FieldEnum{

	INTERNAL_ID("internalId"), 
	EXTERNAL_ID("externalId"), 
	DRV_ID("custrecordma_duh_driver_id"),
	UNIT_NO("custrecordma_duh_unit_no"), 
	CLIENT("custrecordma_duh_client"),
	DRV_FIRST_NAME("custrecordma_duh_first_name"), 
	DRV_LAST_NAME("custrecordma_duh_last_name"),
	DRV_ADD_LINE_1("custrecordma_duh_driver_address1_line1"),
	DRV_ADD_LINE_2("custrecordma_duh_driver_address1_line2"),
	DRV_ADD_CITY("custrecordma_duh_driver_address1_city"),
	DRV_ADD_STATE("custrecordma_duh_driver_address1_state"),
	DRV_ADD_ZIP_CODE("custrecordma_duh_driver_address1_zipcode"),
	DRV_ADD_COUNTY("custrecordma_duh_driver_address1_county"),
	DRV_ADD_COUNTRY("custrecordma_duh_driver_address1_country"),
	DRV_COST_CENTER("custrecordma_duh_cost_center"),
	DRV_COST_CENTER_DESC("custrecordma_duh_cost_center_desc"),
	DRV_RECHARGE_CODE("custrecord_duh_driver_recharge_code"),
	DEL_DEALER_ADD_LINE_1("custrecordma_duh_dd_address1_line1"),
	DEL_DEALER_ADD_LINE_2("custrecordma_duh_dd_address1_line2"),
	DEL_DEALER_ADD_CITY("custrecordma_duh_dd_address1_city"),
	DEL_DEALER_ADD_STATE("custrecordma_duh_dd_address1_state"),
	DEL_DEALER_ADD_ZIP_CODE("custrecordma_duh_dd_address1_zipcode"),
	DEL_DEALER_ADD_COUNTY("custrecordma_duh_dd_address1_county"),
	DEL_DEALER_ADD_COUNTRY("custrecordma_duh_dd_address1_country"),
	EFFECTIVE_FROM("custrecordma_duh_effective_from"),
	UNIT_FLEET_REF_NO("custrecord_duh_fleet_ref_no");

	private final String scriptId;

	private DriverUnitHistoryEnum(String scriptId) {
		this.scriptId = scriptId;
	}

	@Override
	public String getScriptId() {
		return scriptId;
	}
}
package com.mikealbert.accounting.processor.enumeration;

public enum PollerEnum {
	ASSET("POLL-ASSETS"),	
	BILLING_REPORT_REFRESH("POLL-BILLING-REPORT-REFRESH"),	
	CLIENT("POLL-CLIENTS"),
	CLIENT_TRANSACTION_GROUPS("POLL-CLIENT-TRANSACTION-GROUPS"),
	CLIENT_TRANSACTION_GROUP_COMPLETE("POLL-CLIENT-TXN-GROUP-COMPLETE"),	
	CLIENT_CREDIT_MEMOS("POLL-CLIENT-CREDIT-MEMOS"),	
	CLIENT_INVOICE_DEPOSITS("CLIENT_INVOICE_DEPOSITS"),	
	CONTRACT_IN_SERVICE_DATE_CHANGE("POLL-CONTRACT-IN-SERV-DATE-CHG"),
	DISPOSAL_INVOICE("POLL-DISPOSAL-INVOICE"),
	DRIVER_UNIT_HISTORY("POLL-DRIVER-UNIT-HISTORY"),	
	MAINTENANCE_INVOICE("POLL-MAINTENANCE-INVOICES"),
	PO_CLOSE_INBOUND("POLL-PO-CLOSE-INBOUND"),
	STOP_BILLING_EVENT("POLL-STOP-BILLING-EVENT"), 
	UNIT_UPSERT_EVENT("POLL-UNIT-UPSERT-EVENT"),	
	VENDOR("POLL-VENDORS"),
	VENDOR_BILL_PAYMENT("POLL-VENDOR-BILL-PAYMENTS"),
	LEASE_NOVATE("POLL-LEASE-NOVATE"),
	LEASE_INTEREST_UPDATE("POLL-LEASE-INTEREST-UPDATE"),
	LEASE_ACTUAL_END_DATE_UPDATE("POLL-LEASE-ACTUAL-END-DATE-UPD"),;
	
	private final String name;

	private PollerEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
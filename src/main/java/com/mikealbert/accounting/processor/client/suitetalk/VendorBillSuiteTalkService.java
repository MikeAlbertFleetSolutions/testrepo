package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Map;

import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

public interface VendorBillSuiteTalkService {
	public static final String APPROVAL_DEPARTMENT = "Accounting";	
	public static final String PAYABLE_ACCOUNT = "20010 Total Accounts Payable : Accounts Payable";
	
	Map<String, Object> get(String externalId) throws Exception;	
	
	public void create(InvoiceVO invoice) throws Exception;
	
	public void create(InvoiceVO invoice, PurchaseOrderVO purchaseOrder) throws Exception;
	
	public void delete(String externalId) throws Exception;
}

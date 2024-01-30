package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

public interface InvoiceService {
	static final String DISCOUNT_ITEM = "Discount";
	
	InvoiceVO getInvoice(Long docId, boolean fromPurchaseOrder) throws Exception;
	
	void updateGlAccToOne(Long docId) throws Exception;
	
	void create(InvoiceVO invoice) throws Exception;	
	void create(InvoiceVO invoice, PurchaseOrderVO purchaseOrder) throws Exception;
	
	void processNsToWillowDisposalInvoice(DisposalInvoiceVO disposalInvoice) throws Exception;
	
	List<Long> getMaintenanceInvoiceIds(Date start, Date end);
	
	List<DisposalInvoiceVO> getDisposalInvoiceList(Date start, Date end) throws Exception;
	
}

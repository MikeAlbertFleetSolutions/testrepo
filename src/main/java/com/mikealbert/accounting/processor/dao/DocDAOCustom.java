package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

public interface DocDAOCustom {
	
	BigDecimal getUnpaidPOTotal(Long quoId);
	
	InvoiceVO getInvoiceApHeaderByDocId(Long docId);
	
	CreditVO getCreditApHeaderByDocId(Long docId);
	
	List<InvoiceLineVO> getInvoiceApLinesByDocId(Long docId, boolean isVehicleInvoice);
	
	List<CreditLineVO> getCreditApLinesByDocId(Long docId);	
	
	Long getPurchaseOrderDocIdByInvoiceDocId(Long invoiceDocId);
	
	Long getInvoiceArIdFromInvoiceApId(Long invoiceApId);
	
	PurchaseOrderVO getPurchaseOrderHeaderByDocId(Long docId);
	
	PurchaseOrderLineVO getPurchaseOrderLineByDocId(Long docId);
	
	AssetCreateVO getAssetCreateData(AssetCreateVO asset);
	
	List<Long> getMaintenanceInvoiceIds(Date start, Date end);
	
	List<Long> getMaintenanceCreditIds(Date start, Date end);	
	
	Long findDrvIdByDocId(Long docId);
	
	boolean hasDistRecord(Long docId);
	
	BigDecimal getUnpaidPOTotalByFmsId(Long fmsId);
	
	void closeInternal(Long docId);
	
}

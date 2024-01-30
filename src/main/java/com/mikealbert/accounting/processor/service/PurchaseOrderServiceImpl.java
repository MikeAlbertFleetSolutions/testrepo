package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.client.suiteanalytics.PurchaseOrderSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.PurchaseOrderSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorBillSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.DriverCostCenterDAO;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkImmutableRecordException;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

@Service("purchaseOrderService")
public class PurchaseOrderServiceImpl extends TransactionService implements PurchaseOrderService {
	@Resource DocDAO docDAO;
	@Resource DriverCostCenterDAO driverCostCenterDAO;
	@Resource VendorService vendorService;
	@Resource XRefService xRefService;
	@Resource PurchaseOrderSuiteTalkService purchaseOrderSuiteTalkService;
	@Resource PurchaseOrderSuiteAnalyticsService purchaseOrderSuiteAnalyticsService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	@Transactional(readOnly = true)
	public PurchaseOrderVO get(Long docId) throws Exception {
		return getInternalPO(docId);
	}
	
	@Override
	public void add(PurchaseOrderVO po) throws Exception {
		try {
			purchaseOrderSuiteTalkService.add(po);
		} catch(SuiteTalkDuplicateRecordException e) {
			LOG.warn("Purchase Order {} already exists in the accounting system", po.getExternalId().toString());
		}
	}

	@Override
	public void closeExternal(Long docId) throws Exception {
		purchaseOrderSuiteTalkService.close(docId);		
	}
	
	/**
	 * Per business requirements, canceling/closing a PO to stock
	 * should not close the PO in the accounting system. 
	 * Instead, it should update the PO with current data 
	 * for the unit that is now a stock unit.
	 * 
	 * @param Long The docId of the internal canceled/closed PO
	 */
	@Override
	@Transactional(readOnly = true)
	public void update(Long docId) throws Exception {
		PurchaseOrderVO po = getInternalPO(docId);
		if(po == null) throw new Exception(String.format("Query did not yield an updated PO for docId %d", docId));
		
		try{
			purchaseOrderSuiteTalkService.update(po);		
		} catch(SuiteTalkImmutableRecordException e) {
			LOG.warn("Purchase Order {} cannot be updated, likely it is Fully Billed, in the accounting system", po.getExternalId().toString());			
		}
	}		

	/**
	 * Updates an existing purchase order (PO) by performing a two step process.
	 *
	 *  1.) Close existing PO(s) within the accounting system that have the same PO No.
	 *  2.) Create the updated PO in the accounting system. This will replace the closed PO(s). 
	 *  
	 *  The new PO should have a new docId/externalId. However, it should have the 
	 *  same PO No. as the PO(s) that were closed.
	 *  
	 *  @param docId Long The docId of the updated/revised PO
	 *  
	 *  @exception Exception for existence of revised PO. When it exists, do not process, raise exception (duplicate revision). 
	 */
	@Override
	@Transactional(readOnly = true)
	public void revise(Long docId) throws Exception {
		PurchaseOrderVO revisedPO = getInternalPO(docId);
		if(revisedPO == null) throw new Exception(String.format("Query did not yield a revised PO for docId %d", docId));
		
		List<Map<String, Object>> oldPOs = purchaseOrderSuiteTalkService.searchByPoNumberAndVendor(revisedPO.getTranId(), revisedPO.getVendor());

		Map<String, Object> duplicateRevisedPO = purchaseOrderSuiteTalkService.searchByExternalId(docId.toString());
		if(duplicateRevisedPO != null) {
			throw new Exception(String.format("Revised Purchase Order docId %s already exists in the external accounting system.", docId));
		}
		
		for(Map<String, Object> oldPO : oldPOs) {
			if(!"Closed".equals(oldPO.get(PurchaseOrderFieldEnum.STATUS.getScriptId()))) {
				Long oldDocId = Long.parseLong((String)oldPO.get(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId().toLowerCase()));
				
				LOG.info("Closing purchase order docId {} of {} total POs", oldDocId, oldPOs.size());
				
				purchaseOrderSuiteTalkService.close(oldDocId);
			}
		}
		
		purchaseOrderSuiteTalkService.add(revisedPO);
	}	
	
	/**
	 * Finds closed POs with a period of time
	 * 
	 * @param from Date begining of the search period
	 * @param to Date end of the search period
	 */
	@Override
	public List<Map<String, Object>> findClosedPOs(Date from, Date to) throws Exception {
		return purchaseOrderSuiteAnalyticsService.findClosed(from, to);
	}

	/**
	 * Retrieves the internal purchase order from the DB tables, DOC, DOCL, etc.
	 * 
	 * Assumption: PO is released and DIST records exists. If DIST is not there, this 
	 *             method will return null. A possible reason for the missing DIST records
	 *             is that the PO could have been 'C'anceled or revised at the same time this
	 *             PO is in route.
	 *             
	 * @param docId 
	 * @return
	 * @throws Exception When the query did not return a PO record. In this case the query criteria was not met, check the data.
	 */
	private PurchaseOrderVO getInternalPO(Long docId) throws Exception {
		PurchaseOrderVO po = null;
		
		if(docDAO.hasDistRecord(docId)) {
			po = docDAO.getPurchaseOrderHeaderByDocId(docId);
			if(po == null) throw new Exception("Purchase Order header query did not return a doc record for PO docId: " + docId);

			PurchaseOrderLineVO line = docDAO.getPurchaseOrderLineByDocId(docId);

			po
			.setApprovalDepartment(VendorBillSuiteTalkService.APPROVAL_DEPARTMENT)
			.setVendorAddressInternalId(getVendorInternalAddressId(po))
			.getLines().add(line);

			line.setPurchaseOrder(po);
			line = (PurchaseOrderLineVO) bindDepartment(line);
			line = bindItem(line);
			line = (PurchaseOrderLineVO) patchDepartment(line);		
			line = (PurchaseOrderLineVO) bindBusinessUnit(line);
			line = (PurchaseOrderLineVO) patchBusinessUnit(line);
		} else {
			LOG.warn("Dist records were not found for PO docId: {}", docId);			
		}
		
		return po;
	}

	@Override
	public BigDecimal getUnpaidPOTotalByFmsId(Long fmsId) {
		return docDAO.getUnpaidPOTotalByFmsId(fmsId);
	}

	@Override
	public void closeInternal(Long docId) throws Exception {
		docDAO.closeInternal(docId);
		
	}
	
}

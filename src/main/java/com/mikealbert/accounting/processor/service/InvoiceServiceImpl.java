package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.client.suiteanalytics.InvoiceSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorBillSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorCreditSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.item.InvoiceItemHelper;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.accounting.enumeration.TransactionStatusEnum;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@Service("invoiceService")
public class InvoiceServiceImpl extends TransactionService implements InvoiceService {
	@Resource DocDAO docDAO;
	@Resource XRefService xRefService;
	@Resource VendorService vendorService;
	@Resource InvoiceItemHelper invoiceItemHelper;
	@Resource VendorBillSuiteTalkService vendorBillSuiteTalkService;
	@Resource VendorCreditSuiteTalkService vendorCreditSuiteTalkService;
	@Resource ClientInvoiceService clientInvoiceService;
	@Resource ClientPaymentService clientPaymentService;
	@Resource ClientTransactionDAO clientTxnDAO;
	@Resource InvoiceSuiteAnalyticsService invoiceSuiteAnalyticsService;
			
	public InvoiceVO getInvoice(Long docId, boolean fromPurchaseOrder) throws Exception {
		InvoiceVO invoice = initializeInvoice(docId, fromPurchaseOrder);
		
		invoice.getLines().stream()
		.map(line -> line.setHeader(invoice))
		.map(line -> bindRebillableLicenseFee(line))		
		.map(line -> (InvoiceLineVO)bindDepartment(line))		
		.map(line -> (InvoiceLineVO)bindBusinessUnit(line))
		.map(line -> bindItem(line)) //TODO The patch methods relies on the item being set. Need to do something with the patch hack ASAP												
		.map(line -> (InvoiceLineVO)patchDepartment(line))
		.map(line -> (InvoiceLineVO)patchBusinessUnit(line))				
		.map(line -> bindExternalAssetType(line))
		.collect(Collectors.toList());		
		
		addDiscountLine(invoice);
		
		return invoice;
	}
		
	@Override
	public void create(InvoiceVO invoice) throws Exception {
		vendorBillSuiteTalkService.create(invoice);			
	}

	@Override
	public void create(InvoiceVO invoice, PurchaseOrderVO purchaseOrder) throws Exception {				
		vendorBillSuiteTalkService.create(invoice, purchaseOrder);				
	}	
		
	@Transactional
	@Override
	public void updateGlAccToOne(Long docId) throws Exception {
		Doc invoice = docDAO.findById(docId).orElse(null);
		invoice.setGlAcc(1L);
		docDAO.save(invoice);		
	}	
	
	@Override
	public List<Long> getMaintenanceInvoiceIds(Date start, Date end) {
		return docDAO.getMaintenanceInvoiceIds(start, end);
	}
	
	private InvoiceVO initializeInvoice(Long docId, boolean fromPurchaseOrder) throws Exception {
		InvoiceVO invoice = docDAO.getInvoiceApHeaderByDocId(docId);
		if(invoice == null) throw new Exception(String.format("Invoice header was not found for docId: %d", docId));
		
		List<InvoiceLineVO> lines = docDAO.getInvoiceApLinesByDocId(docId, fromPurchaseOrder);
		if(lines.isEmpty()) throw new Exception(String.format("At least one line item is needed to create a vendor bill. No invoice lines were not found for docId: %d", docId));
		
		invoice.setApprovalDepartment(VendorBillSuiteTalkService.APPROVAL_DEPARTMENT);
		invoice.setPayableAccount(VendorBillSuiteTalkService.PAYABLE_ACCOUNT);
		invoice.setLines(lines);
		invoice.setSubsidiary(Long.valueOf(xRefService.getExternalValue(XRefGroupNameEnum.COMPANY, String.valueOf(invoice.getSubsidiary()))));
		invoice.setVendorAddressInternalId(getVendorInternalAddressId(invoice));
		invoice.setCreateFromPurchaseOrder(fromPurchaseOrder);
		
		return invoice;
	}
			
	private InvoiceLineVO bindRebillableLicenseFee(InvoiceLineVO line) {
		Long invoiceArId = null;

		if(line.getHeader().getControlCode() == ControlCodeEnum.FLLICENSE) {
			invoiceArId = docDAO.getInvoiceArIdFromInvoiceApId(Long.valueOf(line.getHeader().getExternalId()));
		}
		
		line.setRebillableLicenseFee(invoiceArId == null ? false : true);
		
		return line;
	}
	
	private InvoiceLineVO bindExternalAssetType(InvoiceLineVO line) throws RuntimeException {
		try {
		if(line.getHeader().isCreateFromPurchaseOrder()) {
			String externalAssetType = xRefService.getExternalValue(XRefGroupNameEnum.INVOICE_ASSET_TYPE, line.getHeader().getControlCode().name());
			externalAssetType = externalAssetType == null ? null : externalAssetType.trim();
			line.setExternalAssetType(externalAssetType);
		}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return line;
	}
	
	/**
	 * When the invoice header contains an actual discount amount, 
	 * this method adds a discount line to the invoice. 
	 * @param invoice
	 * @throws Exception
	 */
	private void addDiscountLine(InvoiceVO invoice) throws Exception {
		if(!AI_OP_CODE.equals(invoice.getOpCode())) return;
		
		if(invoice.getDiscount() != null && invoice.getDiscount().compareTo(BigDecimal.ZERO) != 0) {
			InvoiceLineVO discount = new InvoiceLineVO();
			discount.setItem(DISCOUNT_ITEM);
			discount.setDescription(DISCOUNT_ITEM);
			discount.setRate(invoice.getDiscount().negate());
			discount.setUnit(invoice.getLines().get(0).getUnit());

			invoice.getLines().add(discount);
		}
	}
	
	/**
	 * Pull disposal invoice information from NS and push it into Willow
	 * @param disposalInvoice which includes both transactionId and externalId
	 * @throws Exception
	 * @author Saket.Maheshwary 
	 */
	
	public void processNsToWillowDisposalInvoice(DisposalInvoiceVO disposalInvoice) throws Exception {
		
		super.validate(disposalInvoice);

		ClientInvoiceVO clientInvoiceVO = clientInvoiceService.get(disposalInvoice.getTransactionId(), null);
		
		if(clientInvoiceVO == null) {
			throw new RuntimeException(String.format("Unable to find clientInvoiceVO for disposal invoice: %s", disposalInvoice.toString()));
		} else if(clientInvoiceVO.getStatus() == null)
			throw new RuntimeException(String.format("ClientInvoiceVO.status cannot be null. disposal Invoice: %s", disposalInvoice.toString()));	
		
		disposalInvoice.setStatus(TransactionStatusEnum.getTransactionStatus(clientInvoiceVO.getStatus()));
		
		if(TransactionStatusEnum.PAID_IN_FULL.equals(disposalInvoice.getStatus())) {
			ClientPaymentApplyVO appliedPayment = clientPaymentService.getInvoiceLastPayment(disposalInvoice.getTransactionId());
			
			if (appliedPayment != null && appliedPayment.getTranDate() != null)			
				disposalInvoice.setPaidInFullDate(appliedPayment.getTranDate());
			else
				disposalInvoice.setPaidInFullDate(null);
		}
		else {
			disposalInvoice.setPaidInFullDate(null);
		}
		
		//This will push data into Willow database
		clientTxnDAO.processDisposalInvoiceInfo(disposalInvoice);
		
	}
	
	
	/**
	 * Method to get disposalInvoiceVO data that was changed 
	 * @param disposalInvoice which includes both transactionId and externalId
	 * @throws Exception
	 * @author Saket.Maheshwary 
	 */
	public List<DisposalInvoiceVO> getDisposalInvoiceList(Date start, Date end) throws Exception {
		List<DisposalInvoiceVO> invoices = new ArrayList<DisposalInvoiceVO>();
		List<Map<String, Object>> disposalMap = invoiceSuiteAnalyticsService.getDisposalInvoice(start, end);
		String tmp;
		
		if (disposalMap != null && disposalMap.size() > 0) {
			for(Map<String, Object> result : disposalMap) {
				DisposalInvoiceVO invoice = new DisposalInvoiceVO();
				invoice
					.setTransactionExtId((String)result.get("transaction_extid"))
					.setTransactionId((String)result.get("transaction_id")); //TODO Mar 3, 2022 @ 16:09:1 error generated from old code. app log 01-MAR-22 01.19.00.036000000 PM
					tmp = (String)result.get("docid");
					if(tmp != null)
						invoice.setDocId(Long.valueOf(tmp));									
				invoices.add(invoice);
			}
		}

		return invoices;
		
	}
}

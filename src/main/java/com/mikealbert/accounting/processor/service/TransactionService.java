package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.DriverCostCenterDAO;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.helper.BusinessUnitHelper;
import com.mikealbert.accounting.processor.item.CreditItemHelper;
import com.mikealbert.accounting.processor.item.InvoiceItemHelper;
import com.mikealbert.accounting.processor.item.PurchaseOrderItemHelper;
import com.mikealbert.accounting.processor.vo.CostCenterVO;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.PayableTransactionLineVO;
import com.mikealbert.accounting.processor.vo.PayableTransactionVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

public abstract class TransactionService extends BaseService {
	@Resource DocDAO docDAO;	
	@Resource DriverCostCenterDAO driverCostCenterDAO;
	@Resource VendorService vendorService;
	@Resource InvoiceItemHelper invoiceItemHelper;
	@Resource CreditItemHelper creditItemHelper;	
	@Resource PurchaseOrderItemHelper purchaseOrderItemHelper;	
	@Resource XRefService xRefService;
	
	static final String DELIVERY_INTERNAL_PDI_ITEM = "Delivery - Internal PDI";
	static final String MAINTENANCE_AI_FEE_ITEM = "Maintenance - Auto Integrate Fee";
		
	static final String DEPARTMENT_RENTAL = "Rental";
	static final String DEPARTMENT_FLEET_MAINTENANCE = "Fleet Maintenance";
	
	static final String AI_OP_CODE = "AUTO_MI";
			
	protected Long getVendorInternalAddressId(PayableTransactionVO<?, ?> transaction) throws Exception {
		List<Map<String, Object>> addresses = vendorService.getAddresses(String.valueOf(transaction.getVendorEaaId()));
		
		Map<String, Object> matchedAddress = addresses.stream()
				.filter(address -> address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) != null)
				.filter(address -> Long.valueOf((String) address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName())).equals(transaction.getVendorEaaId()))
				.findAny()
				.orElse(null);
		
		if(matchedAddress ==  null) {
			throw new RetryableSuiteTalkException(String.format("Vendor address for eaaid %d was not found for account %s in external accounting system. Address from NS externalId %s", transaction.getVendorEaaId(), transaction.getVendor(), addresses));
		}
		
		Long id = Long.valueOf((String)matchedAddress.get(VendorAddressFieldEnum.INTERNAL_ID.getName()));		
				
		return id;
	}
	
	protected InvoiceLineVO bindItem(InvoiceLineVO line) throws RuntimeException {
		String itemName;
		
		try {
			itemName = xRefService.getExternalValue(XRefGroupNameEnum.INVOICE_ITEM, invoiceItemHelper.generateKey(line));
			line.setItem(itemName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return line;
	}
	
	protected CreditLineVO bindItem(CreditLineVO line) throws RuntimeException {
		String itemName;
		
		try {
			itemName = xRefService.getExternalValue(XRefGroupNameEnum.INVOICE_ITEM, creditItemHelper.generateKey(line));
			line.setItem(itemName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return line;
	}	
	
	protected PurchaseOrderLineVO bindItem(PurchaseOrderLineVO line) throws RuntimeException {
		String itemName;
		
		try {
			itemName = xRefService.getExternalValue(XRefGroupNameEnum.PURCHASE_ORDER_ITEM, purchaseOrderItemHelper.generateKey(line));
			line.setItem(itemName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return line;
	}	
	
	protected PayableTransactionLineVO<?> bindDepartment(PayableTransactionLineVO<?> line) {
		String department = null;
		CostCenterVO costCenter;
		
		switch(line.getProductCode()) {
		case ST:
			department = DEPARTMENT_RENTAL;
			break;
		case DEMO:
			String[] clientAccountParts = line.getClient().split(",");
			if(clientAccountParts != null && clientAccountParts.length == 3 &&  line.getDrvId() != null) {
				costCenter = driverCostCenterDAO.getActiveCostCenter(Long.valueOf(clientAccountParts[0]), clientAccountParts[1], clientAccountParts[2], line.getDrvId(), line.getHeader().getTranDate());
				department = costCenter == null ? null : costCenter.getDescription();
			}
			break;			
		default:
			department = null;;
		}
		
		line.setDepartment(department);
		
		return line;
	}	
	
	/**
	 * Update department on line based on last minute phase 2 updates. 
	 * TODO Revisit this to create a more elegant solution
	 * @param line
	 */
	protected TransactionLineVO<?> patchDepartment(TransactionLineVO<?> line) {
		//TODO This way of handling department assignment for AI fee is a HACK. Revisit post phase 2.
		if(MAINTENANCE_AI_FEE_ITEM.equals(line.getItem())) {
			line.setDepartment(null);
		}		

		//TODO This way of handling department assignment for PDI is a HACK. Revisit post phase 2.
		if(DELIVERY_INTERNAL_PDI_ITEM.equals(line.getItem())) {
			line.setDepartment(null);
		}
		
		return line;
	}	
	
	protected TransactionLineVO<?> bindBusinessUnit(TransactionLineVO<?> line) {			
		line.setBusinessUnit(BusinessUnitHelper.resolve(line.getProductCode()));
		return line;		
	}	
	
	/**
	 * Update business unit on line based on last minute phase 2 updates. 
	 * TODO Revisit this to create a more elegant solution
	 * @param line
	 */
	protected TransactionLineVO<?> patchBusinessUnit(TransactionLineVO<?>line) {
		//TODO This way of handling department assignment for AI fee is a HACK. Revisit post phase 2.
		if(MAINTENANCE_AI_FEE_ITEM.equals(line.getItem())) {
			line.setBusinessUnit(null);
		}		
		
		//TODO This way of handling department assignment for PDI is a HACK. Revisit post phase 2.
		if(DELIVERY_INTERNAL_PDI_ITEM.equals(line.getItem())) {
			line.setBusinessUnit(null);
		}
		
		return line;
	}	
		
}

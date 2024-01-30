package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorBillFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.util.data.DateUtil;
@Disabled("Skipping Vendor Bill tests until tax engine calc errors are resolved")
@SpringBootTest
@DisplayName("An invoice")
public class VendorBillSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource PurchaseOrderSuiteTalkService purchaseOrderSuiteTalkService;
	@Resource UnitSuiteTalkService unitSuiteTalkService;	
	@Resource VendorBillSuiteTalkService vendorBillSuiteTalkService;
	
	@MockBean DocDAO docDAO;
	
	UnitVO mockUnit;
	
	@BeforeEach
	void putUnit() throws Exception {
		mockUnit = generateMockUnit();
		unitSuiteTalkService.putUnit(mockUnit);		
	}
	
	@AfterEach
	void deleteUnit() throws Exception {
		unitSuiteTalkService.deleteUnit(mockUnit);		
	}
		
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when invoice is for a Vendor , create a vendor bill, based on the Vendor, in the external system")
	public void testCreateFromVendor() throws Exception {
		Map<String, Object> vendorBill;
		
		InvoiceVO invoice = generateInvoice(20);
		invoice.setCreateFromPurchaseOrder(false);
		
		vendorBillSuiteTalkService.create(invoice);
		vendorBill = vendorBillSuiteTalkService.get(invoice.getExternalId().toString());
		vendorBillSuiteTalkService.delete((String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));

		assertEquals(invoice.getExternalId(), (String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(invoice.getTranId(), (String)vendorBill.get(VendorBillFieldEnum.TRAN_ID.getScriptId()));
		assertTrue(((String)vendorBill.get(VendorBillFieldEnum.ENTITY.getScriptId())).contains(invoice.getVendor()));		
		assertEquals(invoice.getSubsidiary(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.SUBSIDIARY.getScriptId())));
		assertEquals(invoice.getPayableAccount(), (String)vendorBill.get(VendorBillFieldEnum.ACCOUNT.getScriptId()));
		assertEquals(DateUtil.convertToLocalDate(invoice.getTranDate()), DateUtil.convertToLocalDate(((Calendar)vendorBill.get(VendorBillFieldEnum.TRAN_DATE.getScriptId())).getTime()));
		assertEquals(invoice.getMemo(), (String)vendorBill.get(VendorBillFieldEnum.MEMO.getScriptId()));
		assertEquals(invoice.getApprovalDepartment(), (String)vendorBill.get(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId()));
		assertTrue(((Boolean)vendorBill.get(VendorBillFieldEnum.AUTO_APPROVE.getScriptId())));
		assertEquals(invoice.getVendorAddressInternalId(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.BILL_ADDRESS_LIST.getScriptId())));
		assertEquals(mockUnit.getUnitNo(), (String)vendorBill.get(CustomSegmentEnum.UNIT_NO.getScriptId()));
 		
		List<Map<String, Object>> items = (List<Map<String, Object>>)vendorBill.get(VendorBillFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.forEach(item -> {
			assertTrue(((String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM.getScriptId())).get("name")).contains(VEHICLE_ITEM));
			assertEquals(invoice.getLines().get(0).getQuantity(), BigDecimal.valueOf(((Double)item.get(VendorBillFieldEnum.ITEM_QUANTITY.getScriptId())).longValue()));
			assertEquals(invoice.getLines().get(0).getRate(), new BigDecimal((String)item.get(VendorBillFieldEnum.ITEM_RATE.getScriptId())));			
			assertEquals(invoice.getLines().get(0).getDepartment(), (String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM_DEPARTMENT.getScriptId())).get("name"));
			assertEquals(invoice.getLines().get(0).getBusinessUnit().getName(), (String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM_CLASSIFICATION.getScriptId())).get("name"));			
		});
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when invoice is for a Vendor that includes a discount, a vendor bill including the discount line is created in the external system")
	public void testCreateFromVendorWithDiscount() throws Exception {
		Map<String, Object> vendorBill;
		
		InvoiceVO invoice = generateInvoice(1, true);
		invoice.setCreateFromPurchaseOrder(false);
		invoice.setDiscount(new BigDecimal(-0.25));
		
		vendorBillSuiteTalkService.create(invoice);
		vendorBill = vendorBillSuiteTalkService.get(invoice.getExternalId().toString());
		vendorBillSuiteTalkService.delete((String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));

		assertEquals(invoice.getExternalId(), (String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(invoice.getTranId(), (String)vendorBill.get(VendorBillFieldEnum.TRAN_ID.getScriptId()));
		assertTrue(((String)vendorBill.get(VendorBillFieldEnum.ENTITY.getScriptId())).contains(invoice.getVendor()));		
		assertEquals(invoice.getSubsidiary(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.SUBSIDIARY.getScriptId())));
		assertEquals(invoice.getPayableAccount(), (String)vendorBill.get(VendorBillFieldEnum.ACCOUNT.getScriptId()));
		assertEquals(DateUtil.convertToLocalDate(invoice.getTranDate()), DateUtil.convertToLocalDate(((Calendar)vendorBill.get(VendorBillFieldEnum.TRAN_DATE.getScriptId())).getTime()));
		assertEquals(invoice.getMemo(), (String)vendorBill.get(VendorBillFieldEnum.MEMO.getScriptId()));
		assertEquals(invoice.getApprovalDepartment(), (String)vendorBill.get(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId()));
		assertTrue(((Boolean)vendorBill.get(VendorBillFieldEnum.AUTO_APPROVE.getScriptId())));
		assertEquals(invoice.getVendorAddressInternalId(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.BILL_ADDRESS_LIST.getScriptId())));
		assertEquals(mockUnit.getUnitNo(), (String)vendorBill.get(CustomSegmentEnum.UNIT_NO.getScriptId()));
		assertEquals(invoice.rolledUpLineRate(), (BigDecimal)vendorBill.get(VendorBillFieldEnum.USER_TOTAL.getScriptId()));
 		
		List<Map<String, Object>> items = (List<Map<String, Object>>)vendorBill.get(VendorBillFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.skip(1)
		.forEach(item -> {
			assertTrue(((String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM.getScriptId())).get("name")).contains(DISCOUNT_ITEM));
			assertNull((item.get(VendorBillFieldEnum.ITEM_QUANTITY.getScriptId())));
			assertEquals(invoice.getLines().get(1).getRate(), new BigDecimal((String)item.get(VendorBillFieldEnum.ITEM_RATE.getScriptId())));
			assertEquals(invoice.getLines().get(1).getDescription(), (String)item.get(VendorBillFieldEnum.ITEM_DESCRIPTION.getScriptId()));			
		});
	}	
		
	@Test
	@DisplayName("when invoice is for a Purchase Order , create a vendor bill, based on the Purchase Order, in the external system")
	public void testCreateFromPurchaseOrder() throws Exception {
		Map<String, Object> vendorBill;
		
		PurchaseOrderVO po = generateMockPO(1);		
		po.setControlCode(ControlCodeEnum.CE_LTD);
		po.setAutoApprove(true);
		
		PurchaseOrderLineVO line = po.getLines().get(0);
		line.setAssetType(AssetTypeEnum.FL);
		
		purchaseOrderSuiteTalkService.add(po);
				
		InvoiceVO invoice = generateInvoice(2);
		invoice.setCreateFromPurchaseOrder(true);
	    invoice.getLines().get(0).setExternalAssetType("Fleet-Closed End");
		invoice.setAutoApprove(true);
		
		when(docDAO.getPurchaseOrderDocIdByInvoiceDocId(Long.valueOf(invoice.getExternalId()))).thenReturn(Long.valueOf(po.getExternalId()));
		
		vendorBillSuiteTalkService.create(invoice, po);
		vendorBill = vendorBillSuiteTalkService.get(invoice.getExternalId().toString());

		
		vendorBillSuiteTalkService.delete((String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		purchaseOrderSuiteTalkService.delete(po.getExternalId().toString());
		
		assertEquals(invoice.getExternalId(), (String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(invoice.getTranId(), (String)vendorBill.get(VendorBillFieldEnum.TRAN_ID.getScriptId()));
		assertTrue(((String)vendorBill.get(VendorBillFieldEnum.ENTITY.getScriptId())).contains(invoice.getVendor()));		
		assertEquals(invoice.getSubsidiary(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.SUBSIDIARY.getScriptId())));
		assertEquals(invoice.getPayableAccount(), (String)vendorBill.get(VendorBillFieldEnum.ACCOUNT.getScriptId()));
		assertEquals(DateUtil.convertToLocalDate(invoice.getTranDate()), DateUtil.convertToLocalDate(((Calendar)vendorBill.get(VendorBillFieldEnum.TRAN_DATE.getScriptId())).getTime()));
		assertTrue((boolean)vendorBill.get(PurchaseOrderFieldEnum.MAIN_PO.getScriptId()));
		assertEquals(po.getControlCode().name(), (String)vendorBill.get(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId()));
	}
	
	@Test
	@DisplayName("when invoice is for a Purchase Order and has a different Vendor , create a vendor bill, based on the Purchase Order, in the external system")
	public void testCreateFromPurchaseOrderSwitchVendor() throws Exception {
		final String NEW_VENDOR = "00040010";
		final Long NEW_VENDOR_ADDRESS_ID = 4200L;
		
		Map<String, Object> vendorBill;
		
		PurchaseOrderVO po = generateMockPO(1);
		po.setControlCode(ControlCodeEnum.CE_LTD);
		po.setAutoApprove(true);
		
		PurchaseOrderLineVO line = po.getLines().get(0);	
		line.setAssetType(AssetTypeEnum.FL);
		
		purchaseOrderSuiteTalkService.add(po);
				
		InvoiceVO invoice = generateInvoice(2);
		invoice.setCreateFromPurchaseOrder(true);
		invoice.getLines().get(0).setExternalAssetType("Fleet-Closed End");
		invoice.setAutoApprove(true);
		invoice.setVendor(NEW_VENDOR);
		invoice.setVendorEaaId(NEW_VENDOR_ADDRESS_ID);
				
		when(docDAO.getPurchaseOrderDocIdByInvoiceDocId(Long.valueOf(invoice.getExternalId()))).thenReturn(Long.valueOf(po.getExternalId()));
		
		vendorBillSuiteTalkService.create(invoice, po);
		vendorBill = vendorBillSuiteTalkService.get(invoice.getExternalId().toString());
		vendorBillSuiteTalkService.delete((String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		
		purchaseOrderSuiteTalkService.delete(po.getExternalId().toString());

		assertEquals(invoice.getExternalId(), (String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(invoice.getTranId(), (String)vendorBill.get(VendorBillFieldEnum.TRAN_ID.getScriptId()));
		assertTrue(((String)vendorBill.get(VendorBillFieldEnum.ENTITY.getScriptId())).contains(invoice.getVendor()));		
		assertEquals(invoice.getSubsidiary(), Long.valueOf((String)vendorBill.get(VendorBillFieldEnum.SUBSIDIARY.getScriptId())));
		assertEquals(invoice.getPayableAccount(), (String)vendorBill.get(VendorBillFieldEnum.ACCOUNT.getScriptId()));
		assertEquals(DateUtil.convertToLocalDate(invoice.getTranDate()), DateUtil.convertToLocalDate(((Calendar)vendorBill.get(VendorBillFieldEnum.TRAN_DATE.getScriptId())).getTime()));
		assertTrue((boolean)vendorBill.get(PurchaseOrderFieldEnum.MAIN_PO.getScriptId()));
		assertEquals(po.getControlCode().name(), (String)vendorBill.get(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId()));
	}	
	
	@Test
	@DisplayName("when invoice is for a purchase order that does not exist, a retryable exception is thrown")	
	public void testCreateFromPurchaseOrderWithNoPurchaseOrder() { 
		assertThrows(RetryableSuiteTalkException.class, () -> {
			Map<String, Object> vendorBill;			
			PurchaseOrderVO po = generateMockPO(1);	
			po.getLines().get(0).setUnit("FAKE");
			
			InvoiceVO invoice = generateInvoice(2);
			invoice.setCreateFromPurchaseOrder(true);
		    invoice.getLines().get(0).setExternalAssetType("Fleet-Closed End");
			invoice.setAutoApprove(true);
			
			
			when(docDAO.getPurchaseOrderDocIdByInvoiceDocId(Long.valueOf(invoice.getExternalId()))).thenReturn(Long.valueOf(po.getExternalId()));
			
			vendorBillSuiteTalkService.create(invoice, po);
			vendorBill = vendorBillSuiteTalkService.get(invoice.getExternalId().toString());
			vendorBillSuiteTalkService.delete((String)vendorBill.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		});	
	}	
	
	@Override
	protected String getUnitNo(){
		return mockUnit.getUnitNo();
	}
		
}
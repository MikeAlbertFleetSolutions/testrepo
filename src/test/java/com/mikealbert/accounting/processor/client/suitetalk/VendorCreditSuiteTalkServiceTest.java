package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.mikealbert.accounting.processor.enumeration.VendorBillFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.util.data.DateUtil;

@Disabled("Skipping Vendor Bill tests until tax engine calc errors are resolved")
@SpringBootTest
@DisplayName("An invoice credit")
public class VendorCreditSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource UnitSuiteTalkService unitSuiteTalkService;	
	@Resource VendorBillSuiteTalkService vendorBillSuiteTalkService;
	@Resource VendorCreditSuiteTalkService vendorCreditSuiteTalkService;
	
	@MockBean DocDAO docDAO;
	
	UnitVO mockUnit;
	
	@BeforeEach
	void up() throws Exception {
		mockUnit = generateMockUnit();
		unitSuiteTalkService.putUnit(mockUnit);		
	}
	
	@AfterEach
	void down() throws Exception {
		unitSuiteTalkService.deleteUnit(mockUnit);		
	}
		
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when invoice credit is issued, create a vendor credit in the external system")
	public void testCreate() throws Exception {
		Map<String, Object> externalCredit;
		
		InvoiceVO invoice = generateInvoice(2);
		invoice.setAutoApprove(true);
		invoice.setCreateFromPurchaseOrder(false);
		vendorBillSuiteTalkService.create(invoice);
		
		CreditVO internalCredit = generateInvoiceCredit(1);
		internalCredit.setParentExternalId(Long.valueOf(invoice.getExternalId()));
		internalCredit.getLines().get(0).setRate(new BigDecimal(".50"));		
		vendorCreditSuiteTalkService.create(internalCredit);
		
		externalCredit = vendorCreditSuiteTalkService.get(internalCredit.getExternalId().toString());
		
		vendorCreditSuiteTalkService.delete((String)externalCredit.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));	
		vendorBillSuiteTalkService.delete(invoice.getExternalId().toString());

		assertEquals(internalCredit.getExternalId(), (String)externalCredit.get(VendorBillFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(internalCredit.getTranId(), (String)externalCredit.get(VendorBillFieldEnum.TRAN_ID.getScriptId()));		
		assertEquals(internalCredit.getSubsidiary(), Long.valueOf((String)externalCredit.get(VendorBillFieldEnum.SUBSIDIARY.getScriptId())));
		assertEquals(internalCredit.getPayableAccount(), (String)externalCredit.get(VendorBillFieldEnum.ACCOUNT.getScriptId()));
		assertEquals(internalCredit.getMemo(), (String)externalCredit.get(VendorBillFieldEnum.MEMO.getScriptId()));
		assertEquals(internalCredit.getApprovalDepartment(), (String)externalCredit.get(VendorBillFieldEnum.APPROVAL_DEPARTMENT.getScriptId()));
		//assertEquals(credit.getVendorAddressInternalId(), Long.valueOf((String)vendorCredit.get(VendorBillFieldEnum.BILL_ADDRESS_LIST.getScriptId())));
		assertEquals(DateUtil.convertToLocalDate(internalCredit.getTranDate()), DateUtil.convertToLocalDate(((Calendar)externalCredit.get(VendorBillFieldEnum.TRAN_DATE.getScriptId())).getTime()));		
		//assertFalse(((Boolean)externalCredit.get(VendorBillFieldEnum.AUTO_APPROVE.getScriptId())));
		assertTrue(((String)externalCredit.get(VendorBillFieldEnum.ENTITY.getScriptId())).contains(invoice.getVendor()));
		//assertEquals(mockUnit.get("unitNo"), (String)externalCredit.get(CustomSegmentEnum.UNIT_NO.getScriptId()));
 		
		List<Map<String, Object>> items = (List<Map<String, Object>>)externalCredit.get(VendorBillFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.forEach(item -> {
			assertTrue(((String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM.getScriptId())).get("name")).contains(VEHICLE_ITEM));
			assertEquals(internalCredit.getLines().get(0).getQuantity(), BigDecimal.valueOf(((Double)item.get(VendorBillFieldEnum.ITEM_QUANTITY.getScriptId())).longValue()));
			assertEquals(internalCredit.getLines().get(0).getRate(), new BigDecimal((String)item.get(VendorBillFieldEnum.ITEM_RATE.getScriptId())));			
			assertEquals(internalCredit.getLines().get(0).getDepartment(), (String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM_DEPARTMENT.getScriptId())).get("name"));
			assertEquals(internalCredit.getLines().get(0).getBusinessUnit().getName(), (String)((Map<String, Object>)item.get(VendorBillFieldEnum.ITEM_CLASSIFICATION.getScriptId())).get("name"));			
		});
	}	
		
	@Test
	@DisplayName("when vendor bill does not exist for credt, a retryable exception is thrown")	
	public void testCreateWithInvalidVendorBill() { 		
		InvoiceVO invoice = generateInvoice(2);
		invoice.setAutoApprove(true);
		invoice.setCreateFromPurchaseOrder(false);
		
		CreditVO internalCredit = generateInvoiceCredit(1);
		internalCredit.setParentExternalId(Long.valueOf(invoice.getExternalId()));
					
		assertThrows(RetryableSuiteTalkException.class, () -> {
			vendorCreditSuiteTalkService.create(internalCredit);		
		});	
	}	
		
	@Override
	protected String getUnitNo(){
		return mockUnit.getUnitNo();
	}
		
}
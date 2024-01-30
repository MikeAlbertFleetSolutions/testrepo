package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("An invoice credit")
public class CreditServiceTest extends TransactionBaseTest{
	@Resource CreditService creditService;
	
	@MockBean DocDAO docDAO;
	@MockBean VendorService vendorService;
		
	UnitVO mockUnit;
	private List<Map<String, Object>> mockVendorAddresses;
	
	@BeforeEach
	void up() throws Exception {
		mockUnit = generateMockUnit();
		mockVendorAddresses = generateMockVendorAddresses();		
	}
		
	@Test
	@DisplayName("when credit is for CE_LTD product code, all properties depending on the product code are setting correctly")	
	public void testGetCreditWithCeProduct() throws Exception {
		CreditVO actualCredit;
		
		CreditVO mockCredit = generateInvoiceCredit(1);
		mockCredit.setControlCode(ControlCodeEnum.CE_LTD);
		mockCredit.setSubsidiary(2L);		
		mockCredit.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		
		when(docDAO.getCreditApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit);
		when(docDAO.getCreditApLinesByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualCredit = creditService.getCredit(Long.valueOf(mockCredit.getExternalId()));
		
		assertFalse(CreditService.MAINTENANCE_AI_FEE_MEMO.equals(actualCredit.getMemo()));
		assertEquals(actualCredit.getLines().get(0).getDepartment(), null);
		assertEquals(actualCredit.getSubsidiary(), 4L);
		assertEquals(BusinessUnitEnum.FLEET_SOLUTIONS, actualCredit.getLines().get(0).getBusinessUnit());
	}
	
	@Test
	@DisplayName("when credit is for FLMAINT product code, all properties depending on the product code are setting correctly")	
	public void testGetCreditWithFlMaintProduct() throws Exception {
		CreditVO actualCredit;
		
		CreditVO mockCredit = generateInvoiceCredit(1);
		mockCredit.setControlCode(ControlCodeEnum.FLMAINT);
		mockCredit.setSubsidiary(2L);		
		mockCredit.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		
		when(docDAO.getCreditApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit);
		when(docDAO.getCreditApLinesByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualCredit = creditService.getCredit(Long.valueOf(mockCredit.getExternalId()));
		
		assertEquals(CreditService.MAINTENANCE_AI_FEE_MEMO, actualCredit.getMemo());
		assertNull(actualCredit.getLines().get(0).getDepartment());
		assertEquals(actualCredit.getSubsidiary(), 4L);
		assertNull(actualCredit.getLines().get(0).getBusinessUnit());
	}
	
	@Test
	@DisplayName("when credit line rate is a negative value, the rate is transformed to an absolute value")	
	public void testGetCreditWithNegativeRateLine() throws Exception {
		CreditVO actualCredit;
		
		CreditVO mockCredit = generateInvoiceCredit(1);
		mockCredit.setControlCode(ControlCodeEnum.CE_LTD);
		mockCredit.setSubsidiary(2L);		
		mockCredit.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		
		when(docDAO.getCreditApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit);
		when(docDAO.getCreditApLinesByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualCredit = creditService.getCredit(Long.valueOf(mockCredit.getExternalId()));
		
		assertTrue(actualCredit.getLines().get(0).getRate().compareTo(new BigDecimal(0)) > 0);
	}
	
	@Test
	@DisplayName("when credit line rate is a positive value, the rate transformed to an absolute value")	
	public void testGetCreditWithPositiveRateLine() throws Exception {
		CreditVO actualCredit;
		
		CreditVO mockCredit = generateInvoiceCredit(1);
		mockCredit.setControlCode(ControlCodeEnum.CE_LTD);
		mockCredit.setSubsidiary(2L);		
		mockCredit.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		
		when(docDAO.getCreditApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit);
		when(docDAO.getCreditApLinesByDocId(ArgumentMatchers.anyLong())).thenReturn(mockCredit.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualCredit = creditService.getCredit(Long.valueOf(mockCredit.getExternalId()));
		
		assertTrue(actualCredit.getLines().get(0).getRate().compareTo(new BigDecimal(0)) > 0);
	}		
	
	@Test
	@DisplayName("when credit glAcc flag is updated, its value is set to 1")
	public void testUpdateGlAccToOne() throws Exception {
		ArgumentCaptor<Long> docIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Doc> docCaptor = ArgumentCaptor.forClass(Doc.class);

		CreditVO mockCredit = generateInvoiceCredit(1);
		Doc doc = new Doc();
		
		when(docDAO.findById(docIdCaptor.capture())).thenReturn(Optional.of(doc));
		when(docDAO.save(docCaptor.capture())).thenReturn(null);
		
		creditService.updateGlAccToOne(Long.valueOf(mockCredit.getExternalId()));
		
		assertEquals(mockCredit.getExternalId(), docIdCaptor.getValue().toString());
		assertEquals(doc, docCaptor.getValue());
		assertEquals(1L, docCaptor.getValue().getGlAcc());
	}
	
	@Test
	@DisplayName("when retrieving new credit(s), the id(s) for the credit is returned")
	public void testGetMaintenanceCreditIds() throws Exception {
		final Long EXPECTED_CREDIT_DOC_ID = 2L;
				
		when(docDAO.getMaintenanceCreditIds(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Arrays.asList(new Long[]{EXPECTED_CREDIT_DOC_ID}));
		
		List<Long> actualIds = creditService.getMaintenanceCreditIds(new Date(), new Date());
		
		assertEquals(EXPECTED_CREDIT_DOC_ID, actualIds.get(0));
	}

	@Override
	protected String getUnitNo(){
		return mockUnit.getUnitNo();
	}
	
	private List<Map<String, Object>> generateMockVendorAddresses() {
		List<Map<String, Object>> mockVendorAddresses = new ArrayList<>();
		mockVendorAddresses.add(new HashMap<String, Object>());
		mockVendorAddresses.get(0).put("externalId","1");
		mockVendorAddresses.get(0).put("internalId", "10");
		return mockVendorAddresses;
	}		
}

package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.VendorBillSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.DriverCostCenterDAO;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.accounting.processor.vo.CostCenterVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
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
@DisplayName("Payables")
public class InvoiceServiceTest extends BaseTest{
	@Resource InvoiceService invoiceService;
	@Resource XRefService xRefService;
	
	@MockBean DocDAO docDAO;
	@MockBean VendorService vendorService;
	@MockBean DriverCostCenterDAO driverCostCenterDAO;
	
	static String VENDOR_ACCOUNT_CODE = "00040010";
	static String APPROVAL_DEPARTMENT = "Accounting";	
	static String VEHICLE_ITEM = "Vehicle";
	static String MAINT_AI_FEE_ITEM = "Maintenance - Auto Integrate Fee";
	
	static String SERVICE_COST_GL_CODE = "01550024215";
		
	UnitVO mockUnit;
	private List<Map<String, Object>> mockVendorAddresses;
	
	@BeforeEach
	void putUnit() throws Exception {
		mockUnit = generateMockUnit();
		mockVendorAddresses = generateMockVendorAddresses();
	}
		
	@Test
	@DisplayName("when invoice does not have a product code, all properties depending on the product code are setting correctly")	
	public void testGetInvoice() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.setSubsidiary(2L);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);
		
		assertTrue(actualInvoice.getLines().size() == 1);
		assertEquals(actualInvoice.getLines().get(0).getDepartment(), null);
		assertEquals(actualInvoice.getSubsidiary(), 4L);
		assertEquals(BusinessUnitEnum.FLEET_SOLUTIONS, actualInvoice.getLines().get(0).getBusinessUnit());		
	}
	
	@Test
	@DisplayName("when invoice is for a DEMO product, all properties depending on the product code are setting correctly")		
	public void testGetInvoiceForDemo() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.getLines().get(0).setProductCode(ProductEnum.DEMO);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);				
		when(driverCostCenterDAO.getActiveCostCenter(any(), any(), any(), any(), any())).thenReturn(new CostCenterVO().setCode("C-3PO NOW").setDescription("C-3PO NOW Description"));
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);
		
		assertTrue(actualInvoice.getLines().size() == 1);
		assertEquals("C-3PO NOW Description", actualInvoice.getLines().get(0).getDepartment());
		assertEquals(BusinessUnitEnum.FLEET_SOLUTIONS, actualInvoice.getLines().get(0).getBusinessUnit());		
	}	
	
	@Test
	@DisplayName("when invoice is for a ST product, all properties depending on the product code are setting correctly")			
	public void testGetInvoiceForST() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.getLines().get(0).setProductCode(ProductEnum.ST);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);				
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);
		
		assertTrue(actualInvoice.getLines().size() == 1);
		assertEquals("Rental", actualInvoice.getLines().get(0).getDepartment());
		assertEquals(BusinessUnitEnum.RENTAL, actualInvoice.getLines().get(0).getBusinessUnit());		
	}
	
	@Test
	@DisplayName("when invoice control code is for a FLLINCENSE, all properties depending on the control code are setting correctly")			
	public void testGetInvoiceForFlLicense() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.setControlCode(ControlCodeEnum.FLLICENSE);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(docDAO.getInvoiceArIdFromInvoiceApId(ArgumentMatchers.anyLong())).thenReturn(2L);	
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);				
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);
		
		assertTrue(actualInvoice.getLines().get(0).isRebillableLicenseFee());
	}	
	
	@Test
	@DisplayName("when invoice control code is for GRD_INTRNL, all properties depending on the control code are setting correctly")			
	public void testGetInvoiceForGrdIntrnl() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.setControlCode(ControlCodeEnum.GRD_INTRNL);
		//mockInvoice.getLines().get(0).setGlCode("01550024215");
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(docDAO.getInvoiceArIdFromInvoiceApId(ArgumentMatchers.anyLong())).thenReturn(2L);	
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);				
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);
		
		assertNull(actualInvoice.getLines().get(0).getBusinessUnit());
		assertNull(actualInvoice.getLines().get(0).getDepartment());		
	}
	
	@Test
	@DisplayName("when invoice is for maintenance, all properties depending on the control code are set correctly")	
	public void testGetInvoiceForFlMaint() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.setControlCode(ControlCodeEnum.FLMAINT);
		mockInvoice.setSubsidiary(2L);
		mockInvoice.setOpCode(TransactionService.AI_OP_CODE);
		mockInvoice.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		mockInvoice.getLines().get(0).setGlCode(SERVICE_COST_GL_CODE);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);

		assertTrue(actualInvoice.getLines().size() == 1);	
		assertEquals(MAINT_AI_FEE_ITEM, mockInvoice.getLines().get(0).getItem());		
		assertNull(mockInvoice.getLines().get(0).getDepartment()); 
		assertNull(mockInvoice.getLines().get(0).getBusinessUnit());				
	}
	
	
	@Test
	@DisplayName("when invoice has a discount, the discount line is added")	
	public void testGetInvoiceWithDiscount() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);
		mockInvoice.setSubsidiary(2L);
		mockInvoice.setDiscount(new BigDecimal(.25));
		mockInvoice.setOpCode(TransactionService.AI_OP_CODE);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), false);

		assertTrue(actualInvoice.getLines().size() == 2);
		assertEquals(mockInvoice.rolledUpLineRate(), actualInvoice.rolledUpLineRate());			
		assertNull(mockInvoice.getLines().get(1).getDepartment());
		assertNull(mockInvoice.getLines().get(1).getBusinessUnit());		
	}
	
	@Test
	@DisplayName("when invoice is for a Fleet Closed End purchase order, all relevant properties are setting correctly")	
	public void testGetInvoiceForFleetClosedEndPurchaseOrder() throws Exception {
		InvoiceVO actualInvoice;
		InvoiceVO mockInvoice = generateInvoice(1);		
		mockInvoice.setControlCode(ControlCodeEnum.CE_LTD);
		mockInvoice.getLines().get(0).setAssetType(AssetTypeEnum.FL);
		mockInvoice.getLines().get(0).setProductCode(ProductEnum.CE_LTD);
		
		when(docDAO.getInvoiceApHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockInvoice);
		when(docDAO.getInvoiceApLinesByDocId(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(mockInvoice.getLines());
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualInvoice = invoiceService.getInvoice(Long.valueOf(mockInvoice.getExternalId()), true);
		
		assertEquals("Fleet-Closed End", actualInvoice.getLines().get(0).getExternalAssetType());
		assertEquals(BusinessUnitEnum.FLEET_SOLUTIONS, actualInvoice.getLines().get(0).getBusinessUnit());		
		assertNull(actualInvoice.getLines().get(0).getDepartment());
	}
	
	@Test
	@DisplayName("when invoice glAcc flag is updated, its value is set to 1")
	public void testUpdateGlAccToOne() throws Exception {
		ArgumentCaptor<Long> docIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Doc> docCaptor = ArgumentCaptor.forClass(Doc.class);

		InvoiceVO mockInvoice = generateInvoice(1);
		Doc doc = new Doc();
		
		when(docDAO.findById(docIdCaptor.capture())).thenReturn(Optional.of(doc));
		when(docDAO.save(docCaptor.capture())).thenReturn(null);
		
		invoiceService.updateGlAccToOne(Long.valueOf(mockInvoice.getExternalId()));
		
		assertEquals(mockInvoice.getExternalId(), docIdCaptor.getValue().toString());
		assertEquals(doc, docCaptor.getValue());
		assertEquals(1L, docCaptor.getValue().getGlAcc());
	}
	
	@Test
	@DisplayName("when retrieving new maintenance invoice(s), the id(s) for the invoice is returned")
	public void testGetMaintenanceInvoiceIds() throws Exception {
		final Long EXPECTED_INVOICE_DOC_ID = 1L;
				
		when(docDAO.getMaintenanceInvoiceIds(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Arrays.asList(new Long[]{EXPECTED_INVOICE_DOC_ID}));
		
		List<Long> actualIds = invoiceService.getMaintenanceInvoiceIds(new Date(), new Date());
		
		assertEquals(EXPECTED_INVOICE_DOC_ID, actualIds.get(0));
	}
	
	private InvoiceVO generateInvoice(int numberOfLines) {
		InvoiceVO invoice = new InvoiceVO();
		invoice.setExternalId(String.valueOf(-1 * System.currentTimeMillis()));
		invoice.setTranId(invoice.getExternalId().toString());
		invoice.setVendor(VENDOR_ACCOUNT_CODE);
		invoice.setApprovalDepartment(APPROVAL_DEPARTMENT);
		invoice.setPayableAccount(VendorBillSuiteTalkService.PAYABLE_ACCOUNT);
		invoice.setControlCode(ControlCodeEnum.ST);
		invoice.setTranDate(new Date());
		invoice.setSubsidiary(1L);
		invoice.setVendorEaaId(1L);
		
		IntStream.range(0, numberOfLines).parallel()
		.forEach(idx -> {
			InvoiceLineVO line = new InvoiceLineVO(invoice);
			line.setItem(VEHICLE_ITEM);
			line.setQuantity(BigDecimal.ONE);
			line.setRate(BigDecimal.ONE);
			line.setDepartment("");
			line.setBusinessUnit(BusinessUnitEnum.NONE);
			line.setLocation("");
			line.setDescription(String.format("Line %d Description ", idx));
			line.setUnit(mockUnit.getUnitNo());	
			line.setHeader(invoice);
			line.setClient("1,C,00000001");
			line.setDrvId(1L);
			line.setProductCode(ProductEnum.NONE);
			
			invoice.getLines().add(line);
		});


		return invoice;
	}
	
	private List<Map<String, Object>> generateMockVendorAddresses() {
		List<Map<String, Object>> mockVendorAddresses = new ArrayList<>();
		mockVendorAddresses.add(new HashMap<String, Object>());
		mockVendorAddresses.get(0).put("externalId","1");
		mockVendorAddresses.get(0).put("internalId", "10");
		return mockVendorAddresses;
	}	

}

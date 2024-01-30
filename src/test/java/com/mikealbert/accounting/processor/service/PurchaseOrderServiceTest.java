package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.PurchaseOrderSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

@SpringBootTest
@DisplayName("A Purchse Order")
public class PurchaseOrderServiceTest extends BaseTest{
	@Resource PurchaseOrderService purchaseOrderService;
	@Resource XRefService xRefService;
	
	@MockBean DocDAO docDAO;
	@MockBean VendorService vendorService;
	@MockBean PurchaseOrderSuiteTalkService purchaseOrderSuiteTalkService;
	
	static String VENDOR_ACCOUNT_CODE = "00040010";
	static String APPROVAL_DEPARTMENT = "Accounting";	
	static String VEHICLE_ITEM = "Vehicle";
	
	UnitVO mockUnit;
	private List<Map<String, Object>> mockVendorAddresses;
	
	@BeforeEach
	void putUnit() throws Exception {
		mockUnit = generateMockUnit();
		mockVendorAddresses = generateMockVendorAddresses();
	}
		
	@Test
	@DisplayName("when purchase order does not have a product code, all properties depending on the product code are setting correctly")	
	public void testGet() throws Exception {
		PurchaseOrderVO actualPO;
		PurchaseOrderLineVO actualLine;
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		PurchaseOrderLineVO mockLine = mockPO.getLines().get(0);		
		
		when(docDAO.hasDistRecord(ArgumentMatchers.anyLong())).thenReturn(true);
		when(docDAO.getPurchaseOrderHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockPO);
		when(docDAO.getPurchaseOrderLineByDocId(ArgumentMatchers.anyLong())).thenReturn(mockLine);
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		
		actualPO = purchaseOrderService.get(ArgumentMatchers.anyLong());
		actualLine = actualPO.getLines().get(0);
		
		assertEquals(mockPO.getExternalId(), actualPO.getExternalId());
		assertNotNull(actualLine);
		assertNotNull(actualLine.getHeader());
		assertEquals(actualLine.getDepartment(), null);
		assertEquals(BusinessUnitEnum.FLEET_SOLUTIONS, actualLine.getBusinessUnit());		
	}
	
	@Test
	@DisplayName("when new purchase order, the puchase order is sent to the external accounting system")
	public void testAdd() throws Exception {
		ArgumentCaptor<PurchaseOrderVO> poCaptor = ArgumentCaptor.forClass(PurchaseOrderVO.class);		
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		
		doNothing().when(purchaseOrderSuiteTalkService).add(poCaptor.capture());
		
		purchaseOrderService.add(mockPO);
		
		assertEquals(mockPO, poCaptor.getValue());
	}
	
	@Test
	@DisplayName("when adding a duplicate purchase order, the exception is suppressed")
	public void testAddDuplicate() throws Exception {
		ArgumentCaptor<PurchaseOrderVO> poCaptor = ArgumentCaptor.forClass(PurchaseOrderVO.class);		
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		
		doThrow(SuiteTalkDuplicateRecordException.class).when(purchaseOrderSuiteTalkService).add(poCaptor.capture());
		
		purchaseOrderService.add(mockPO);
		
		assertEquals(mockPO, poCaptor.getValue());
	}	
	
	@Test
	@DisplayName("when closing the purchase order, the puchase order is sent to be closed in the external accounting system")
	public void testClose() throws Exception {
		ArgumentCaptor<Long> docIdCaptor = ArgumentCaptor.forClass(Long.class);		
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		
		doNothing().when(purchaseOrderSuiteTalkService).close(docIdCaptor.capture());
		
		purchaseOrderService.closeExternal(Long.valueOf(mockPO.getExternalId()));
		
		assertEquals(mockPO.getExternalId(), docIdCaptor.getValue().toString());
	}	
	
	@Test
	@DisplayName("when updating the purchase order, the internal purchase order is sent to be updated in the external accounting system")
	public void testUpdate() throws Exception {
		ArgumentCaptor<PurchaseOrderVO> poCaptor = ArgumentCaptor.forClass(PurchaseOrderVO.class);		
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		PurchaseOrderLineVO mockLine = mockPO.getLines().get(0);

		when(docDAO.hasDistRecord(ArgumentMatchers.anyLong())).thenReturn(true);
		when(docDAO.getPurchaseOrderHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockPO);
		when(docDAO.getPurchaseOrderLineByDocId(ArgumentMatchers.anyLong())).thenReturn(mockLine);
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);		
		doNothing().when(purchaseOrderSuiteTalkService).update(poCaptor.capture());
		
		purchaseOrderService.update(Long.valueOf(mockPO.getExternalId()));
		
		assertEquals(mockPO, poCaptor.getValue());
	}	
	
	@Test
	@DisplayName("when revising the purchase order (PO), all existing purchase orders are closed and replaced with the new/revised PO in the external accounting system")
	public void testRevise() throws Exception {
		final Long OLD_PO_DOC_ID = -1L;
		
		ArgumentCaptor<Long> oldDocIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<PurchaseOrderVO> revisedPOCaptor = ArgumentCaptor.forClass(PurchaseOrderVO.class);
		ArgumentCaptor<String> revisedPOTranIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> duplicateRevisedPOTranIdCaptor = ArgumentCaptor.forClass(String.class);		
		
		List<Map<String, Object>> oldPOs = new ArrayList<>(0);
		oldPOs.add(new HashMap<>());
		oldPOs.get(0).put(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId().toLowerCase(), OLD_PO_DOC_ID.toString());
		
		PurchaseOrderVO mockNewPO = generatePurchaseOrder();
		PurchaseOrderLineVO mockNewLine = mockNewPO.getLines().get(0);		

		when(purchaseOrderSuiteTalkService.searchByExternalId(duplicateRevisedPOTranIdCaptor.capture())).thenReturn(null);
		when(purchaseOrderSuiteTalkService.searchByPoNumberAndVendor(revisedPOTranIdCaptor.capture(), ArgumentMatchers.anyString())).thenReturn(oldPOs);
		when(docDAO.hasDistRecord(ArgumentMatchers.anyLong())).thenReturn(true);
		when(docDAO.getPurchaseOrderHeaderByDocId(ArgumentMatchers.anyLong())).thenReturn(mockNewPO);
		when(docDAO.getPurchaseOrderLineByDocId(ArgumentMatchers.anyLong())).thenReturn(mockNewLine);
		when(vendorService.getAddresses(ArgumentMatchers.anyString())).thenReturn(mockVendorAddresses);
		
		doNothing().when(purchaseOrderSuiteTalkService).close(oldDocIdCaptor.capture());
		doNothing().when(purchaseOrderSuiteTalkService).add(revisedPOCaptor.capture());		
		
		purchaseOrderService.revise(Long.valueOf(mockNewPO.getExternalId()));
		
		assertEquals(mockNewPO.getExternalId(), revisedPOCaptor.getValue().getExternalId());
		assertEquals(mockNewPO.getTranId(), revisedPOTranIdCaptor.getValue());
		assertEquals(OLD_PO_DOC_ID, oldDocIdCaptor.getValue());
		assertEquals(mockNewPO, revisedPOCaptor.getValue());
	}	
	
	private PurchaseOrderVO generatePurchaseOrder() {
		PurchaseOrderVO po = new PurchaseOrderVO();
		po.setExternalId(String.valueOf(-1 * System.currentTimeMillis()));
		po.setTranId(po.getExternalId().toString());
		po.setVendor(VENDOR_ACCOUNT_CODE);
		po.setApprovalDepartment(APPROVAL_DEPARTMENT);
		po.setControlCode(ControlCodeEnum.ST);
		po.setTranDate(new Date());
		po.setSubsidiary(1L);
		po.setVendorEaaId(1L);
		
		PurchaseOrderLineVO line = new PurchaseOrderLineVO(po);
		line.setItem(VEHICLE_ITEM);
		line.setQuantity(BigDecimal.ONE);
		line.setRate(BigDecimal.ONE);
		line.setDepartment("");
		line.setBusinessUnit(BusinessUnitEnum.NONE);
		line.setLocation("");
		line.setDescription("Line Description");
		line.setUnit(mockUnit.getUnitNo());	
		line.setPurchaseOrder(po);
		line.setClient("1,C,00000001");
		line.setDrvId(1L);
		line.setProductCode(ProductEnum.NONE);

		po.getLines().add(line);

		return po;
	}
	
	private List<Map<String, Object>> generateMockVendorAddresses() {
		List<Map<String, Object>> mockVendorAddresses = new ArrayList<>();
		mockVendorAddresses.add(new HashMap<String, Object>());
		mockVendorAddresses.get(0).put("externalId","1");
		mockVendorAddresses.get(0).put("internalId", "10");
		return mockVendorAddresses;
	}	
	
	@Test
	@DisplayName("when closing the purchase order, the puchase order is sent to be closed in the internal accounting system")
	public void testCloseInternal() throws Exception {
		
		PurchaseOrderService mockSErvice = Mockito.spy(PurchaseOrderService.class);

		ArgumentCaptor<Long> docIdCaptor = ArgumentCaptor.forClass(Long.class);		
		
		PurchaseOrderVO mockPO = generatePurchaseOrder();
		
		doNothing().when(mockSErvice).closeInternal(docIdCaptor.capture());
		
		mockSErvice.closeInternal(Long.valueOf(mockPO.getExternalId()));
		
		verify(mockSErvice, times(1)).closeInternal(Long.valueOf(mockPO.getExternalId()));
	}	

}

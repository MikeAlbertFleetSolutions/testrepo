package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.enumeration.PurchaseOrderFieldEnum;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkImmutableRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkNoRecordFoundException;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.util.data.DateUtil;

@Disabled
@SpringBootTest
@DisplayName("An outbound Purchase Order")
public class PurchaseOrderSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource UnitSuiteTalkService unitSuiteTalkService;
	@Resource PurchaseOrderSuiteTalkService poSuiteTalkService;
		
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
	@DisplayName("when new PO, a PO is created in the external accounting system")	
	public void testCreate() throws Exception {
		PurchaseOrderVO po;
		PurchaseOrderLineVO line;
		
		Map<String, Object> extPO;
		
		po = generateMockPO(1);
		line = po.getLines().get(0);
		
		poSuiteTalkService.add(po);

		extPO = poSuiteTalkService.getByExternalId(po.getExternalId().toString());
		
		poSuiteTalkService.delete(po.getExternalId().toString());
			
		assertEquals(po.getExternalId(), (String)extPO.get(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId()), "Wrong PO");
		assertEquals(po.getTranId(), (String) extPO.get(PurchaseOrderFieldEnum.TRAN_ID.getScriptId()));
		assertTrue(((String) extPO.get(PurchaseOrderFieldEnum.ENTITY.getScriptId())).contains(po.getVendor()));		
		assertEquals(DateUtil.convertToLocalDate(po.getTranDate()), DateUtil.convertToLocalDate(((Calendar)extPO.get(PurchaseOrderFieldEnum.TRAN_DATE.getScriptId())).getTime()));
		assertEquals(po.getApprovalDepartment(), (String)extPO.get(PurchaseOrderFieldEnum.APPROVAL_DEPARTMENT.getScriptId()));
		assertTrue(((Boolean) extPO.get(PurchaseOrderFieldEnum.AUTO_APPROVE.getScriptId())));
		assertEquals(po.getVendorAddressInternalId(), Long.valueOf((String) extPO.get(PurchaseOrderFieldEnum.BILL_ADDRESS_LIST.getScriptId())));
		
		List<Map<String, Object>> items = (List<Map<String, Object>>) extPO.get(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.forEach(item -> {
			assertTrue(((String)((Map<String, Object>)item.get(PurchaseOrderFieldEnum.ITEM.getScriptId())).get("name")).contains(VEHICLE_ITEM));
			assertEquals(line.getQuantity(), BigDecimal.valueOf(((Double)item.get(PurchaseOrderFieldEnum.ITEM_QUANTITY.getScriptId())).longValue()));
			assertEquals(line.getRate(), new BigDecimal((String)item.get(PurchaseOrderFieldEnum.ITEM_RATE.getScriptId())));			
			assertEquals(line.getDepartment(), (String)((Map<String, Object>)item.get(PurchaseOrderFieldEnum.ITEM_DEPARTMENT.getScriptId())).get("name"));
			assertEquals(line.getBusinessUnit().getName(), (String)((Map<String, Object>)item.get(PurchaseOrderFieldEnum.ITEM_CLASSIFICATION.getScriptId())).get("name"));			
		});		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when revising the PO, a new PO with the same PO # of a closed PO is created in the external accounting system")	
	public void testCreateOfClosedPO() throws Exception {
		PurchaseOrderVO oldPO, newPO;
		PurchaseOrderLineVO newLine;		
		Map<String, Object> extOldPO, extNewPO;
		
		oldPO = generateMockPO(1);			
		poSuiteTalkService.add(oldPO);
		poSuiteTalkService.close(Long.valueOf(oldPO.getExternalId()));
		extOldPO = poSuiteTalkService.getByExternalId(oldPO.getExternalId().toString());

        newPO = generateMockPO(1);
		newPO.setTranId(oldPO.getTranId());
		newLine = newPO.getLines().get(0);
		newLine.setRate(new BigDecimal("2.00"));		
		poSuiteTalkService.add(newPO);
		extNewPO = poSuiteTalkService.getByExternalId(newPO.getExternalId().toString());		
		
		poSuiteTalkService.delete(oldPO.getExternalId().toString());
		poSuiteTalkService.delete(newPO.getExternalId().toString());
							
		assertEquals("Closed", (String)extOldPO.get(PurchaseOrderFieldEnum.STATUS.getScriptId()), "Old PO was not closed");
		assertEquals(extOldPO.get(PurchaseOrderFieldEnum.TRAN_ID.getScriptId()), extNewPO.get(PurchaseOrderFieldEnum.TRAN_ID.getScriptId()));
		assertNotEquals("Closed", (String)extNewPO.get(PurchaseOrderFieldEnum.STATUS.getScriptId()), "New PO is closed. Why?");
		assertNotEquals(extOldPO.get(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId()), extNewPO.get(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId()));
		
		List<Map<String, Object>> items = (List<Map<String, Object>>) extNewPO.get(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.forEach(item -> {
			assertEquals(newLine.getRate(), new BigDecimal((String)item.get(PurchaseOrderFieldEnum.ITEM_RATE.getScriptId())));						
		});				
	}	
		

	@Test
	@DisplayName("when new PO for unit that does exists, a retryable exception is thrown")	
	public void testCreateWithMissingUnit() { 
		assertThrows(RetryableSuiteTalkException.class, () -> {
			PurchaseOrderVO po = generateMockPO(1);	
			po.getLines().get(0).setUnit("FAKE");
			
			poSuiteTalkService.add(po);		
		});	
	}
	
	@Test
	@DisplayName("when new PO already exists in accounting system, a duplicate record exception is thrown")	
	public void testCreateDuplicatePO() throws Exception{ 
		PurchaseOrderVO po = generateMockPO(1);	
		poSuiteTalkService.add(po);	
		
		assertThrows(SuiteTalkDuplicateRecordException.class, () -> {			
			poSuiteTalkService.add(po);		
		});	
		
		poSuiteTalkService.delete(po.getExternalId().toString());		
	}	
	
	@Test
	@DisplayName("when update PO entity, PO entity is updated in the external accounting system")	
	public void testUpdateEntity() throws Exception {
		final String NEW_VENDOR = "00040010";
		PurchaseOrderVO po;
		
		Map<String, Object> extPO;
		
		po = generateMockPO(1);
		
		poSuiteTalkService.add(po);
		poSuiteTalkService.updateEntity(po.getExternalId().toString(), NEW_VENDOR);
		extPO = poSuiteTalkService.getByExternalId(po.getExternalId().toString());
		poSuiteTalkService.delete(po.getExternalId().toString());
			
		assertTrue(((String)extPO.get(PurchaseOrderFieldEnum.ENTITY.getScriptId())).contains(NEW_VENDOR));				
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when updating PO item, the PO item is updated in the external accounting system")	
	public void testUpdate() throws Exception {
		final String EQUIPMENT_ITEM = "Equipment";
		PurchaseOrderVO po;		
		Map<String, Object> extPO;
		
		po = generateMockPO(1);
		po.setAutoApprove(true);
		po.setControlCode(ControlCodeEnum.INVENTORY);
		
		poSuiteTalkService.add(po);
		
		po.getLines().get(0).setItem(EQUIPMENT_ITEM);		
		
		poSuiteTalkService.update(po);		
		extPO = poSuiteTalkService.getByExternalId(po.getExternalId().toString());
		poSuiteTalkService.delete(po.getExternalId().toString());
		
		List<Map<String, Object>> items = (List<Map<String, Object>>) extPO.get(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId());
				
		assertEquals(1, items.size());
		assertEquals(ControlCodeEnum.INVENTORY, ControlCodeEnum.valueOf((String)extPO.get(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId())));
		assertEquals(po.getVendorAddressInternalId(), Long.valueOf((String) extPO.get(PurchaseOrderFieldEnum.BILL_ADDRESS_LIST.getScriptId())));

		items.stream()
		.forEach(item -> {
			assertEquals(((String)((Map<String, Object>)item.get(PurchaseOrderFieldEnum.ITEM.getScriptId())).get("name")), EQUIPMENT_ITEM);
		});		
			
	}	

	@Test
	@DisplayName("when updating PO item that is fully billed, then a SuiteTalkImmutableException is thrown")	
	public void testUpdateFullyBilled() {
		assertThrows(SuiteTalkImmutableRecordException.class, () -> {		
		    PurchaseOrderVO po = generateMockPO(1)
		        .setInternalId("1733950")
		        .setExternalId("7194403")
		        .setAutoApprove(true)
		        .setControlCode(ControlCodeEnum.OE_IRENT);
		    
		    poSuiteTalkService.update(po);
		});			
	}		
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when updating closed PO item, the PO item is updated in the external accounting system")	
	public void testUpdateClosePO() throws Exception {
		final String EQUIPMENT_ITEM = "Equipment";
		PurchaseOrderVO po;		
		Map<String, Object> extPO;
		
		po = generateMockPO(1);
		po.setAutoApprove(true);
		po.setControlCode(ControlCodeEnum.INVENTORY);
		
		poSuiteTalkService.add(po);
		poSuiteTalkService.close(Long.valueOf(po.getExternalId()));
		
		po.getLines().get(0).setItem(EQUIPMENT_ITEM);		
		
		poSuiteTalkService.update(po);		
		extPO = poSuiteTalkService.getByExternalId(po.getExternalId().toString());
		poSuiteTalkService.delete(po.getExternalId().toString());
		
		List<Map<String, Object>> items = (List<Map<String, Object>>) extPO.get(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId());
				
		assertEquals(1, items.size());
		assertEquals(ControlCodeEnum.INVENTORY, ControlCodeEnum.valueOf((String)extPO.get(PurchaseOrderFieldEnum.UPDATE_CONTROL_CODE.getScriptId())));
		assertEquals(po.getVendorAddressInternalId(), Long.valueOf((String) extPO.get(PurchaseOrderFieldEnum.BILL_ADDRESS_LIST.getScriptId())));

		items.stream()
		.forEach(item -> {
			assertEquals(((String)((Map<String, Object>)item.get(PurchaseOrderFieldEnum.ITEM.getScriptId())).get("name")), EQUIPMENT_ITEM);
		});					
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("when closing PO, the PO is closed in the external accounting system")	
	public void testClose() throws Exception {
		PurchaseOrderVO po;		
		Map<String, Object> extPO;
		
		po = generateMockPO(1);
		po.setAutoApprove(true);
		
		poSuiteTalkService.add(po);
		poSuiteTalkService.close(Long.valueOf(po.getExternalId()));
		extPO = poSuiteTalkService.getByExternalId(po.getExternalId().toString());
		poSuiteTalkService.delete(po.getExternalId().toString());
		
		List<Map<String, Object>> items = (List<Map<String, Object>>) extPO.get(PurchaseOrderFieldEnum.ITEM_LIST.getScriptId());
		items.stream()
		.forEach(item -> {
			assertTrue(((boolean)item.get(PurchaseOrderFieldEnum.IS_CLOSED.getScriptId())));
		});		
			
	}
	 
	@Test 
	public void testGetByPoNumber() throws Exception {
		final String PO_NUMBER = "PON00273391";
		final String VENDOR_EXTERNAL_ID = "00162619";
		
		List<Map<String, Object>> poMaps = poSuiteTalkService.searchByPoNumberAndVendor(PO_NUMBER, VENDOR_EXTERNAL_ID);
		assertNotNull(poMaps);
		assertEquals(PO_NUMBER, poMaps.get(0).get(PurchaseOrderFieldEnum.TRAN_ID.getScriptId()));
	}
	
	@Test
	public void testGetByExternalId() throws Exception {
		final String EXTERNAL_ID = "6544733";
		Map<String, Object> poMap = poSuiteTalkService.getByExternalId(EXTERNAL_ID);
		assertEquals(EXTERNAL_ID, poMap.get(PurchaseOrderFieldEnum.EXTERNAL_ID.getScriptId()));
	}
	
	@Test
	@DisplayName("when closing a PO that does not exist, a SuiteTalkNoRecordFoundException exception is thrown")	
	public void testCloseNotExist() { 
		assertThrows(SuiteTalkNoRecordFoundException.class, () -> {
			PurchaseOrderVO po = generateMockPO(1);	
			
			poSuiteTalkService.close(Long.valueOf(po.getExternalId()));
		});	
	}
	
	@Disabled
	@Test
	@DisplayName("load testing this...")	
	public void doomsTest() throws Exception {

		IntStream.range(0, 10)
		.forEach(idx -> {
			try {
				PurchaseOrderVO po;
				po = generateMockPO(1);

				poSuiteTalkService.add(po);		
				poSuiteTalkService.getByExternalId(po.getExternalId().toString());
				poSuiteTalkService.delete(po.getExternalId().toString());
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});		

	}	
		
	@Override
	protected String getUnitNo(){
		return mockUnit.getUnitNo();
	}
}

package com.mikealbert.accounting.processor.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.item.CreditItemHelper;
import com.mikealbert.accounting.processor.item.InvoiceItemHelper;
import com.mikealbert.accounting.processor.item.PurchaseOrderItemHelper;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("A transaction")
public class ItemHelperTest extends BaseTest{
	@Resource PurchaseOrderItemHelper purchaseOrderItemHelper;
	@Resource InvoiceItemHelper invoiceItemHelper;
	@Resource CreditItemHelper creditItemHelper;
	
	static final String AUTO_INTEGRATE_GL_CODE = "01550024215";

	@Test
	@DisplayName("when AP invoice is for FLFINE that is rebillable, the rebillable item key is generated")	
	public void testInvoiceItemHelperForFLFINERebillableItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLFINE\",\"index\":3}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLFINE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setRebillableClientFine(true);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}		

	@Test
	@DisplayName("when AP invoice is for FLFINE DEMO product where driver is BDM, the Fine - Selling item key is generated")	
	public void testInvoiceItemHelperForFLFINESellingItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLFINE\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLFINE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setDepartment("Business Development Managers");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}			

	@Test
	@DisplayName("when AP invoice is for FLLICENSE that is rebillable, the rebillable item key is generated")	
	public void testInvoiceItemHelperForFLLICENSERebillableItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLLICENSE\",\"index\":3}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLLICENSE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setRebillableLicenseFee(true);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}

	@Test
	@DisplayName("when AP invoice is for FLLICENSE DEMO unit with Cost Center of CPM, the Title & License - Selling item key is generated")	
	public void testInvoiceItemHelperForFLLICENSESellingItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLLICENSE\",\"index\":0}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLLICENSE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setDepartment("Client Partnership Managers");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	

	@Test
	@DisplayName("when AP invoice is for FLLICENSE DEMO unit with Cost Center of Delivery, the Title & License - Selling item key is generated")	
	public void testInvoiceItemHelperForFLLICENSEGandAItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLLICENSE\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLLICENSE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setDepartment("Delivery");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Test
	@DisplayName("when AP invoice is for FLLICENSE Sales Tax Driver Sales, the Sales Tax - Driver Sales item key is generated")	
	public void testInvoiceItemHelperForFLLICENSESalesTaxDriverSales() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLLICENSE\",\"index\":2}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLLICENSE);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setRebillableLicenseFee(false);
		line.setGlCode("01220010000");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}
	
	@Test
	@DisplayName("when FLMAINT AP invoice is for CE product, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTAndCE() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.CE_LTD);
		line.setGlCode("01260005000");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Test
	@DisplayName("when FLMAINT AP invoice is for ST product, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTAndST() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":2}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.ST);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	

	@Test
	@DisplayName("when FLMAINT AP invoice is for DEMO product and department 'is in' list, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTAndDEMOAndInDepartment() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":3}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setDepartment("Business Development Managers");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}
		
	@Test
	@DisplayName("when FLMAINT AP invoice is for DEMO product and department is NOT in list, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTAndDEMOAndNotInDepartment() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":4}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	



	@Test
	@DisplayName("when FLMAINT AP invoice is for AI Fee, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTAiFee() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":0}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.CE_LTD);
		line.setGlCode("01550024215");
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Test
	@DisplayName("when AP invoice is for FLMAINTNA, the correct item key is generated")	
	public void testInvoiceItemHelperForFLMAINTNA() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINTNA\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.FLMAINTNA);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}
	
	@Test
	@DisplayName("when AP invoice is for DE, the correct item key is generated")	
	public void testInvoiceItemHelperForDE() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"DE\",\"index\":0}";

		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.DE);

		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);

		String key = invoiceItemHelper.generateKey(line);

		assertEquals(EXPECTED_KEY, key);		
	}

	@Test
	@DisplayName("when AP invoice is for DEMO, the correct item key is generated")	
	public void testInvoiceItemHelperForDEMO() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"DEMO\",\"index\":0}";

		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.DEMO);


		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setDepartment("BDM");

		String actualKey = invoiceItemHelper.generateKey(line);

		assertEquals(EXPECTED_KEY, actualKey);		
	}

	@Test
	@DisplayName("when AP invoice is for DEMOMAINT invoice where the line gl code is for AI, the AI fee item key is generated")	
	public void testInvoiceItemHelperForDEMOMAINTAutoIntegrateItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"DEMOMAINT\",\"index\":0}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.DEMOMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setGlCode(AUTO_INTEGRATE_GL_CODE);
		
		String actualKey = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, actualKey);		
	}

	@Test
	@DisplayName("when AP invoice is for DEMOMAINT invoice ST, the Maintenance - Selling item key is generated")	
	public void testInvoiceItemHelperForDEMOMAINTMaintenanceSellingItem() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"DEMOMAINT\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.DEMOMAINT);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setDepartment("Marketing");
		
		String actualKey = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, actualKey);		
	}	

	@Test
	@DisplayName("when AP invoice is for UCMAINT, the correct item key is generated")	
	public void testInvoiceItemHelperForUCMAINT() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"UCMAINT\",\"index\":1}";
		
		InvoiceVO invoice = new InvoiceVO();
		invoice.setControlCode(ControlCodeEnum.UCMAINT);
		invoice.setDisposalFiscalYear(2020L);
		invoice.setCurrentFiscalYear(2020L);
		
		InvoiceLineVO line = new InvoiceLineVO(invoice);
		line.setProductCode(ProductEnum.DEMO);
		line.setAssetType(AssetTypeEnum.UC);
		
		String key = invoiceItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Test
	@DisplayName("when Credit is for UCMAINT, the correct item key is generated")	
	public void testCreditItemHelperForUCMAINT() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"UCMAINT\",\"index\":0}";
		
		CreditVO po = new CreditVO();
		po.setControlCode(ControlCodeEnum.UCMAINT);
		
		CreditLineVO line = new CreditLineVO(po);
		line.setProductCode(ProductEnum.DEMO);
		line.setAssetType(AssetTypeEnum.UC);
		
		String key = creditItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}
	
	@Test
	@DisplayName("when Credit is for DEMOMAINT, the correct item key is generated")	
	public void testCreditItemHelperForDemoMaint() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"DEMOMAINT\",\"index\":0}";
		
		CreditVO po = new CreditVO();
		po.setControlCode(ControlCodeEnum.DEMOMAINT);
		
		CreditLineVO line = new CreditLineVO(po);
		line.setProductCode(ProductEnum.DEMO);
		
		String key = creditItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Disabled
	@Test
	@DisplayName("when Purchase Order is for FLMAINT, the correct item key is generated")	
	public void testPurchaseOrderItemHelperForFLMAINT() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINT\",\"index\":1}";
		
		PurchaseOrderVO po = new PurchaseOrderVO();
		po.setControlCode(ControlCodeEnum.FLMAINT);
		
		PurchaseOrderLineVO line = new PurchaseOrderLineVO(po);
		line.setProductCode(ProductEnum.DEMO);
		line.setGlCode("01260005000");
		
		String key = purchaseOrderItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	

	@Disabled
	@Test
	@DisplayName("when Purchase Order is for FLMAINTNA, the correct item key is generated")	
	public void testPurchaseOrderItemHelperForFLMAINTNA() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"FLMAINTNA\",\"index\":1}";
		
		PurchaseOrderVO po = new PurchaseOrderVO();
		po.setControlCode(ControlCodeEnum.FLMAINTNA);
		
		PurchaseOrderLineVO line = new PurchaseOrderLineVO(po);
		line.setProductCode(ProductEnum.DEMO);
		
		String key = purchaseOrderItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
	
	@Disabled	
	@Test
	@DisplayName("when Purchase Order is for UCMAINT, the correct item key is generated")	
	public void testPurchaseOrderItemHelperForUCMAINT() throws Exception {
		final String EXPECTED_KEY = "{\"controlCode\":\"UCMAINT\",\"index\":1}";
		
		PurchaseOrderVO po = new PurchaseOrderVO();
		po.setControlCode(ControlCodeEnum.UCMAINT);
		po.setDisposalFiscalYear(2020L);
		po.setCurrentFiscalYear(2020L);
		
		PurchaseOrderLineVO line = new PurchaseOrderLineVO(po);
		line.setProductCode(ProductEnum.DEMO);
		line.setAssetType(AssetTypeEnum.UC);
		
		String key = purchaseOrderItemHelper.generateKey(line);
		 
		assertEquals(EXPECTED_KEY, key);		
	}	
}

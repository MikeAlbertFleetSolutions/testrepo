package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.vo.ClientDepositApplicationVO;

@DisplayName("Given a deposit application request")
@SpringBootTest
public class DepositApplicationSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource DepositApplicationSuiteTalkService depositApplicationSuiteTalkService;
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	
	@Disabled
	@DisplayName("when getting the deposit application, then the deposit application is returned with all expected fields populated")
	@Test
	public void testGet() throws Exception {  
		final String DEPOSIT_APPLICATION_INTERNAL_ID = "1400716";     

		ClientDepositApplicationVO actualClientDepositApplicationVO = depositApplicationSuiteTalkService.get(DEPOSIT_APPLICATION_INTERNAL_ID, null);

		assertNotNull(actualClientDepositApplicationVO);
	}	

	@Disabled
	@DisplayName("when getting the deposit applicationn amount applied to a specific invoice, then the deposit application amount applied to the invoice is returned")
	@Test
	public void testGetAmountAppledToInvoice() throws Exception {  
		final String DEPOSIT_APPLICATION_INTERNAL_ID = "1400716";     
		final String CLIENT_INVOICE_INTERNAL_ID = "1394893";  		

		BigDecimal amount = depositApplicationSuiteTalkService.getAmountAppledToInvoice(DEPOSIT_APPLICATION_INTERNAL_ID, CLIENT_INVOICE_INTERNAL_ID);

		assertEquals(new BigDecimal("2.5"), amount);
	}	

	public void test() {
		assertTrue(true);
	}

	@Override
	protected String getUnitNo() {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

@DisplayName("Given a request")
@SpringBootTest
public class InvoiceSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	@Resource TransactionSuiteAnalyticsService transactionSuiteAnalyticsService;
	
	@DisplayName("when a new customer invoice, then the customer invoice is returned with all expected fields populated")
	@Disabled("Need to activate later")
	@Test
	public void testGetInvoiceAllFieldsSet() throws Exception {       
		ClientInvoiceVO expectedInvoiceVO = generateMockCustomerInvoice(1);
		expectedInvoiceVO.setMaType(ControlCodeEnum.FLBILLING.name());

		createWrapper(expectedInvoiceVO);

		ClientInvoiceVO actualInvoiceVO = invoiceSuiteTalkService.get(expectedInvoiceVO.getInternalId(), expectedInvoiceVO.getExternalId().toString());
				
		invoiceSuiteTalkService.delete(actualInvoiceVO);
		
		assertEquals(expectedInvoiceVO.getExternalId(), actualInvoiceVO.getExternalId());
		assertEquals(expectedInvoiceVO.isGrouped(), actualInvoiceVO.isGrouped());
		
		assertNotNull(actualInvoiceVO.getTranId());
		assertNotNull(actualInvoiceVO.getTranDate());
		assertNotNull(actualInvoiceVO.getPayableAccount());
		assertNotNull(actualInvoiceVO.getStatus());
		assertNotNull(actualInvoiceVO.getSubsidiary());
		assertNotNull(actualInvoiceVO.getDocId());
		//assertNotNull(actualInvoiceVO.getDocLineId());
		//assertNotNull(actualInvoiceVO.getMaType());
		assertNotNull(actualInvoiceVO.getGroupNumber());

		for(int i = 0; i < actualInvoiceVO.getLines().size(); i++) {
			assertEquals(expectedInvoiceVO.getLines().get(i).getItem(), actualInvoiceVO.getLines().get(i).getItem());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDescription(), actualInvoiceVO.getLines().get(i).getDescription());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDepartment(), actualInvoiceVO.getLines().get(i).getDepartment());
			assertEquals(expectedInvoiceVO.getLines().get(i).getRate(), actualInvoiceVO.getLines().get(i).getRate());
			assertEquals(expectedInvoiceVO.getLines().get(i).getQuantity(), actualInvoiceVO.getLines().get(i).getQuantity());
			assertEquals(expectedInvoiceVO.getLines().get(i).getLinePaidAmount(), actualInvoiceVO.getLines().get(i).getLinePaidAmount());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDocId(), actualInvoiceVO.getLines().get(i).getDocId());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDocLineId(), actualInvoiceVO.getLines().get(i).getDocLineId());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverId(), actualInvoiceVO.getLines().get(i).getDriverId());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverFirstName(), actualInvoiceVO.getLines().get(i).getDriverFirstName());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverLastName(), actualInvoiceVO.getLines().get(i).getDriverLastName());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverState(), actualInvoiceVO.getLines().get(i).getDriverState());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverCostCenter(), actualInvoiceVO.getLines().get(i).getDriverCostCenter());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverCostCenterDescription(), actualInvoiceVO.getLines().get(i).getDriverCostCenterDescription());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverRechargeCode(), actualInvoiceVO.getLines().get(i).getDriverRechargeCode());
			assertEquals(expectedInvoiceVO.getLines().get(i).getDriverFleetRefNo(), actualInvoiceVO.getLines().get(i).getDriverFleetRefNo());
			assertNotNull(actualInvoiceVO.getLines().get(i).getMaType()); 
		}		
	}	
	
	//TODO Trail accounting period based on the today's date. Maybe a few months back.
	@DisplayName("when searching for outstanding transactions based on MA Type, then search yields all matching invoices")
	@Test
	public void testFindOutstanding() throws Exception {
		final String ACCOUNTING_PERIOD = "Feb 2023";
		final String MA_TYPE = BillingReportTypeEnum.RENTAL.getMaType();
		final String TXN_STATUS_OPEN = "Open";
		final String TXN_STATUS_CODE_OPEN = "A";

		List<Map<String, Object>> transactions = transactionSuiteAnalyticsService.findByPeriodAndMaType(ACCOUNTING_PERIOD, MA_TYPE).parallelStream()
		    .filter(txn -> ((String)txn.get("status")).equalsIgnoreCase(TXN_STATUS_CODE_OPEN))
			.collect(Collectors.toList());

		if(transactions.size() > 0) {
			List<ClientInvoiceVO> results = invoiceSuiteTalkService.findOustanding(String.valueOf(transactions.get(5).get("entity")), null, MA_TYPE);
			
			results.forEach(result -> {
				assertEquals(ControlCodeEnum.FLBILLING.name(), result.getMaType());
				assertTrue(TXN_STATUS_OPEN.equalsIgnoreCase(result.getStatus()));	
			});

			assertTrue(results.size() > 0);
		}
	}

	private ClientInvoiceVO generateMockCustomerInvoice(int numberOfLines) throws Exception {
		Thread.sleep(5);
		String uid = "-" + String.valueOf(System.currentTimeMillis());
		
		ClientInvoiceVO invoiceVO = new ClientInvoiceVO()
				.setExternalId(uid)
				.setClientExternalId("1C00032816")
				.setGrouped(true)
				.setGroupNumber(uid)
				.setAutoApprove(false)
				.setDueDate(new Date())
				.setDocId(-1L)
				.setDocLineId(-1L);
		
		IntStream.range(0, numberOfLines)
		.forEach(idx -> {
			ClientInvoiceLineVO lineVO = new ClientInvoiceLineVO(invoiceVO)
					.setItem(super.RENT_ITEM)
					.setQuantity(BigDecimal.ONE)
					.setRate(new BigDecimal("1.00"))
					.setDescription("Monthly Rental")
					.setLinePaidAmount(new BigDecimal(".50"))
					.setDocId(-1L)
					.setDocLineId(-1L)
					.setDriverId(-1L)
					.setDriverFirstName("First Name")
					.setDriverLastName("Last Name")
					.setDriverState("OH")
					.setDriverCostCenter("Cost Center")
					.setDriverCostCenterDescription("Cost Center Desc")
					.setDriverRechargeCode("Recharge Code")
					.setDriverFleetRefNo("Fleet Ref No")
					.setMonthServiceDate(new Date())
					.setTransactionLineDate(new Date());

			invoiceVO.getLines().add(lineVO);
		});
		
		return invoiceVO;
	}

	private void createWrapper(ClientInvoiceVO invoiceVO) throws Exception {
		invoiceSuiteTalkService.create(invoiceVO);
	}

	@Override
	protected String getUnitNo() {
		// TODO Auto-generated method stub
		return null;
	}

}

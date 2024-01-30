package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

@DisplayName("Given a request ")
@SpringBootTest
public class AgingTransactionSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource InvoiceSuiteTalkService invoiceSuiteTalkService;
	@Resource AgingTransactionSuiteTalkService transactionSuiteTalkService;
		
	@Disabled("SB1 contains multiple subsidiary tax regestrations, as a result this test will fail. Enable post refresh/when there is on registration")
	@DisplayName("when requesting aging transactions within a specific period, then all aging transactions within the specified period are returned")
	@Test
	public void testGetAgingByClientAndPeriod() throws Exception {
		final AgingPeriodEnum agingPeriod = AgingPeriodEnum.AGING_90;

		Calendar cal = Calendar.getInstance();
		ClientInvoiceVO mockInvoice;

		cal.add(Calendar.DAY_OF_MONTH, -1 * (agingPeriod.getMax().intValue()));
		mockInvoice = generateMockCustomerInvoice(1);
		mockInvoice.setTranDate(cal.getTime());
		mockInvoice.setDueDate(cal.getTime());
	
		createWrapper(mockInvoice);

		List<ReceivableTransactionVO<?, ?>> actualTransactions = transactionSuiteTalkService.getAging(null, mockInvoice.getClientExternalId(), agingPeriod);	
				
		invoiceSuiteTalkService.delete(mockInvoice);

		assertNotNull(
			actualTransactions.stream()
					.filter(transaction -> mockInvoice.getExternalId().equals(transaction.getExternalId()))
					.findFirst()
					.orElse(null)
		);	
		
		assertNull(
			actualTransactions.stream()
					.filter(transaction -> transaction.getDaysOverdue().compareTo(agingPeriod.getMin()) < 0 || transaction.getDaysOverdue().compareTo(agingPeriod.getMax()) > 0)
					.findFirst()
					.orElse(null)
		);	
	}
	
	private ClientInvoiceVO generateMockCustomerInvoice(int numberOfLines) throws Exception {
		Thread.sleep(5);
		String uid = "-" + String.valueOf(System.currentTimeMillis());
		
		ClientInvoiceVO invoiceVO = new ClientInvoiceVO()
				.setExternalId(uid)
				.setClientExternalId("1C00032816")
				.setGrouped(true)
				.setGroupNumber(uid)
				.setDueDate(new Date())
				.setDocId(-1L)
				.setDocLineId(-1L)
				.setAutoApprove(true)
				.setSkipApproval("Accounting Approval");
		
		IntStream.range(0, numberOfLines)
		.forEach(idx -> {
			ClientInvoiceLineVO lineVO = new ClientInvoiceLineVO(invoiceVO)
			        .setItem(super.RENT_ITEM)
					.setQuantity(BigDecimal.ONE)
					.setRate(new BigDecimal("1.00"))
					.setDocId(-1L)
					.setDocLineId(-1L)
					.setMaType(ControlCodeEnum.FLBILLING.name());

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

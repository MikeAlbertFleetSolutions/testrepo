package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.PaymentMethodEnum;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;
import com.mikealbert.util.data.DateUtil;

@DisplayName("Given a client's payment")
@SpringBootTest
public class CustomerPaymentSuiteTalkServiceTest extends BaseTest{
	@Resource CustomerPaymentSuiteTalkService customerPaymentSuiteTalkService;
	

	@DisplayName("when a new customer payment, then the customer payment is returned with all expected fields populated")
	@Test
	public void testGetPaymentAllFieldsSet() throws Exception {       
		ClientPaymentVO expectedPaymentVO = generateMockCustomerPayment();

		createWrapper(expectedPaymentVO);

		ClientPaymentVO actualPaymentVO = customerPaymentSuiteTalkService.getPayment(null, expectedPaymentVO.getExternalId().toString());

		customerPaymentSuiteTalkService.delete(actualPaymentVO);

		assertEquals(expectedPaymentVO.getExternalId(), actualPaymentVO.getExternalId());
		assertEquals(expectedPaymentVO.getMemo(), actualPaymentVO.getMemo());
		assertEquals(expectedPaymentVO.getTranDate(), actualPaymentVO.getTranDate());
		assertEquals(expectedPaymentVO.getAmount(), actualPaymentVO.getAmount().setScale(2));
		assertEquals(expectedPaymentVO.getAppliedAmount(), actualPaymentVO.getAppliedAmount().setScale(2));
		assertEquals(expectedPaymentVO.getUnAppliedAmount(), actualPaymentVO.getUnAppliedAmount().setScale(2));		

		assertNotNull(actualPaymentVO.getTranId());	
		
		//assertTrue(actualPaymentVO.getLines().size() > 0);

		actualPaymentVO.getLines().stream().parallel()
		        .forEach(line -> assertFalse(line.isApply()));
	}	

	@DisplayName("when updating its external id, the update is saved in the the accounting system")
	@Test
	public void testUpdate() throws Exception {
		ClientPaymentVO expectedPaymentVO, actualPaymentVO;

		expectedPaymentVO = generateMockCustomerPayment();
		createWrapper(expectedPaymentVO);

		String newExternalId = generateMockCustomerPayment().getExternalId();

		actualPaymentVO = customerPaymentSuiteTalkService.getPayment(null, expectedPaymentVO.getExternalId().toString());
		actualPaymentVO.setExternalId(newExternalId);
		actualPaymentVO.setPaymentMethod(expectedPaymentVO.getPaymentMethod()); // TODO This is a hack around NS not persisting the Payment Method assigned in the create.
		customerPaymentSuiteTalkService.update(actualPaymentVO);

		actualPaymentVO = customerPaymentSuiteTalkService.getPayment(actualPaymentVO.getInternalId(), null);

		customerPaymentSuiteTalkService.delete(actualPaymentVO);
		
		assertEquals(newExternalId, actualPaymentVO.getExternalId());
	}

	@DisplayName("when reqiesting the last payment a client made, the last payment is returned")
	@Test
	public void getLastPayment() throws Exception {
		ClientPaymentVO mockPayment1, mockPayment2;

		mockPayment1 = generateMockCustomerPayment();
		mockPayment2 = generateMockCustomerPayment();
		
		createWrapper(mockPayment1);
		createWrapper(mockPayment2);

		ClientPaymentVO actualPayment = customerPaymentSuiteTalkService.getLastPayment(null, mockPayment1.getClientExternalId());	

		customerPaymentSuiteTalkService.delete(mockPayment1);
		customerPaymentSuiteTalkService.delete(mockPayment2);

		assertEquals(mockPayment2.getExternalId(), actualPayment.getExternalId());
	}

	@DisplayName("when payment is a journal entry, then the details of the journal entery is returned")
	@Test
	public void getPaymentApplyJournalEntry() throws Exception {
		final String INVOICE_INTERNAL_ID = "2721635";
		final String PAYMENT_INTERNAL_ID = "2663977";
		final String PAYMENT_EXTERNAL_ID = null;
		final String PAYMENT_TYPE = "journalentry";
		
		ClientPaymentApplyVO clientPaymentApplyVO = customerPaymentSuiteTalkService.getPaymentApply(INVOICE_INTERNAL_ID, PAYMENT_INTERNAL_ID, PAYMENT_EXTERNAL_ID, PAYMENT_TYPE);

		assertNotNull(clientPaymentApplyVO);
		assertNotNull(clientPaymentApplyVO.getTranId());
		assertNotNull(clientPaymentApplyVO.getTranDate());
		assertEquals(PAYMENT_INTERNAL_ID, clientPaymentApplyVO.getInternalId());
	}	

	private ClientPaymentVO generateMockCustomerPayment() throws Exception {
		Thread.sleep(5);
		String uid = "-" + String.valueOf(System.currentTimeMillis());
		
		ClientPaymentVO paymentVO = new ClientPaymentVO();
		paymentVO.setExternalId(uid);
		paymentVO.setClientExternalId("1C00032816");
		paymentVO.setReference("UNIT TEST");
		paymentVO.setAmount(new BigDecimal("1.00"));
		paymentVO.setAppliedAmount(new BigDecimal("0.00"));
		paymentVO.setUnAppliedAmount(new BigDecimal("1.00"));		
		paymentVO.setMemo("Unit Test");
		paymentVO.setTranDate(DateUtil.now(DateUtil.PATTERN_DATE)); 
		paymentVO.setPaymentMethod(PaymentMethodEnum.ACH.name());
		//paymentVO.setPaymentMethod("Lockbox");
		
		return paymentVO;
	}

	private void createWrapper(ClientPaymentVO paymentVO) throws Exception {
		customerPaymentSuiteTalkService.create(paymentVO);
	}
	
}

package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.DepositApplicationSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.InvoiceSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.constant.enumeration.ApplicationEnum;

@SpringBootTest
@DisplayName("Given client's invoice deposit application")
public class ClientInvoiceDepositServiceTest extends BaseTest{
	@Resource ClientInvoiceDepositService clientInvoiceDepositService;

	@MockBean ClientTransactionDAO clientTransactionDAO;
	@MockBean InvoiceSuiteTalkService invoiceSuiteTalkService;
	@MockBean DepositApplicationSuiteTalkService depositApplicationSuiteTalkService;

	@BeforeEach
	void up() throws Exception {}
		
	@Test
	@DisplayName("when applied to a grouped invoice, then the details of the transaction is sent to the data layer for further processing")	
	public void testProcess() throws Exception {

		ClientInvoiceDepositVO mockClientInvoiceDepositVO = new ClientInvoiceDepositVO()
				.setDepositApplicationInternalId("n")
				.setDepositApplicationTranId("nn")
				.setInvoiceInternalId("nnn")
				.setInvoiceTranId("nnnn")
				.setDocId(0L)
				.setDocLineId(-1L)
				.setAmountApplied(BigDecimal.ONE);

		doNothing().when(clientTransactionDAO).processInvoiceDeposit(any());

		clientInvoiceDepositService.process(mockClientInvoiceDepositVO);

		verify(clientTransactionDAO, times(1)).processInvoiceDeposit(eq(mockClientInvoiceDepositVO));
	}

	@Test
	@DisplayName("when requesting client depsoit for an invoice that does not have a doc id reference on all lines, then client deposit VO is returned intialized with the invoice header data and summed line paid amounts")
	public void testDepositsWithoutDocOnAllLines() throws Exception {
		ClientInvoiceVO mockInvoice = new ClientInvoiceVO()
		        .setDocId(-1L)
				.setDocLineId(-2L)
				.setOrigin(ApplicationEnum.WILLOW);
		mockInvoice.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("1")).setDocId(-5L).setDocLineId(-6L));
		mockInvoice.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("1")));

		List<ClientInvoiceVO> mockInvoices = new ArrayList<>(0);
		mockInvoices.add(mockInvoice);

		ClientInvoiceDepositVO expectedDeposit = new ClientInvoiceDepositVO()
		        .setDocId(-1L)
				.setDocLineId(-2L)
				.setAmountApplied(new BigDecimal("2"));

		List<ClientInvoiceDepositVO> expectedDeposits = new ArrayList<>(0);
		expectedDeposits.add(expectedDeposit);

		List<ClientInvoiceDepositVO> actualDeposits = clientInvoiceDepositService.deposits(mockInvoices);

		assertEquals(expectedDeposits,  actualDeposits);
	}

	@Test
	@DisplayName("when requesting client depsoit for an invoice that has a doc id reference on all lines, then client deposit VO is returned intialized with the invoice line data")
	public void testDepositsWitDocOnAllLines() throws Exception {
		ClientInvoiceVO mockInvoice = new ClientInvoiceVO()
		        .setDocId(-1L)
				.setDocLineId(-2L)
				.setOrigin(ApplicationEnum.WILLOW);
		mockInvoice.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("1")).setDocId(-3L).setDocLineId(-4L));
		mockInvoice.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("1")).setDocId(-5L).setDocLineId(-6L));

		List<ClientInvoiceVO> mockInvoices = new ArrayList<>(0);
		mockInvoices.add(mockInvoice);

		List<ClientInvoiceDepositVO> expectedDeposits = new ArrayList<>(0);
		expectedDeposits.add(new ClientInvoiceDepositVO()
				.setDocId(-3L)
				.setDocLineId(-4L)
				.setAmountApplied(new BigDecimal("1")));
		expectedDeposits.add(new ClientInvoiceDepositVO()
				.setDocId(-5L)
				.setDocLineId(-6L)
				.setAmountApplied(new BigDecimal("1")));				

		List<ClientInvoiceDepositVO> actualDeposits = clientInvoiceDepositService.deposits(mockInvoices);

		assertEquals(expectedDeposits,  actualDeposits);
	}
}

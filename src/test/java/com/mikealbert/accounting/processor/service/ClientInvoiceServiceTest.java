package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.mikealbert.accounting.processor.client.suitetalk.InvoiceSuiteTalkService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ApplicationEnum;

@SpringBootTest
@DisplayName("Given an invoice(s)")
public class ClientInvoiceServiceTest extends BaseTest{
	@Resource ClientInvoiceService clientInvoiceService;

	@MockBean InvoiceSuiteTalkService invoiceSuiteTalkService;
	
	@BeforeEach
	void up() throws Exception {}
			
	@Test
	@DisplayName("when ..., then ...")	
	public void testFindOutstanding() throws Exception {
		final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";
		final ControlCodeEnum MA_TYPE = ControlCodeEnum.FLBILLING;

		List<ClientInvoiceVO> expectedClientInvoiceVOs = new ArrayList<>(0);
		expectedClientInvoiceVOs.add(new ClientInvoiceVO());

		when(invoiceSuiteTalkService.findOustanding(any(), any(), any())).thenReturn(expectedClientInvoiceVOs);

		List<ClientInvoiceVO> actualInvoiceVOs = clientInvoiceService.findOutStanding(null, CLIENT_EXTERNAL_ID, MA_TYPE);

		verify(invoiceSuiteTalkService, times(1)).findOustanding(isNull(), eq(CLIENT_EXTERNAL_ID), eq(MA_TYPE.name()));
		
		assertTrue(actualInvoiceVOs.size() > 0);
	}	

	@Test
	@DisplayName("when invoice has a balance, then it is added to the total")	
	public void testTotalUnpaidBalance() throws Exception {
		List<ClientInvoiceVO> clientInvoiceVOs = new ArrayList<>(0);

		clientInvoiceVOs.add(new ClientInvoiceVO());
		clientInvoiceVOs.add(new ClientInvoiceVO());
		clientInvoiceVOs.add(new ClientInvoiceVO());

		clientInvoiceVOs.get(0).setBalance(BigDecimal.ONE);
		clientInvoiceVOs.get(1).setBalance(BigDecimal.ONE);

		assertEquals(new BigDecimal("2"), clientInvoiceService.sumBalance(clientInvoiceVOs));
	}
	
	@Test
	@DisplayName("when invoice has line amount paid on the line(s), then the sum of line amount paid is correct")
	public void testSumLineAmountPaid() throws Exception {
		ClientInvoiceVO clientInvoiceVO = new ClientInvoiceVO();
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("5")));
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(new BigDecimal("5")));
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO().setLinePaidAmount(null));

		BigDecimal total = clientInvoiceService.sumLinePaidAmount(clientInvoiceVO);

		assertEquals(BigDecimal.TEN, total);
	}

	@Test
	@DisplayName("when checking an invoice that has a line with doc ids, then true is returned")
	public void testHasDocOnLineAll() {
		ClientInvoiceVO clientInvoiceVO = new ClientInvoiceVO();
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO().setDocId(-1L).setDocLineId(-1L));		

		boolean result = clientInvoiceService.hasDocOnLine(clientInvoiceVO);

		assertTrue(result);
	}

	@Test
	@DisplayName("when checking an invoice where not all lines have doc ids, then false is returned")
	public void testHasDocOnLineOneWithout() {
		ClientInvoiceVO clientInvoiceVO = new ClientInvoiceVO();
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO());
		clientInvoiceVO.getLines().add(new ClientInvoiceLineVO().setDocId(-1L).setDocLineId(-1L));		

		boolean result = clientInvoiceService.hasDocOnLine(clientInvoiceVO);

		assertFalse(result);
	}	

	@Test
	@DisplayName("when checking an invoice that has no lines, then false is returned")
	public void testHasDocOnLineNoLines() {
		ClientInvoiceVO clientInvoiceVO = new ClientInvoiceVO();

		boolean result = clientInvoiceService.hasDocOnLine(clientInvoiceVO);

		assertFalse(result);
	}
	
	@Test
	@DisplayName("when filtering internal invoices from a list of containing internal invoices, then only the internal invoices are returned")
	public void testFilterInternalInvoices() {
		List<ClientInvoiceVO> mockClientInvoiceVOs = new ArrayList<>(0);
		mockClientInvoiceVOs.add(new ClientInvoiceVO().setOrigin(ApplicationEnum.NETSUITE));
		mockClientInvoiceVOs.add(new ClientInvoiceVO().setOrigin(ApplicationEnum.WILLOW));

		List<ClientInvoiceVO> expectedClientInvoiceVOs = new ArrayList<>(0);
		expectedClientInvoiceVOs.add(mockClientInvoiceVOs.get(1));

		List<ClientInvoiceVO> actualClientInvoiceVOs = clientInvoiceService.filterInternalInvoices(mockClientInvoiceVOs);

		assertEquals(expectedClientInvoiceVOs, actualClientInvoiceVOs);
	}
}

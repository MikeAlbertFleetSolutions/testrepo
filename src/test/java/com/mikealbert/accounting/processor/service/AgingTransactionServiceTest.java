package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.AgingTransactionSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.accounting.processor.vo.ReceivableTransactionVO;
import com.mikealbert.constant.accounting.enumeration.AgingPeriodEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a request")
public class AgingTransactionServiceTest extends BaseTest{
	@Resource AgingTransactionService agingTransactionService;

	@MockBean SuiteTalkCacheService suiteTalkCacheService;
	@MockBean AgingTransactionSuiteTalkService agingTransactionSuiteTalkService;

	@BeforeEach
	void up() throws Exception {}
	
	@Test
	@DisplayName("when request is to retrieve a client's aging transactions within a specific aging period/bucket, then the correct parameters are passed to the method that returns client's aging transactions for the given aging period")
	public void testGetAgingWithinPeriod() throws Exception {
		ClientVO mockClientVO = new ClientVO()
		        .setInternalId("nnnnnnnn")
				.setExternalId("1Cnnnnnnnn");

		when(agingTransactionSuiteTalkService.getAging(any(), any(), any())).thenReturn(new ArrayList<>(0));

		agingTransactionService.getAging(mockClientVO.getInternalId(), mockClientVO.getExternalId(), AgingPeriodEnum.AGING_30);

		verify(agingTransactionSuiteTalkService, times(1)).getAging(eq(mockClientVO.getInternalId()), eq(mockClientVO.getExternalId()), eq(AgingPeriodEnum.AGING_30));
	}		

	@Test
	@DisplayName("when request is to group a client's aging transactions by aging peiord, the correct parameters are passed to the method that returns client's aging transactions")
	public void testGroupByAgingPeriod() throws Exception {
		List<ReceivableTransactionVO<?, ?>> clientInvoiceVOs = new ArrayList<>(0);
		clientInvoiceVOs.add(new ClientInvoiceVO());
		clientInvoiceVOs.get(0).setDaysOverdue(AgingPeriodEnum.AGING_90.getMax());

		Map<AgingPeriodEnum, List<ReceivableTransactionVO<?, ?>>> groupedTransactions = agingTransactionService.groupByAgingPeriod(clientInvoiceVOs); 

		assertEquals(1, groupedTransactions.keySet().size());
		assertNotNull(groupedTransactions.get(AgingPeriodEnum.AGING_90));
	}	
}

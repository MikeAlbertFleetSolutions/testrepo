package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.enumeration.BillingReportTypeEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.service.AccountingPeriodService;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;

@DisplayName("Given a request ")
@SpringBootTest
public class BillingReportTransactionSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource BillingReportTransactionSuiteTalkService billingReportTransactionSuiteTalkService;
	@Resource AccountingPeriodService accountingPeriodService;
		
    
    @Disabled("This test can be enabled once we have production trasnactions that include the report data fields")
	@DisplayName("when request is to the billing transactions by extId, accounting periods and maType, then the matching transactions are returned")
	@Test
	public void testGet() throws Exception {
		final String STARTING_ACCOUNTING_PERIOD_NAME = "Sep 2022";
		final String ENDING_ACCOUNTING_PERIOD_NAME = "Sep 2022";
		final BillingReportTypeEnum REPORT_TYPE = BillingReportTypeEnum.MAINTENANCE;

		List<String> accountingPeriodInternalIds =  accountingPeriodService.getByNameRange(STARTING_ACCOUNTING_PERIOD_NAME, ENDING_ACCOUNTING_PERIOD_NAME).stream()
		    .map(a -> a.getInternalId())
			.collect(Collectors.toList());
		
		List<BillingReportTransactionVO> billingReportTransactionVOs = billingReportTransactionSuiteTalkService.get("1C00006429", accountingPeriodInternalIds, REPORT_TYPE);

		assertEquals(2, billingReportTransactionVOs.size());

		billingReportTransactionVOs.stream()
		    .forEach(txn -> {
				if(txn.getType().equals(TransactionTypeEnum.CLIENT_INVOICE)) {
					assertNotNull(txn.getDueDate());
				}

				assertNotNull(txn.getTranInternalId());
				assertNotNull(txn.getTranExternalId());
				assertNotNull(txn.getMaTransactionDate());				
				assertNotNull(txn.getType());	
				assertNotNull(txn.getStatus());		
				assertNotNull(txn.isGrouped());
				assertNotNull(txn.getAccountCode());			
				assertNotNull(txn.getAccountName());			
				assertNotNull(txn.getTranDate());			
				assertNotNull(txn.getReportType());			
				assertNotNull(txn.getDocId());			
				assertNotNull(txn.getTransactionNumber());			
				assertNotNull(txn.getLineId());	
				assertNotNull(txn.getLineNo());
				assertNotNull(txn.getDescription());
				assertNotNull(txn.getInvoiceNote());
				assertNotNull(txn.getAccountingPeriod());	
				assertNotNull(txn.getUnitInternalId());		
				assertNotNull(txn.getUnit());
				assertNotNull(txn.getUnitVin());		
				assertNotNull(txn.getUnitYear());
				assertNotNull(txn.getUnitMake());
				assertNotNull(txn.getUnitModel());
				assertNotNull(txn.getFleetRefNo());				
				assertNotNull(txn.getQty());
				assertNotNull(txn.getBaseNetAmount().getGross());
				assertNotNull(txn.getBaseNetAmount().getTax());
				assertNotNull(txn.getBaseNetAmount().getAmount());


				assertNotEquals(BigDecimal.ZERO, txn.getBaseNetAmount().getTax());
			});
	}

	@DisplayName("when get billling transactions response fails, a SuiteTalkException is raised")
	@Test
	public void testGetFailedResponse() throws Exception {
		assertThrows(SuiteTalkException.class, () -> {
			billingReportTransactionSuiteTalkService.get("0", new ArrayList<>(0), null);
		});
	}


	@Override
	protected String getUnitNo() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

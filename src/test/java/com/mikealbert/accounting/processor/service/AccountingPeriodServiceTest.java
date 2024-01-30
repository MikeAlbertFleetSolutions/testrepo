package com.mikealbert.accounting.processor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.AccountingPeriodSuiteTalkService;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a request")
public class AccountingPeriodServiceTest extends BaseTest{
	@Resource AccountingPeriodService accountingPeriodService;

	@MockBean AccountingPeriodSuiteTalkService accountingPeriodSuiteTalkService;

	@BeforeEach
	void up() throws Exception {}

	@Test
	@DisplayName("when request is to retrieve an accounting period, the correct parameters are passed to the method that returns accounting period")
	public void testGetAging() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "n";

		when(accountingPeriodSuiteTalkService.get(any())).thenReturn(new AccountingPeriodVO());

		accountingPeriodService.get(ACCOUNTING_PERIOD_ID);

		verify(accountingPeriodSuiteTalkService, times(1)).get(eq(ACCOUNTING_PERIOD_ID));
	}	

	@Test
	@DisplayName("when...then...")
	public void testGetByRange() throws Exception {
		final String STARTING_PERIOD_NAME = "Dec-2021";
		final String ENDING_PERIOD_NAME = "May-2022";
		final int EXPECTED_NUMBER_OF_SERVICE_CALLS = 6;
		
		when(accountingPeriodSuiteTalkService.get(any())).thenReturn(new AccountingPeriodVO());

		accountingPeriodService.getByNameRange(STARTING_PERIOD_NAME, ENDING_PERIOD_NAME);

		verify(accountingPeriodSuiteTalkService, times(EXPECTED_NUMBER_OF_SERVICE_CALLS)).getByName(anyString());
	}
	
}

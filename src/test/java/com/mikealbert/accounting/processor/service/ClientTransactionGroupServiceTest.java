package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suiteanalytics.InvoiceSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.TransactionSuiteAnalyticsService;
import com.mikealbert.accounting.processor.dao.ClientTransactionDAO;
import com.mikealbert.accounting.processor.entity.MessageLog;
import com.mikealbert.accounting.processor.enumeration.ClientTransactionGroupFieldEnum;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.util.data.DateUtil;

@SpringBootTest
@DisplayName("Given a request...")
public class ClientTransactionGroupServiceTest extends BaseTest{
	@Resource ClientTransactionGroupService clientTransactionGroupService;

	@MockBean AccountingPeriodService accountingPeriodService;
	@MockBean ClientTransactionDAO clientTransactionDAO;
	@MockBean MessageLogService messageLogService;
	@MockBean InvoiceSuiteAnalyticsService invoiceSuiteAnalyticService;
	@MockBean TransactionSuiteAnalyticsService transactionSuiteAnalyticService;	
	
	@SpyBean JavaMailSender javaMailSender;

	@BeforeEach
	public void up() throws Exception {}
		
	static final String MESSAGE_ID_1 = "accounting-period1|1Caaaaaaaa";
	static final String MESSAGE_ID_2 = "accounting-period1|1Cbbbbbbbb";
	static final String MESSAGE_ID_3 = "accounting-period2|1Caaaaaaaa";	

	@DisplayName("when requesting updates made to invoice groups between a time period, the appropriate calls are made to get the data")
	@Test
	public void testGetUpdates() throws Exception {
		final Date EXPECTED_FROM = new Date();
		final Date EXPECTED_TO = new Date();
		final List<Map<String, Object>> EXPECTED_UPDATES = new ArrayList<>(0);

		when(transactionSuiteAnalyticService.findUpdatedClientTransactionGroups(any(), any())).thenReturn(EXPECTED_UPDATES);

		List<ClientTransactionGroupVO> actualResult =  clientTransactionGroupService.getUpdates(EXPECTED_FROM, EXPECTED_TO);

		verify(transactionSuiteAnalyticService, times(1)).findUpdatedClientTransactionGroups(eq(EXPECTED_FROM), eq(EXPECTED_TO));

		assertEquals(EXPECTED_UPDATES, actualResult);
	}

	@Test
	@DisplayName("when an updated transaction group, then the appropriate calls are made to process it")	
	public void testProcess() throws Exception {
		ClientTransactionGroupVO mockClientTransactionGroupVO = new ClientTransactionGroupVO()
		        .setAccountingPeriodId("0")
				.setAccountingPeriodDate(new Date())
				.setClientInternalId("-1")
				.setClientExternalId("-1C00000000")
				.setGroupNumber("-1000");

		doNothing().when(clientTransactionDAO).processTransactionGroup(any());

		clientTransactionGroupService.process(mockClientTransactionGroupVO);

		verify(clientTransactionDAO, times(1)).processTransactionGroup(eq(mockClientTransactionGroupVO));

	}

	@Disabled("This test is disabled because QA would like to test mulitple print run headers without calling dist complete")
	@Test
	@DisplayName("when processing is complete, then the appropriate calls are made to further process the complete reequest")	
	public void testProcessComplete() throws Exception {
		doNothing().when(clientTransactionDAO).processTransactionGroupComplete();

		clientTransactionGroupService.complete();

		verify(clientTransactionDAO, times(1)).processTransactionGroupComplete();

	}

	@DisplayName("when requesting emailing of complete action...then the email is sent with the correct content")
	@Test
	public void testEmailComplete() throws Exception {
		final List<String> ACCOUNTING_PERIODS = Arrays.asList(new String[]{"0", "1"});
		final Date ACCOUNTING_PERIOD_END_DATE = new Date();		
		final String SUBJECT = "Group Invoice Distribution Completed";
		final String FROM = super.emailFrom;
		final String TO = super.emailTo;
		final String TEXT = String.format("Distribution has been completed on all invoices for the period of %s.", DateUtil.convertToString(ACCOUNTING_PERIOD_END_DATE, DateUtil.PATTERN_YEAR_MONTH));

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(FROM);
		simpleMailMessage.setSubject(SUBJECT);
		simpleMailMessage.setTo(TO);
		simpleMailMessage.setText(TEXT);
		
		when(accountingPeriodService.get(any())).thenReturn(new AccountingPeriodVO().setEnd(ACCOUNTING_PERIOD_END_DATE));
		doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
		
		clientTransactionGroupService.emailComplete(ACCOUNTING_PERIODS);

		verify(javaMailSender, times(2)).send(eq(simpleMailMessage));
	}		

	@DisplayName("when all message logs have an end date, then the internal transaction grouping process hass been completed")
	@Test
	public void testHasInternalGroupingBeenCompleted() throws Exception {
		List<MessageLog> mockMessageLogs = new ArrayList<>(0);
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_1));
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_2));
		mockMessageLogs.get(0).setEndDate(new Date());		
		mockMessageLogs.get(1).setEndDate(new Date());
				
		when(messageLogService.find(any(), any(), any())).thenReturn(mockMessageLogs);
		when(messageLogService.findWithPartialMessageId(any(), any())).thenReturn(mockMessageLogs);

		assertTrue(clientTransactionGroupService.hasInternalGroupingBeenCompleted(new Date(), new Date()));
	}
	

	@DisplayName("when at least one message log has no end date, then the internal transaction grouping has not be completed")
	@Test
	public void testHasInternalGroupingBeenCompletedWhenOneGroupPending() throws Exception {
		List<MessageLog> mockMessageLogs = new ArrayList<>(0);
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_1));
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_2));
		mockMessageLogs.get(0).setEndDate(new Date());		
				
		when(messageLogService.find(any(), any(), any())).thenReturn(mockMessageLogs);
		when(messageLogService.findWithPartialMessageId(any(), any())).thenReturn(mockMessageLogs);

		assertFalse(clientTransactionGroupService.hasInternalGroupingBeenCompleted(new Date(), new Date()));
	}	

	@DisplayName("when requesting the accounting period(s) of completed internal groups within a time frame, then a distinct list of accounting periods is returned")
	@Test
	public void testCompletedInternalGroupAccountingPeriods() throws Exception {
		List<MessageLog> mockMessageLogs = new ArrayList<>(0);
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_1));
		mockMessageLogs.add(new MessageLog(EventEnum.GROUP_TRANSACTION.name(), MESSAGE_ID_2));
		mockMessageLogs.get(0).setEndDate(new Date());		
		mockMessageLogs.get(1).setEndDate(new Date());
				
		when(messageLogService.find(any(), any(), any())).thenReturn(mockMessageLogs);
		//when(messageLogService.findWithPartialMessageId(any(), any())).thenReturn(mockMessageLogs);

		List<String> accountingPeriods =  clientTransactionGroupService.completedInternalGroupAccountingPeriods(new Date(), new Date());

		assertEquals(1, accountingPeriods.size());
		//assertTrue(clientTransactionGroupService.hasInternalGroupingBeenCompleted(new Date(), new Date()));
	}

	@DisplayName("when requesting a message id, the message id is formatted from the invoice group and returned")
	@Test 
	public void testFormatMessageId() {
		final String EXPECTED_MESSAGE_ID = "123|1C00000000";

		ClientTransactionGroupVO mockClientTransactionGroupVO = new ClientTransactionGroupVO()
		        .setAccountingPeriodId("123")
				.setClientAccountCode("00000000")
				.setClientExternalId("1C00000000")
				.setGroupNumber("321");
				
		String actualMessageId = clientTransactionGroupService.formatMessageId(mockClientTransactionGroupVO);

		assertEquals(EXPECTED_MESSAGE_ID, actualMessageId);
		
	}

	@DisplayName("when requesting message ids for multiple invoice groups, the message ids are formatted from the invoice groups and returned")
	@Test 
	public void testFormatMessageIds() {
		final String EXPECTED_MESSAGE_ID_1 = "123|1C00000000";
		final String EXPECTED_MESSAGE_ID_2 = "456|1C00000000";

		ClientTransactionGroupVO mockClientTransactionGroupVO1 = new ClientTransactionGroupVO()
		        .setAccountingPeriodId("123")
				.setClientAccountCode("00000000")
				.setClientExternalId("1C00000000")
				.setGroupNumber("321");

		ClientTransactionGroupVO mockClientTransactionGroupVO2 = new ClientTransactionGroupVO()
		        .setAccountingPeriodId("456")
				.setClientAccountCode("00000000")
				.setClientExternalId("1C00000000")
				.setGroupNumber("654");				

		List<ClientTransactionGroupVO> mockClientTransactionGroupVOs = new ArrayList<>(0);
		mockClientTransactionGroupVOs.add(mockClientTransactionGroupVO1);
		mockClientTransactionGroupVOs.add(mockClientTransactionGroupVO2);		

		List<String> actualMessageIds = clientTransactionGroupService.formatMessageIds(mockClientTransactionGroupVOs);

		actualMessageIds = actualMessageIds.stream()
		    .filter(actualMessageId -> actualMessageId.equals(EXPECTED_MESSAGE_ID_1) || actualMessageId.equals(EXPECTED_MESSAGE_ID_2))
			.collect(Collectors.toList());

		assertEquals(2, actualMessageIds.size());
		
	}	
	
	@DisplayName("when converting a list of transaction group maps to transaction group VOs, then correct ClientTransactionGroupVOs are returned")
	@Test
	public void testConvertToTransactionGroupVOs() {
		final String ACCOUNTING_PERIOD_ID = "0";
		final Date ENDING = new Date();
		final String CLIENT_INTERNAL_ID = "-1";
		final String CLIENT_EXTERNAL_ID = "-1Cnnnnnnnn";
		final String GROUP_NUMBER = "-2";

		List<Map<String, Object>> mockClientTransactionGroupVOMaps = new ArrayList<>(0);
		mockClientTransactionGroupVOMaps.add(new HashMap<>());
		mockClientTransactionGroupVOMaps.get(0).put("accounting_period_id", ACCOUNTING_PERIOD_ID);
		mockClientTransactionGroupVOMaps.get(0).put("ending", ENDING);
		mockClientTransactionGroupVOMaps.get(0).put("customer_internal_id", CLIENT_INTERNAL_ID);
		mockClientTransactionGroupVOMaps.get(0).put("customer_external_id", CLIENT_EXTERNAL_ID);
		mockClientTransactionGroupVOMaps.get(0).put("group_number", GROUP_NUMBER);

		List<ClientTransactionGroupVO> mockClientTransactionGroupVOs = new ArrayList<>(0);
		mockClientTransactionGroupVOs.add(new ClientTransactionGroupVO());
		mockClientTransactionGroupVOs.get(0).setAccountingPeriodId(ACCOUNTING_PERIOD_ID);
		mockClientTransactionGroupVOs.get(0).setAccountingPeriodDate(ENDING);
		mockClientTransactionGroupVOs.get(0).setClientInternalId(CLIENT_INTERNAL_ID);
		mockClientTransactionGroupVOs.get(0).setClientExternalId(CLIENT_EXTERNAL_ID);
		mockClientTransactionGroupVOs.get(0).setGroupNumber(GROUP_NUMBER.toString());

		List<ClientTransactionGroupVO> actualClientTransactionGroupVOs = clientTransactionGroupService.convertToTransactionGroupVOs(mockClientTransactionGroupVOMaps);

		assertEquals(mockClientTransactionGroupVOs, actualClientTransactionGroupVOs);
	}

	@DisplayName("when converting a transaction group map to transaction group VO, then correct ClientTransactionGroupVOs are returned")
	@Test
	public void testConvertToTransactionGroupVO() {
		final String ACCOUNTING_PERIOD_ID = "0";
		final Date ENDING = new Date();
		final String CLIENT_INTERNAL_ID = "-1";
		final String CLIENT_EXTERNAL_ID = "-1Cnnnnnnnn";
		final String GROUP_NUMBER = "-2";

		Map<String, Object> mockClientTransactionGroupVOMap = new HashMap<>();
		mockClientTransactionGroupVOMap.put("accounting_period_id", ACCOUNTING_PERIOD_ID);
		mockClientTransactionGroupVOMap.put("ending", ENDING);
		mockClientTransactionGroupVOMap.put("customer_internal_id", CLIENT_INTERNAL_ID);
		mockClientTransactionGroupVOMap.put("customer_external_id", CLIENT_EXTERNAL_ID);
		mockClientTransactionGroupVOMap.put("group_number", GROUP_NUMBER);

		ClientTransactionGroupVO expectedClientTransactionGroupVO = new ClientTransactionGroupVO()
				.setAccountingPeriodId(ACCOUNTING_PERIOD_ID)
				.setAccountingPeriodDate(ENDING)
				.setClientInternalId(CLIENT_INTERNAL_ID)
				.setClientExternalId(CLIENT_EXTERNAL_ID)
				.setGroupNumber(GROUP_NUMBER.toString());

		ClientTransactionGroupVO actualClientTransactionGroupVO = clientTransactionGroupService.convertToTransactionGroupVO(mockClientTransactionGroupVOMap);

		assertEquals(expectedClientTransactionGroupVO, actualClientTransactionGroupVO);
	}
	
	@DisplayName("when requesting all groupable transactions for a client within an accounting period, then all matching transactions are returned")
	@Test
	public void testFindGroupTransactions() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "0";
		final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";

		List<Map<String, Object>> mockResults = new ArrayList<>(0);
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());
		mockResults.get(0).put("group_number", "00");
		mockResults.get(1).put("group_number", null);	

		when(transactionSuiteAnalyticService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(anyString(), anyString())).thenReturn(mockResults);

		List<Map<String, Object>> actualResults = clientTransactionGroupService.findGroupTransactions(ACCOUNTING_PERIOD_ID, CLIENT_EXTERNAL_ID);
		
		verify(transactionSuiteAnalyticService, times(1)).findGroupableTransactionsByAccountingPeriodAndClientExternalId(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID));

		assertEquals(2, actualResults.size());
	}

	@DisplayName("when requesting groupable transactions that have not been grouped, then those ungrouped transactions are returned")
	@Test
	public void testFindGroupTransactionsThatHaveNotBeenGrouped() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "0";
		final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";

		List<Map<String, Object>> mockResults = new ArrayList<>(0);
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());		
		mockResults.get(0).put("group_number", "00");
		mockResults.get(1).put("group_number", null);
		mockResults.get(2).put("group_number", "000");		

		when(transactionSuiteAnalyticService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(anyString(), anyString())).thenReturn(mockResults);

		List<Map<String, Object>> actualResults = clientTransactionGroupService.findGroupTransactions(ACCOUNTING_PERIOD_ID, CLIENT_EXTERNAL_ID, false);
		
		verify(transactionSuiteAnalyticService, times(1)).findGroupableTransactionsByAccountingPeriodAndClientExternalId(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID));

		assertEquals(1, actualResults.size());
	}

	@DisplayName("when requesting groupable transactions that have been grouped, then those grouped transactions are returned")
	@Test
	public void testFindGroupTransactionsThatHaveBeenGrouped() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "0";
		final String CLIENT_EXTERNAL_ID = "1Cnnnnnnnn";

		List<Map<String, Object>> mockResults = new ArrayList<>(0);
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());		
		mockResults.get(0).put("group_number", null);
		mockResults.get(1).put("group_number", "00");
		mockResults.get(2).put("group_number", null);		

		when(transactionSuiteAnalyticService.findGroupableTransactionsByAccountingPeriodAndClientExternalId(anyString(), anyString())).thenReturn(mockResults);

		List<Map<String, Object>> actualResults = clientTransactionGroupService.findGroupTransactions(ACCOUNTING_PERIOD_ID, CLIENT_EXTERNAL_ID, true);
		
		verify(transactionSuiteAnalyticService, times(1)).findGroupableTransactionsByAccountingPeriodAndClientExternalId(eq(ACCOUNTING_PERIOD_ID), eq(CLIENT_EXTERNAL_ID));

		assertEquals(1, actualResults.size());
	}	

	@DisplayName("when requesting all client transaction groups by accounting period, then the client transaction groups in the accounting period are returned")
	@Test
	public void testFindAllClientTransactiongGroupsByAccountingPeriod() throws Exception {
		final String ACCOUNTING_PERIOD_ID = "0";

		List<String> accountingPeriodIds = Arrays.asList(ACCOUNTING_PERIOD_ID);

		List<Map<String, Object>> mockResults = new ArrayList<>(0);
		mockResults.add(new HashMap<>());
		mockResults.add(new HashMap<>());
		mockResults.get(0).put("group_number", "00");
		mockResults.get(1).put("group_number", null);

		when(transactionSuiteAnalyticService.findAllClientTransactionGroupsByAccountingPeriod(any())).thenReturn(mockResults);

		List<ClientTransactionGroupVO> actualResults = clientTransactionGroupService.findAllClientTransactiongGroupsByAccountingPeriod(accountingPeriodIds);
		
		verify(transactionSuiteAnalyticService, times(1)).findAllClientTransactionGroupsByAccountingPeriod(eq(accountingPeriodIds));

		assertEquals(2, actualResults.size());
	}
	
	@DisplayName("when requesting a list of distinct accounting period ids from a list of client transaction groups, then the list of distinct accounting period ids are returned")
	@Test
	public void testDistinctAccountingPeriodIds() {
		List<Map<String, Object>> clientTransactionGroupMaps = new ArrayList<>(0);
		clientTransactionGroupMaps.add(new HashMap<>());
		clientTransactionGroupMaps.add(new HashMap<>());
		clientTransactionGroupMaps.add(new HashMap<>());		
		clientTransactionGroupMaps.get(0).put(ClientTransactionGroupFieldEnum.ACCOUNTING_PERIOD_ID.getScriptId(), "0");
		clientTransactionGroupMaps.get(1).put(ClientTransactionGroupFieldEnum.ACCOUNTING_PERIOD_ID.getScriptId(), "1");
		clientTransactionGroupMaps.get(2).put(ClientTransactionGroupFieldEnum.ACCOUNTING_PERIOD_ID.getScriptId(), "0");

		List<String> actualAccountingPeriodIds = clientTransactionGroupService.distinctAccountingPeriodId(
			clientTransactionGroupService.convertToTransactionGroupVOs(clientTransactionGroupMaps));

		assertEquals(2, actualAccountingPeriodIds.size());
	}
}

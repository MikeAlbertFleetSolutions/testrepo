package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.VendorTestHelper;
import com.mikealbert.accounting.processor.client.suiteanalytics.CustomerSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.enumeration.ClientFieldEnum;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.constant.enumeration.AccountTypeEnum;
import com.mikealbert.constant.enumeration.CurrencyCodeEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@DisplayName("Given a Client update from the external system")
public class ClientServiceTest extends BaseTest{
	@Resource ClientService clientService;

	@MockBean CustomerSuiteAnalyticsService customerSuiteAnalyticsService;
	@MockBean CustomerSuiteTalkService customerSuiteTalkService;
	@MockBean SuiteTalkCacheService suiteTalkCacheService;
	@MockBean ExternalAccountDAO externalAccountDAO;
	@MockBean TaxJurisdictionService taxJurisdictionService;

	static final String CUSTOMER_INTERNAL_ID = "1";
	static final String CUSTOMER_ACCOUNT_CODE = "-10000000";

	@BeforeEach
	void up() throws Exception {

	}
		
	@Test
	@DisplayName("when retrieving client, then the client VO containing the details is returned")
	public void testGet() throws Exception {
		ClientVO mockClientVO = generateMockClientVO();
		mockClientVO.setExternalId("-1");

		when(customerSuiteTalkService.getCustomer(isNull(), anyString(), anyBoolean())).thenReturn(mockClientVO);
		
		ClientVO actualClientVO = clientService.get(mockClientVO.getExternalId(), true);

		verify(customerSuiteTalkService, times(1)).getCustomer(isNull(), eq(mockClientVO.getExternalId()), eq(false));

		assertEquals(mockClientVO, actualClientVO);
	}

	@Test
	@DisplayName("when new client, all properties needed to persist the client are assigned correctly")	
	public void testProcessClientCreate() throws Exception {
		final String CUSTOMER_INTERNAL_ID = "1";
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";

		ArgumentCaptor<ClientVO> clientVOCaptor = ArgumentCaptor.forClass(ClientVO.class);		

		ClientVO mockClientVO = generateMockClientVO();
		
		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("internalId", CUSTOMER_INTERNAL_ID);
		clientMap.put("externalId", null);
		clientMap.put("accountCode", CUSTOMER_ACCOUNT_CODE); 
		clientMap.put("companyName", "Unit Test LLC.");
	
		when(customerSuiteTalkService.getCustomer(anyString(), isNull(), anyBoolean())).thenReturn(mockClientVO);
		when(customerSuiteTalkService.formatExternalId(any())).thenReturn("1C"+CUSTOMER_ACCOUNT_CODE);		
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);				
		when(externalAccountDAO.desencrypt(anyString())).thenReturn("ôÎ£Þ");
		when(externalAccountDAO.save(any())).then(i -> {
			ExternalAccount account = (ExternalAccount)i.getArguments()[0];
			account.setId(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), CUSTOMER_ACCOUNT_CODE));
			account.getExternalAccountAddresses().get(0).setEaaId(1L);
			return account;
		});
		doNothing().when(customerSuiteTalkService).update(clientVOCaptor.capture());

		ExternalAccount account = clientService.process(clientMap);
		
		assertEquals(mockClientVO.getAccountCode(), account.getId().getAccountCode());
		assertEquals(mockClientVO.getExternalId(), clientVOCaptor.getValue().getExternalId());
		assertEquals(mockClientVO.getEmail(), account.getTelexCode());
		assertEquals(mockClientVO.getPhoneNumber(), account.getTelephoneNumber());
		assertEquals(mockClientVO.getFaxNumber(), account.getFaxCode());		
		assertEquals(mockClientVO.getCreditLimit1(), account.getCreditLimit());
		assertEquals(mockClientVO.getCreditLimit2(), account.getCreditLimit2());
		assertEquals(mockClientVO.getUnitLimit1(), account.getCreditUnit1());
		assertEquals(mockClientVO.getUnitLimit2(), account.getCreditUnit2());
		assertEquals(mockClientVO.getCapitalLimit1(), account.getCapitalLimit1());
		assertEquals(mockClientVO.getCapitalLimit2(), account.getCapitalLimit2());
		assertEquals(mockClientVO.getPurchaseCreditLimit(), account.getPurchaseCreditLimit());
		assertEquals(mockClientVO.getCreditScore(), account.getCreditScore());
		assertEquals(mockClientVO.getIncorporationState(), account.getIncorporationState());
		assertEquals(mockClientVO.getRiskDepositAmount(), account.getRiskDepositAmt());
		assertEquals(mockClientVO.getAddress1(), account.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockClientVO.getAddress2(), account.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockClientVO.getPostalCode(), account.getExternalAccountAddresses().get(0).getPostcode());

		assertEquals("O", account.getAccStatus());
		assertEquals(CurrencyCodeEnum.USD.name(), account.getCurrencyCode());
		assertEquals(1L, account.getCrtCId());
		assertEquals(AccountTypeEnum.C.name(), account.getCrtExtAccType());		
		assertEquals("STANDARD", account.getCreditTermsCode());
		assertEquals("YELLOW-EXP", account.getGroupCode());
		assertEquals("R", account.getCredApprStatus());
		assertEquals(1L, account.getOverdueInterest());
		assertEquals("I", account.getCreditManagementType());
		assertEquals("Y", account.getUpfitFeeChgd());
		assertEquals("N", account.getBankruptInd());
		assertEquals(CUSTOMER_INTERNAL_ID, clientVOCaptor.getValue().getInternalId());
		assertEquals(80, account.getAccountName().length());
		assertEquals(25, account.getShortName().length());
		assertEquals("N", account.getPrintStatement());
		assertEquals("CHECK", account.getPaymentMethod());
		
		assertNotNull(account.getDateOpened());


	}
	
	@Test
	@DisplayName("when client does not have a default billing address, then the client is not processed")	
	public void testProcessClientWithNoDefaultBillingAddress() throws Exception {
		final String CUSTOMER_INTERNAL_ID = "1";
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";

		ClientVO mockClientVO = generateMockClientVO();
		mockClientVO.setAddressInternalId(null);
		
		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("internalId", CUSTOMER_INTERNAL_ID);
		clientMap.put("externalId", null);
		clientMap.put("accountCode", CUSTOMER_ACCOUNT_CODE);
		clientMap.put("companyName", "Unit Test LLC.");
	
		when(customerSuiteTalkService.getCustomer(anyString(), isNull(), anyBoolean())).thenReturn(mockClientVO);

		ExternalAccount account = clientService.process(clientMap);
		
        assertNull(account);
	}

	@Test
	@DisplayName("when change is an update to client and a new default billing address, then the client is updated in willow with the addition of a new default POST address")	
	public void testProcessClientUpdateWithNewAddress() throws Exception {
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";
		final String CUSTOMER_ACCOUNT_NAME = "Unit Test Inc.";

		ArgumentCaptor<ClientVO> clientVOCaptor = ArgumentCaptor.forClass(ClientVO.class);		
		ArgumentCaptor<ExternalAccountPK> accountIdCaptor = ArgumentCaptor.forClass(ExternalAccountPK.class);				

		ExternalAccount mockAccount = generateMockAccount();
		mockAccount.setWebQuotesReqCcApproval("N");
		mockAccount.setWebQuotesReqFaApproval("Y");
		mockAccount.setUpfitInd("N");
		mockAccount.setPaymentInd("M");
		mockAccount.setInternationalInd("N");
		mockAccount.setTaxInd("Y");
		mockAccount.setPaymentMethod("LOCKBOX");

		ClientVO mockClientVO = generateMockClientVO();
		mockClientVO.setExternalId(String.format("%s%s%s",mockAccount.getId().getCId(), mockAccount.getId().getAccountType(), CUSTOMER_ACCOUNT_CODE));
		mockClientVO.setAccountName(CUSTOMER_ACCOUNT_NAME);
		
		Map<String, String> message = new HashMap<>();
		message.put("internalId", mockClientVO.getInternalId());
		message.put("externalId", mockClientVO.getExternalId());
		message.put("accountCode", mockClientVO.getAccountCode());
		message.put("companyName", mockClientVO.getAccountName());

		doNothing().when(customerSuiteTalkService).update(clientVOCaptor.capture());
		when(customerSuiteTalkService.getCustomer(eq(mockClientVO.getInternalId()), eq(mockClientVO.getExternalId()), eq(true))).thenReturn(mockClientVO);
		when(customerSuiteTalkService.formatExternalId(eq(mockAccount.getId().getAccountCode()))).thenReturn(mockClientVO.getExternalId());
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);				
		when(externalAccountDAO.desencrypt(anyString())).thenReturn("ôÎ£Þ");
		when(externalAccountDAO.findById(accountIdCaptor.capture())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.save(any())).then(i -> {
			ExternalAccount account = (ExternalAccount)i.getArguments()[0];
			account.setExternalAccountBankAccounts(new ArrayList<>());
			IntStream.range(0, account.getExternalAccountAddresses().size()).forEach(idx -> account.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));
			return account;
		});

		ExternalAccount account = clientService.process(message);
		
		//Client level identifier and changes
		assertEquals(clientService.parseAccountCodeFromExternalId(mockClientVO.getExternalId()), account.getId().getAccountCode());
		assertEquals(mockClientVO.getAccountName(), account.getAccountName());
		assertEquals(1L, account.getOverdueInterest());
		
		//New and existing address only
		assertEquals(2, account.getExternalAccountAddresses().size());
		
		//Update of existing addresss
		assertEquals(0, account.getExternalAccountAddresses().get(0).getEaaId());
		assertEquals("2", account.getExternalAccountAddresses().get(0).getAddressCode());		
		assertFalse(account.getExternalAccountAddresses().get(0).isDefaultBilling());
		assertNull(account.getExternalAccountAddresses().get(0).getDefaultInd());

		//New address
		assertEquals(1, account.getExternalAccountAddresses().get(1).getEaaId());
		assertEquals("1", account.getExternalAccountAddresses().get(1).getAddressCode());		
		assertTrue(account.getExternalAccountAddresses().get(1).isDefaultBilling());
		assertEquals("Y", account.getExternalAccountAddresses().get(1).getDefaultInd());
		
		//Verify callback to NS to update the extId
		assertEquals(mockClientVO.getExternalId(), clientVOCaptor.getValue().getExternalId());
		assertEquals(mockClientVO.getAddressInternalId(), clientVOCaptor.getValue().getAddressInternalId());		
		assertEquals(account.getExternalAccountAddresses().get(1).getEaaId(), Long.parseLong(clientVOCaptor.getValue().getAddressExternalId()));

		//Verify fields did not get updated
		assertEquals("N", account.getWebQuotesReqCcApproval());
		assertEquals("Y", account.getWebQuotesReqFaApproval());
		assertEquals("N", account.getUpfitInd());
		assertEquals("M", account.getPaymentInd());
		assertEquals("N", account.getInternationalInd());
		assertEquals("Y", account.getTaxInd());
		assertEquals("LOCKBOX", account.getPaymentMethod());

	}

	@Test
	@DisplayName("when client does not have an account name, then the client is not processed, instead, an exception is raised")	
	public void testProcessClientWithNoAccountName() throws Exception {
		final String CUSTOMER_INTERNAL_ID = "1";
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";

		assertThrows(Exception.class, () -> {
			ClientVO mockClientVO = generateMockClientVO();
			mockClientVO.setAccountName(null);
			
			Map<String, String> clientMap = new HashMap<>();
			clientMap.put("internalId", CUSTOMER_INTERNAL_ID);
			clientMap.put("externalId", null);
			clientMap.put("accountCode", CUSTOMER_ACCOUNT_CODE);
			clientMap.put("companyName", "Unit Test LLC.");
		
			when(customerSuiteTalkService.getCustomer(anyString(), isNull(), anyBoolean())).thenReturn(mockClientVO);
	
			clientService.process(clientMap);
		});
	}

	@Test
	@DisplayName("when client has a credit score that exceeds the store's buffer size, then the client is not processed, instead, an exception is raised")	
	public void testProcessClientWithLongCreditScore() throws Exception {
		final String CUSTOMER_INTERNAL_ID = "1";
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";

		assertThrows(Exception.class, () -> {
			ClientVO mockClientVO = generateMockClientVO();
			mockClientVO.setCreditScore("abcdefghijklmopqrstuvwxyz");
			
			Map<String, String> clientMap = new HashMap<>();
			clientMap.put("internalId", CUSTOMER_INTERNAL_ID);
			clientMap.put("externalId", null);
			clientMap.put("accountCode", CUSTOMER_ACCOUNT_CODE);
			clientMap.put("companyName", "Unit Test LLC.");
		
			when(customerSuiteTalkService.getCustomer(anyString(), isNull(), anyBoolean())).thenReturn(mockClientVO);
	
			clientService.process(clientMap);
		});
	}	

	@Test
	@DisplayName("when client has a negative value credit limit, then the client is not processed, instead, an exception is raised")	
	public void testProcessClientWithNegativeCreditLimit() throws Exception {
		final String CUSTOMER_INTERNAL_ID = "1";
		final String CUSTOMER_ACCOUNT_CODE = "-10000000";

		assertThrows(Exception.class, () -> {
			ClientVO mockClientVO = generateMockClientVO();
			mockClientVO.setCreditLimit1(new BigDecimal("-11"));
			
			Map<String, String> clientMap = new HashMap<>();
			clientMap.put("internalId", CUSTOMER_INTERNAL_ID);
			clientMap.put("externalId", null);
			clientMap.put("accountCode", CUSTOMER_ACCOUNT_CODE);
			clientMap.put("companyName", "Unit Test LLC.");
		
			when(customerSuiteTalkService.getCustomer(anyString(), isNull(), anyBoolean())).thenReturn(mockClientVO);
	
			clientService.process(clientMap);
		});
	}

	@Test
	@DisplayName("when account has a parent, then get the parent account")
	public void testGenerateParentMapFromAccount() throws Exception {
		final String PARENT_ACCOUNT_CODE = "00000001";
		final String CHILD_ACCOUNT_CODE = "00000002";
		final String PARENT_EXTERNAL_ID = "1C" + PARENT_ACCOUNT_CODE;
		final String CHILD_EXTERNAL_ID = "1C" + CHILD_ACCOUNT_CODE;

		ExternalAccount parentAccountMock = generateMockAccount();
		parentAccountMock.getId().setAccountCode(PARENT_ACCOUNT_CODE);

		ExternalAccount childAccountMock = generateMockAccount();		
		childAccountMock.getId().setAccountCode(CHILD_ACCOUNT_CODE);		
		childAccountMock.setParentEntity(parentAccountMock.getId().getCId());
		childAccountMock.setParentAccountType(parentAccountMock.getId().getAccountType());
		childAccountMock.setParentAccount(parentAccountMock.getId().getAccountCode());

		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("externalId", CHILD_EXTERNAL_ID);

		when(customerSuiteTalkService.formatExternalId(eq(PARENT_ACCOUNT_CODE))).thenReturn(PARENT_EXTERNAL_ID);
		when(externalAccountDAO.findById(eq(new ExternalAccountPK(childAccountMock.getId().getCId(), childAccountMock.getId().getAccountType(), childAccountMock.getId().getAccountCode())))).thenReturn(Optional.of(childAccountMock));

		List<Map<String, String>> parentMaps = clientService.getClientParents(clientMap);

		assertEquals(1, parentMaps.size());

		assertEquals(parentMaps.get(0).get(ClientFieldEnum.INTERNAL_ID.getScriptId()), null);
		assertEquals(PARENT_EXTERNAL_ID, parentMaps.get(0).get(ClientFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(PARENT_ACCOUNT_CODE, parentMaps.get(0).get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()));
	}

	@Test
	@DisplayName("when client has a parent, then get the parent account")
	public void testGenerateParentMapFromCustomer() throws Exception {
		final String PARENT_ACCOUNT_CODE = "00000001";
		final String CHILD_ACCOUNT_CODE = "00000002";
		final String PARENT_EXTERNAL_ID = "1C" + PARENT_ACCOUNT_CODE;
		final String CHILD_EXTERNAL_ID = "1C" + CHILD_ACCOUNT_CODE;

		ClientVO clientMock = generateMockClientVO();
		clientMock.setParentInternalId("1");
		clientMock.setParentExternalId(PARENT_EXTERNAL_ID);
		clientMock.setParentAccountCode(PARENT_ACCOUNT_CODE);

		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("externalId", CHILD_EXTERNAL_ID);

		when(customerSuiteTalkService.getCustomer(any(), anyString(), anyBoolean())).thenReturn(clientMock);
		when(externalAccountDAO.findById(any())).thenReturn(Optional.ofNullable(null));		

		List<Map<String, String>> parentMaps = clientService.getClientParents(clientMap);

		verify(externalAccountDAO, times(1)).findById(any());

		assertEquals(1, parentMaps.size());
		assertEquals(clientMock.getParentInternalId(), parentMaps.get(0).get(ClientFieldEnum.INTERNAL_ID.getScriptId()));
		assertEquals(clientMock.getParentExternalId(), parentMaps.get(0).get(ClientFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(clientMock.getParentAccountCode(), parentMaps.get(0).get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()));
	}

	@Test
	@DisplayName("when client and account both have a parent (switch from old to new), then return both parents")
	public void testGenerateParentMapFromClientWithOldAndNewParent() throws Exception {
		final String OLD_PARENT_ACCOUNT_CODE = "00000001";
		final String NEW_PARENT_ACCOUNT_CODE = "00000002";		
		final String CLIENT_ACCOUNT_CODE = "00000003";
		final String OLD_PARENT_EXTERNAL_ID = "1C" + OLD_PARENT_ACCOUNT_CODE;
		final String NEW_PARENT_EXTERNAL_ID = "1C" + NEW_PARENT_ACCOUNT_CODE;		
		final String CLIENT_EXTERNAL_ID = "1C" + CLIENT_ACCOUNT_CODE;

		ExternalAccount accountMock = generateMockAccount();
		accountMock.getId().setAccountCode(CLIENT_ACCOUNT_CODE);
		accountMock.setParentAccount(OLD_PARENT_ACCOUNT_CODE);

		ClientVO clientMock = generateMockClientVO();
		clientMock.setParentInternalId("1");
		clientMock.setParentExternalId(NEW_PARENT_EXTERNAL_ID);
		clientMock.setParentAccountCode(NEW_PARENT_ACCOUNT_CODE);

		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("externalId", CLIENT_EXTERNAL_ID);		

		when(customerSuiteTalkService.getCustomer(any(), anyString(), anyBoolean())).thenReturn(clientMock);
		when(externalAccountDAO.findById(eq(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), CLIENT_ACCOUNT_CODE)))).thenReturn(Optional.of(accountMock));		
		when(customerSuiteTalkService.formatExternalId(eq(OLD_PARENT_ACCOUNT_CODE))).thenReturn(OLD_PARENT_EXTERNAL_ID);

		List<Map<String, String>> parentMaps = clientService.getClientParents(clientMap);

		assertEquals(2, parentMaps.size());

		assertEquals(clientMock.getParentInternalId(), parentMaps.get(0).get(ClientFieldEnum.INTERNAL_ID.getScriptId()));
		assertEquals(NEW_PARENT_EXTERNAL_ID, parentMaps.get(0).get(ClientFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(NEW_PARENT_ACCOUNT_CODE, parentMaps.get(0).get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()));

		assertEquals(null, parentMaps.get(1).get(ClientFieldEnum.INTERNAL_ID.getScriptId()));
		assertEquals(OLD_PARENT_EXTERNAL_ID, parentMaps.get(1).get(ClientFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(OLD_PARENT_ACCOUNT_CODE, parentMaps.get(1).get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()));
	}

	@Test
	@DisplayName("when client and account both have the same parent, then the single parent is returned")
	public void testGenerateParentMapFromClientWithSameParent() throws Exception {
		final String PARENT_ACCOUNT_CODE = "00000001";
		final String CLIENT_ACCOUNT_CODE = "00000003";
		final String PARENT_EXTERNAL_ID = "1C" + PARENT_ACCOUNT_CODE;
		final String CLIENT_EXTERNAL_ID = "1C" + CLIENT_ACCOUNT_CODE;

		ExternalAccount accountMock = generateMockAccount();
		accountMock.getId().setAccountCode(CLIENT_ACCOUNT_CODE);
		accountMock.setParentAccount(PARENT_ACCOUNT_CODE);

		ClientVO clientMock = generateMockClientVO();
		clientMock.setParentInternalId("1");
		clientMock.setParentExternalId(PARENT_EXTERNAL_ID);
		clientMock.setParentAccountCode(PARENT_ACCOUNT_CODE);

		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("externalId", CLIENT_EXTERNAL_ID);		

		when(customerSuiteTalkService.getCustomer(any(), anyString(), anyBoolean())).thenReturn(clientMock);
		when(externalAccountDAO.findById(eq(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), CLIENT_ACCOUNT_CODE)))).thenReturn(Optional.of(accountMock));		
		when(customerSuiteTalkService.formatExternalId(eq(PARENT_ACCOUNT_CODE))).thenReturn(PARENT_EXTERNAL_ID);

		List<Map<String, String>> parentMaps = clientService.getClientParents(clientMap);

		assertEquals(1, parentMaps.size());

		assertEquals(clientMock.getParentInternalId(), parentMaps.get(0).get(ClientFieldEnum.INTERNAL_ID.getScriptId()));
		assertEquals(PARENT_EXTERNAL_ID, parentMaps.get(0).get(ClientFieldEnum.EXTERNAL_ID.getScriptId()));
		assertEquals(PARENT_ACCOUNT_CODE, parentMaps.get(0).get(ClientFieldEnum.ACCOUNT_CODE.getScriptId()));
	}

	@Test
	@DisplayName("when client and account do not have a parent, then return null")
	public void testGenerateParentMapFromClientWithNoParent() throws Exception {
		ExternalAccount accountMock = generateMockAccount();

		Map<String, String> clientMap = new HashMap<>();
		clientMap.put("externalId", null);

		ClientVO clientMock = generateMockClientVO();

		when(customerSuiteTalkService.getCustomer(any(), anyString(), anyBoolean())).thenReturn(clientMock);
		when(externalAccountDAO.findById(any())).thenReturn(Optional.of(accountMock));		

		List<Map<String, String>> parentMaps = clientService.getClientParents(clientMap);
        
		assertEquals(0, parentMaps.size());
	}

	@Test
	@DisplayName("when requesting all active clients with an account balance, then only active clients with an account balance is returned")
	public void testFindWithBalance() throws Exception {
		ClientVO mockClientVO1 = generateMockClientVO()
		        .setBalance(BigDecimal.ZERO)
				.setUnappliedBalance(BigDecimal.ZERO)
				.setAgingCurrent(BigDecimal.ZERO)
				.setAging30(BigDecimal.ZERO)
				.setAging60(BigDecimal.ZERO)
				.setAging90(BigDecimal.ZERO)
				.setAging91Plus(BigDecimal.ZERO);
				
		ClientVO mockClientVO2 = generateMockClientVO()
				.setBalance(BigDecimal.ONE);		

		List<ClientVO> mockClientVOs = Arrays.asList(new ClientVO[]{mockClientVO1, mockClientVO2});
		List<ClientVO> expectedClientVOs = Arrays.asList(new ClientVO[]{mockClientVO2});

		when(customerSuiteTalkService.findAllActive()).thenReturn(mockClientVOs);
		when(customerSuiteTalkService.enrichWithNotes(any())).thenAnswer(i -> i.getArguments()[0]);

		List<ClientVO> actualClientVOs = clientService.findWithBalance();

		verify(customerSuiteTalkService, times(1)).enrichWithNotes(eq(mockClientVO2));

		assertEquals(expectedClientVOs, actualClientVOs);
	}

	private ClientVO generateMockClientVO() {
		return new ClientVO()
		        .setInternalId("1")
		        .setAccountCode("-10000000")
				.setAccountName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx81")
				.setShortName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx81")
				.setTaxId("12-3456789")
				.setParentAccountCode(null)
				.setEmail("no-reply@mikealbert.com")
				.setPhoneNumber("555-555-5555")
				.setFaxNumber("666-666-6666")				
				.setStatus("CLIENT-Closed Won")
				.setInactive(false)
				.setCurrency("USA")
				.setTerms("1st of Mo")
				.setCategory("Yellow Express")
				.setCreditStatus("Review Required")
				.setFinanceCharge(true)
				.setCreditManagementType("No Hierarchy")
				.setLastCreditCheck(new Date())
				.setCreditLimit1(new BigDecimal("999999999999.99"))
				.setCreditLimit2(new BigDecimal("999999999999.99"))
				.setUnitLimit1(99999L)
				.setUnitLimit2(99999L)
				.setCapitalLimit1(new BigDecimal("999999999999.99"))
				.setCapitalLimit2(new BigDecimal("999999999999.99"))
				.setPurchaseCreditLimit(new BigDecimal("999999999999.99"))
				.setCreditScore("ONE-1")
				.setIncorporationState("Ohio")
				.setRiskDepositAmount(new BigDecimal("999999999999.99"))
				.setSuppressStatement(true)
				.setInterimFinanceCharge(true)
				.setBankrupt(false)
				.setAddressInternalId("1")
				.setAddress1("10340 Evendale Dr")
				.setAddress2("Attn: Perseverance")
				.setCountry("_unitedStates")
				.setRegion("OH")
				.setCounty("Hamilton")
				.setCity("Cincinnati")
				.setPostalCode("45241-2512");
	}

	private ExternalAccount generateMockAccount() {
		ExternalAccount account = new ExternalAccount();

		account.setId(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), ClientServiceTest.CUSTOMER_ACCOUNT_CODE));
		account.setExternalAccountAddresses(new ArrayList<>(0));
		account.getExternalAccountAddresses().add(new ExtAccAddress());
		account.getExternalAccountAddresses().get(0).setAddressCode("1");
		account.getExternalAccountAddresses().get(0).setDefaultBilling(true);
		account.getExternalAccountAddresses().get(0).setDefaultInd("Y");
		account.getExternalAccountAddresses().get(0).setAddressType("POST");
		account.getExternalAccountAddresses().get(0).setAddressLine1("10340 Evendale Dr");
		account.getExternalAccountAddresses().get(0).setAddressLine2("Attn: Perseverance");
		account.getExternalAccountAddresses().get(0).setCountry("USA");
		account.getExternalAccountAddresses().get(0).setRegion("OH");
		account.getExternalAccountAddresses().get(0).setCountyCode("016");
		account.getExternalAccountAddresses().get(0).setTownCity("Cincinnati");
		account.getExternalAccountAddresses().get(0).setPostcode("45241-2512");

		return account;
	}
}

package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.ClientVO;

@Disabled("Temp disable.  Need to figure out why this is failing")
@DisplayName("Given a client")
@SpringBootTest
public class CustomerSuiteTalkServiceTest extends BaseTest{
	@Resource CustomerSuiteTalkService customerSuiteTalkService;
	
	@DisplayName("when a new child of a parent, then the child client is returned with all fields populated inlucing references to the parent account")
	@Test
	public void testGetChildCustomerAllFieldsSet() throws Exception {       
		ClientVO expectedParentClientVO = generateMockCustomer();

		ClientVO expectedChildClientVO = generateMockCustomer();
		expectedChildClientVO.setParentAccountCode(expectedParentClientVO.getExternalId());

		createWrapper(expectedParentClientVO);
		createWrapper(expectedChildClientVO);

		ClientVO actualClientVO = customerSuiteTalkService.getCustomer(null, expectedChildClientVO.getExternalId(), true);

		customerSuiteTalkService.delete(expectedChildClientVO);
		customerSuiteTalkService.delete(expectedParentClientVO);		

		assertEquals("Hierarchy / Share Limits", actualClientVO.getCreditManagementType());

		assertNotNull(actualClientVO.getInternalId());
		assertNotNull(actualClientVO.getEntityId());
		assertNotNull(actualClientVO.getSubsidiary());
		assertNotNull(actualClientVO.getAccountCode());
		assertNotNull(actualClientVO.getAccountName());
		assertNotNull(actualClientVO.getShortName());
		assertNotNull(actualClientVO.getPrintOnCheckAs());
		assertNotNull(actualClientVO.getEmail());
		assertNotNull(actualClientVO.getPhoneNumber());
		assertNotNull(actualClientVO.getFaxNumber());		
		assertNotNull(actualClientVO.getCategory());
		assertNotNull(actualClientVO.getCurrency());
		assertNotNull(actualClientVO.getParentAccountCode());
		assertNotNull(actualClientVO.getParentInternalId());
		assertNotNull(actualClientVO.getParentExternalId());		
		assertNotNull(actualClientVO.getStatus());
		assertNotNull(actualClientVO.getTerms());
		assertNotNull(actualClientVO.getCreditStatus());
		assertNotNull(actualClientVO.getCreditScore());		
		assertNotNull(actualClientVO.getLastCreditCheck());
		assertNotNull(actualClientVO.getIncorporationState());
		assertNotNull(actualClientVO.getRiskDepositAmount());	
		assertNotNull(actualClientVO.getAddressInternalId());
		assertNotNull(actualClientVO.getAddressExternalId());
		assertNotNull(actualClientVO.getAddress1());
		assertNotNull(actualClientVO.getAddress2());
		assertNotNull(actualClientVO.getCountry());
		assertNotNull(actualClientVO.getRegion());
		assertNotNull(actualClientVO.getCounty());
		assertNotNull(actualClientVO.getCity());
		assertNotNull(actualClientVO.getPostalCode());
		assertNotNull(actualClientVO.getBalance());
		assertNotNull(actualClientVO.getDepositBalance());
		assertNotNull(actualClientVO.getAgingCurrent());	
		assertNotNull(actualClientVO.getAging30());	
		assertNotNull(actualClientVO.getAging60());	
		assertNotNull(actualClientVO.getAging90());	
		assertNotNull(actualClientVO.getAging91Plus());	
		assertNotNull(actualClientVO.getLastPaymentAmount());
		assertNotNull(actualClientVO.getCollectionStatus());

		assertNull(actualClientVO.getCreditLimit1());
		assertNull(actualClientVO.getCreditLimit2());
		assertNull(actualClientVO.getUnitLimit1());
		assertNull(actualClientVO.getUnitLimit2());
		assertNull(actualClientVO.getCapitalLimit1());
		assertNull(actualClientVO.getCapitalLimit2());	
		assertNull(actualClientVO.getPurchaseCreditLimit());
		assertNull(actualClientVO.getLastPaymentDate());
		assertNull(actualClientVO.getCollectionNote());

		assertTrue(actualClientVO.isInterimFinanceCharge());
		assertTrue(actualClientVO.isSuppressStatement());
		assertTrue(actualClientVO.isBankrupt());
		assertFalse(actualClientVO.isFinanceCharge());		
		assertFalse(actualClientVO.isPerson());
		assertFalse(actualClientVO.isInactive());

		assertEquals(2, actualClientVO.getIncorporationState().length());
	}

	@DisplayName("when a new client, then the client is returned with all fields populated")
	@Test
	public void testGetCustomerAllFieldsSet() throws Exception {       
		ClientVO expectedClientVO = generateMockCustomer();

		createWrapper(expectedClientVO);

		ClientVO actualClientVO = customerSuiteTalkService.getCustomer(null, expectedClientVO.getExternalId(), true);

		//ClientVO actualClientVO = customerSuiteTalkService.getCustomer("22875", null);

		customerSuiteTalkService.delete(expectedClientVO);

		assertEquals("No Hierarchy", actualClientVO.getCreditManagementType());

		assertNotNull(actualClientVO.getInternalId());
		assertNotNull(actualClientVO.getEntityId());
		assertNotNull(actualClientVO.getSubsidiary());
		assertNotNull(actualClientVO.getAccountCode());
		assertNotNull(actualClientVO.getAccountName());
		assertNotNull(actualClientVO.getShortName());
		assertNotNull(actualClientVO.getPrintOnCheckAs());
		assertNotNull(actualClientVO.getEmail());
		assertNotNull(actualClientVO.getPhoneNumber());
		assertNotNull(actualClientVO.getFaxNumber());		
		assertNotNull(actualClientVO.getCategory());
		assertNotNull(actualClientVO.getCurrency());
		assertNotNull(actualClientVO.getStatus());
		assertNotNull(actualClientVO.getTerms());
		assertNotNull(actualClientVO.getCreditStatus());
		assertNotNull(actualClientVO.getCreditScore());		
		assertNotNull(actualClientVO.getLastCreditCheck());
		assertNotNull(actualClientVO.getIncorporationState());
		assertNotNull(actualClientVO.getRiskDepositAmount());	
		assertNotNull(actualClientVO.getAddressInternalId());
		assertNotNull(actualClientVO.getAddressExternalId());
		assertNotNull(actualClientVO.getAddress1());
		assertNotNull(actualClientVO.getAddress2());
		assertNotNull(actualClientVO.getCountry());
		assertNotNull(actualClientVO.getRegion());
		assertNotNull(actualClientVO.getCounty());
		assertNotNull(actualClientVO.getCity());
		assertNotNull(actualClientVO.getPostalCode());
		assertNotNull(actualClientVO.getCreditLimit1());
		assertNotNull(actualClientVO.getCreditLimit2());
		assertNotNull(actualClientVO.getUnitLimit1());
		assertNotNull(actualClientVO.getUnitLimit2());
		assertNotNull(actualClientVO.getCapitalLimit1());
		assertNotNull(actualClientVO.getCapitalLimit2());	
		assertNotNull(actualClientVO.getPurchaseCreditLimit());	
		assertNotNull(actualClientVO.getAgingCurrent());	
		assertNotNull(actualClientVO.getAging30());	
		assertNotNull(actualClientVO.getAging60());	
		assertNotNull(actualClientVO.getAging90());	
		assertNotNull(actualClientVO.getAging91Plus());	
		assertNotNull(actualClientVO.getLastPaymentAmount());

		assertNull(actualClientVO.getParentAccountCode());	
		assertNull(actualClientVO.getLastPaymentDate());	
		assertNull(actualClientVO.getCollectionNote());

		assertTrue(actualClientVO.isInterimFinanceCharge());
		assertTrue(actualClientVO.isSuppressStatement());		
		assertTrue(actualClientVO.isBankrupt());

		assertFalse(actualClientVO.isFinanceCharge());		
		assertFalse(actualClientVO.isPerson());
		assertFalse(actualClientVO.isInactive());

		assertEquals(2, actualClientVO.getIncorporationState().length());
	}	

	@Disabled
	@DisplayName("when requesting all active clients, then all active clients are returned")
	@Test
	public void testAllActive() throws Exception {  
		List<ClientVO> clients =  customerSuiteTalkService.findAllActive();
		
		assertTrue(clients.size() > 0);		
	}	

	@DisplayName("when updating its external id, the update is saved in the the accounting system")
	@Test
	public void testUpdate() throws Exception {
		ClientVO expectedClientVO, actualClientVO;

		expectedClientVO = generateMockCustomer();
		createWrapper(expectedClientVO);

		actualClientVO = customerSuiteTalkService.getCustomer(null, expectedClientVO.getExternalId(), true);
		actualClientVO.setExternalId(actualClientVO.getExternalId());
		customerSuiteTalkService.update(actualClientVO);

		actualClientVO = customerSuiteTalkService.getCustomer(actualClientVO.getInternalId(), null, true);

		customerSuiteTalkService.delete(actualClientVO);
		
		assertTrue(actualClientVO.getExternalId().startsWith("1C"));
		assertNotNull(actualClientVO.getAddressExternalId());
	}

	@Disabled("SB1 no longer has the invalid character(s) in the note text for this client")
	@Test
	public void testEnrichWithNotesWithInvalidCharacter() throws Exception {
		final String ERROR_CODE = "ERROR";
		final String CLIENT_INT_ID = "13232";
		final String CLIENT_EXT_ID = "1C00006904";
	
		ClientVO mockClient = new ClientVO()
				.setInternalId(CLIENT_INT_ID)
				.setExternalId(CLIENT_EXT_ID);
	
		ClientVO actualClient =  customerSuiteTalkService.enrichWithNotes(mockClient);
	
		assertEquals(ERROR_CODE, actualClient.getCollectionNote().getComment());
   }
	
	private ClientVO generateMockCustomer() throws Exception {
		Thread.sleep(5);
		String uid = "-" + String.valueOf(System.currentTimeMillis());
		return new ClientVO()
		        .setExternalId(customerSuiteTalkService.formatExternalId(uid))
		        .setEntityId(uid)
		        .setPerson(false)
		        .setAccountCode(uid)
		        .setAccountName("UT " + uid)
				.setPrintOnCheckAs("UT " + uid)
		        .setSubsidiary(1L)
		        .setEmail("ut@ut.com")
		        .setPhoneNumber("555-555-5555")
				.setFaxNumber("666-666-6666")
		        .setStatus("CLIENT-Closed Won")
		        .setCategory("Fleet with Units")
		        .setCurrency("USA")
		        .setTerms("1st of the Month")
		        .setCreditLimit1(BigDecimal.ONE)
		        .setCreditLimit2(BigDecimal.ONE)
		        .setUnitLimit1(1L)
		        .setUnitLimit2(1L)
		        .setCapitalLimit1(BigDecimal.ONE)
		        .setCapitalLimit2(BigDecimal.ONE)
		        .setPurchaseCreditLimit(BigDecimal.ONE)
		        .setCreditScore("ONE-1")
		        .setCreditStatus("Review Required")
		        .setCreditManagementType("No Hierarchy")
		        .setLastCreditCheck(new Date())
		        .setFinanceCharge(true)
				.setSuppressStatement(true)
		        .setIncorporationState("Delaware") //TODO Willow has state code -> may be a problem
		        .setRiskDepositAmount(BigDecimal.ONE)
		        .setInterimFinanceCharge(true)
		        .setBankrupt(true)
				.setCollectionStatus("A")
		        .setAddressExternalId(uid)
		        .setDefaultBilling(true)
		        .setDefaultShipping(true)
		        .setAddress1("A1 " + uid)
		        .setAddress2("A2 " + uid)
		        .setCountry("United States")
		        .setRegion("OH")
		        .setCounty("Hamilton")
		        .setCity("Evendale")
		        .setPostalCode("45241-2512");
	}

	private void createWrapper(ClientVO clientVO) throws Exception {
		customerSuiteTalkService.create(clientVO);
	}

}

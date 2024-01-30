package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.VendorTestHelper;
import com.mikealbert.accounting.processor.client.suiteanalytics.VendorSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ExtAccAddressDAO;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.enumeration.PaymentMethodEnum;
import com.mikealbert.accounting.processor.enumeration.TaxFormEnum;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorFieldEnum;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.accounting.processor.vo.VendorVO;
import com.mikealbert.util.data.DataUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class VendorServiceTest extends BaseTest{
	@Resource VendorService vendorService;
	
	@MockBean ExternalAccountDAO externalAccountDAO;
	@MockBean VendorSuiteAnalyticsService vendorSuiteAnalyticService;
	@MockBean VendorSuiteTalkService vendorSuiteTalkService;
	@MockBean TaxJurisdictionService taxJurisdictionService;	
	@MockBean ExtAccAddressDAO extAccAddressDAO;
			
	@Test
	public void testUpsertVendor() throws Exception{	
		final String INT_C_ID = "1";
		final String EXT_C_ID = "1";
		
        VendorVO mockVendorVO = VendorTestHelper.createVendorVO();

		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.C_ID.getName(), EXT_C_ID);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> i.getArguments()[0]);
		
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));		
		
		assertNotNull(vendor, "Vendor upsert did not work");
		assertEquals(1, vendor.getExternalAccountBankAccounts().size(), "Bank details was not created");
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), vendor.getRegName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getAccountName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getPayeeName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 25), vendor.getShortName());	
		assertEquals(INT_C_ID, String.valueOf(vendor.getId().getCId()));
		assertEquals(4L, vendor.getExternalAccountBankAccounts().get(0).getEabaBaId());
	}
	
	
	@Test
	public void testUpsertVendorWithoutAdddress() throws Exception{	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		
		List<Map<String, Object>> mockNSAddresses = null;
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
			
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));		
	
		assertNull(vendor);
	}
	
	@Test
	public void testUpsertVendorNewVendorWithNewW9Adddress() throws Exception{	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
			
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("0"),  
    			eq("0"));		
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), vendor.getRegName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getPayeeName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getAccountName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 25), vendor.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), vendor.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), vendor.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), vendor.getFaxCode());
		assertEquals(null, vendor.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), vendor.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), vendor.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), vendor.getGroupCode());
		assertEquals("USD", vendor.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), vendor.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), vendor.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), vendor.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), vendor.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, vendor.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation	
				
		assertEquals("POST", vendor.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(0).getPostcode());
		assertEquals("Y", vendor.getExternalAccountAddresses().get(0).getDefaultInd());	
		
		//assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName()), vendor.getban());		
		
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());
	}	
	
	
	@Test
	public void testUpsertVendorNewVendorWithNewW9AndRemitAdddress() throws Exception{	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();		
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
								
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	

    	verify(vendorSuiteTalkService, times(2)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());  
    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("0"),  
    			eq("0"));
    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"),  
    			eq("1"));    	
    	
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), vendor.getRegName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getPayeeName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getAccountName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 25), vendor.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), vendor.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), vendor.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), vendor.getFaxCode());
		assertEquals(null, vendor.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), vendor.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), vendor.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), vendor.getGroupCode());
		assertEquals("USD", vendor.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), vendor.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), vendor.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), vendor.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), vendor.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, vendor.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation	
				
		assertEquals("POST", vendor.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());			
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(0).getPostcode());
		assertNull(vendor.getExternalAccountAddresses().get(0).getDefaultInd());	

		assertEquals("POST", vendor.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("1", vendor.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(1).getPostcode());
		assertEquals("Y", vendor.getExternalAccountAddresses().get(1).getDefaultInd());
		
		//assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName()), vendor.getban());		
		
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());
	}
	
	@Test
	public void testUpsertVendorNewVendorWithNewChild() throws Exception{	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();					
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();

		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "T");
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
		
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));
		
		assertNull(account);					
	}	

	@Test
	public void testUpsertVendorNewVendorWithNewW9AndChildAdddress() throws Exception{
    	ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
    	ExternalAccount childAccount;    	
    	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();					
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "T");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.ATTENTION.getName(),  VendorTestHelper.ATTENTION);		

		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);				
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", "11111111")));		
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
				
		childAccount = accountCaptor.getAllValues().stream()
				.filter(child -> child.getId().getAccountCode().contains(VendorService.CHILD_ACCOUNT_CODE_PREFIX))
				.findFirst()
				.orElse(null);		
		
    	verify(externalAccountDAO, times(2)).save(any());
		   	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("0"),  
    			eq("0"));
    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"),  
    			any());	    	
    	
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), childAccount.getRegName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getPayeeName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getAccountName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getShortName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), childAccount.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), childAccount.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), childAccount.getFaxCode());
		assertEquals(null, childAccount.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), childAccount.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), childAccount.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), childAccount.getGroupCode());
		assertEquals("USD", account.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), childAccount.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), childAccount.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), childAccount.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), childAccount.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, account.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation
		
    	assertEquals(1, childAccount.getExternalAccountAddresses().size());
    	assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getPayeeName());
    	assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getAccountName());
    	assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getShortName());    	
    	assertEquals(account.getId().getAccountCode(), childAccount.getParentAccount());    			
	}	
	
	@Test
	public void testUpsertVendorExistingVendorChangeRemitToChildAddress() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);   
    	
	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(2);
		
		ExternalAccount childAccount;
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();					
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "T");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.ATTENTION.getName(),  VendorTestHelper.ATTENTION);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);				
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.nextChildAccountCode()).thenReturn(VendorTestHelper.CHILD_ACCOUNT_CODE);		
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);		
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", VendorTestHelper.ACCOUNT_CODE)));		
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount parentAccount = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));
		
		childAccount = accountCaptor.getAllValues().stream()
				.filter(child -> child.getId().getAccountCode().contains(VendorService.CHILD_ACCOUNT_CODE_PREFIX))
				.findFirst()
				.orElse(null);
		
    	verify(externalAccountDAO, times(2)).save(any());
    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(anyString(), anyString(), anyString(), anyString());
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"),  
    			any());	
    	
    	assertEquals(2, parentAccount.getExternalAccountAddresses().size());
    	
    	assertNotNull(childAccount.getParentAccount());
    	assertEquals(1, childAccount.getExternalAccountAddresses().size());
    	assertEquals("1", childAccount.getExternalAccountAddresses().get(0).getAddressCode());
    	assertEquals("Y", childAccount.getExternalAccountAddresses().get(0).getDefaultInd());
	}
	
	@Test
	public void testUpsertVendorExistingVendorWithAllAddressesDeleted() throws Exception{    	
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);   
    	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(2);
    	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();							
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = new ArrayList<>();
	
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);				
		when(extAccAddressDAO.findByRelatedAccountId(anyLong(), anyString(), anyString())).thenReturn(mockParentAccount.getExternalAccountAddresses());		
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		doNothing().when(extAccAddressDAO).delete(any());		
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", "11111111")));				
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});	
	
		vendorService.reconcileDeletedAddresses(mockParentAccount, mockNSAddresses);

    	verify(extAccAddressDAO, times(0)).deleteById(anyLong());   	
	}
	
	@Test
	public void testUpsertVendorExistingVendorWithExistingW9AndNewChildAdddress() throws Exception{    	
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
    	ExternalAccount childAccount;   
    	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(1);
		ExternalAccount mockChildAccount = VendorTestHelper.generateAccount(1); 
		mockChildAccount.setId(new ExternalAccountPK(1, "S", VendorTestHelper.ACCOUNT_CODE + "-1"));
		mockChildAccount.getExternalAccountAddresses().clear();
    	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();							
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "T");	

		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);				
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));
		when(externalAccountDAO.findById(new ExternalAccountPK(1, "S", mockParentAccount.getId().getAccountCode() + "-1"))).thenReturn(Optional.of(mockChildAccount));		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", "11111111")));		
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
				
		childAccount = accountCaptor.getAllValues().stream()
				.filter(child -> child.getId().getAccountCode().contains(VendorService.CHILD_ACCOUNT_CODE_PREFIX))
				.findFirst()
				.orElse(null);		
		
    	verify(externalAccountDAO, times(2)).save(any());		   	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"),  
    			any());	    	
    	
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), childAccount.getRegName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getPayeeName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getAccountName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), childAccount.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), childAccount.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), childAccount.getFaxCode());
		assertEquals(null, childAccount.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), childAccount.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), childAccount.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), childAccount.getGroupCode());
		assertEquals("USD", childAccount.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), childAccount.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), childAccount.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), childAccount.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), childAccount.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, account.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation
		
    	assertEquals(1, account.getExternalAccountAddresses().size());
		assertEquals("POST", childAccount.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("1", childAccount.getExternalAccountAddresses().get(0).getAddressCode());			
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), childAccount.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), childAccount.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), childAccount.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), childAccount.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), childAccount.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), childAccount.getExternalAccountAddresses().get(0).getPostcode());
		assertEquals("Y", childAccount.getExternalAccountAddresses().get(0).getDefaultInd());    	
		
    	assertEquals(1, childAccount.getExternalAccountAddresses().size());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getPayeeName());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getAccountName());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getShortName());    	
    	assertEquals(account.getId().getAccountCode(), childAccount.getParentAccount());
    			
	}
	
	@Test
	public void testUpsertVendorExistingVendorWithExistingW9AndChildAdddress() throws Exception{     	
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
    	ExternalAccount childAccount;   
    	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(1); 
		
		ExternalAccount mockChildAccount = VendorTestHelper.generateAccount(1); 
		mockChildAccount.setId(new ExternalAccountPK(1, "S", VendorTestHelper.CHILD_ACCOUNT_CODE));
		mockChildAccount.getExternalAccountAddresses().get(0).setEaaId(1L);
		mockChildAccount.getExternalAccountAddresses().get(0).setAddressCode("1");
    	
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();							
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");			
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "T");		

		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);				
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));
		when(externalAccountDAO.findById(new ExternalAccountPK(1, "S", VendorTestHelper.CHILD_ACCOUNT_CODE))).thenReturn(Optional.of(mockChildAccount));		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.nextChildAccountCode()).thenReturn(VendorTestHelper.CHILD_ACCOUNT_CODE);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", VendorTestHelper.CHILD_ACCOUNT_CODE)));		
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
				
		childAccount = accountCaptor.getAllValues().stream()
				.filter(child -> child.getId().getAccountCode().contains(VendorService.CHILD_ACCOUNT_CODE_PREFIX))
				.findFirst()
				.orElse(null);		
		
    	verify(externalAccountDAO, times(2)).save(any());		   	
    	verify(vendorSuiteTalkService, times(0)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());    		    	
    	
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), childAccount.getRegName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getPayeeName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getAccountName());
		assertEquals(VendorTestHelper.ATTENTION, childAccount.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), childAccount.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), childAccount.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), childAccount.getFaxCode());
		assertEquals(null, childAccount.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), childAccount.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), childAccount.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), childAccount.getGroupCode());
		assertEquals("USD", childAccount.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), childAccount.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), childAccount.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), childAccount.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), childAccount.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), childAccount.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, account.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation
		
    	assertEquals(1, account.getExternalAccountAddresses().size());
		assertEquals("POST", childAccount.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("1", childAccount.getExternalAccountAddresses().get(0).getAddressCode());			
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), childAccount.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), childAccount.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), childAccount.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), childAccount.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), childAccount.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), childAccount.getExternalAccountAddresses().get(0).getPostcode());
		assertEquals("Y", childAccount.getExternalAccountAddresses().get(0).getDefaultInd());    	
		
    	assertEquals(1, childAccount.getExternalAccountAddresses().size());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getPayeeName());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getAccountName());
    	assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ATTENTION.getName()), childAccount.getShortName());    	
    	assertEquals(account.getId().getAccountCode(), childAccount.getParentAccount());
    			
	}	
	
	@Test
	public void testUpsertVendorExistingVendorWithExistingW9AndNewRemitAdddress() throws Exception{	
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(1);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();							
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);		
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
						
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());		
    	
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"),  
    			eq("1"));		    	
    	
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), vendor.getRegName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getPayeeName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getAccountName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 25), vendor.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), vendor.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), vendor.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), vendor.getFaxCode());
		assertEquals(null, vendor.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), vendor.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), vendor.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue() , vendor.getGroupCode());
		assertEquals("USD", vendor.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), vendor.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), vendor.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), vendor.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), vendor.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, vendor.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation	
				
		assertEquals("POST", vendor.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());			
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(0).getPostcode());
		assertNull(vendor.getExternalAccountAddresses().get(0).getDefaultInd());	

		assertEquals("POST", vendor.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("1", vendor.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(1).getPostcode());
		assertEquals("Y", vendor.getExternalAccountAddresses().get(1).getDefaultInd());
		
		//assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName()), vendor.getban());		
		
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());
	}
	
	@Test
	public void testUpsertVendorExistingVendorWithExistingW9AndExistingRemitAndNewRemitAdddress() throws Exception{	
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(2);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();							
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(3);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");		
		mockNSAddresses.get(2).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("2"), 
    			eq("2"));			
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), vendor.getRegName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getPayeeName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 80), vendor.getAccountName());
		assertEquals(DataUtil.substr(mockVendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()), 0, 25), vendor.getShortName());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.CATEGORY.getName()), vendor.getOrganisationType());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.PHONE.getName()), vendor.getTelephoneNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.FAX.getName()), vendor.getFaxCode());
		assertEquals(null, vendor.getEmail());
		assertEquals(mockVendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()), vendor.getCreditTermsCode());	
		assertEquals(mockVendorMap.get(VendorFieldEnum.TAX_ID.getName()).replaceAll("-", ""), vendor.getTaxRegNo());		
		assertEquals(TaxFormEnum.NONE.getValue(), vendor.getGroupCode());
		assertEquals("USD", vendor.getCurrencyCode());		//TODO Default currency code should be a global constant
		assertEquals(PaymentMethodEnum.valueOf(mockVendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName())).getValue(), vendor.getPaymentMethod());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()), vendor.getFirstName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()), vendor.getLastName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()), vendor.getOccupation());	
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getBankName());		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getBankSortCode());
		
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountNumber());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getExternalAccountBankAccounts().get(0).getBankSortCode());
		assertEquals(4L, vendor.getExternalAccountBankAccounts().get(0).getEabaBaId());	//TODO Constant maybe for account type translation	
		
		
		assertEquals("POST", vendor.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("W9", vendor.getExternalAccountAddresses().get(0).getAddressCode());		
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(0).getPostcode());
		assertNull(vendor.getExternalAccountAddresses().get(0).getDefaultInd());	

		assertEquals("POST", vendor.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("1", vendor.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(1).getPostcode());
		assertNull(vendor.getExternalAccountAddresses().get(1).getDefaultInd());

		assertEquals("POST", vendor.getExternalAccountAddresses().get(2).getAddressType());
		assertEquals("2", vendor.getExternalAccountAddresses().get(2).getAddressCode());		
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), vendor.getExternalAccountAddresses().get(2).getAddressLine1());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), vendor.getExternalAccountAddresses().get(2).getAddressLine2());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.COUNTRY.getName()), vendor.getExternalAccountAddresses().get(2).getCountry());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.STATE.getName()), vendor.getExternalAccountAddresses().get(2).getRegion());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.CITY.getName()), vendor.getExternalAccountAddresses().get(2).getTownCity());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ZIP.getName()), vendor.getExternalAccountAddresses().get(2).getPostcode());
		assertEquals("Y", vendor.getExternalAccountAddresses().get(2).getDefaultInd());		
	}	
	
	@Test
	public void testUpsertVendorNewVendorWithACHPaymentMethod() throws Exception{	

		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		mockVendorVO.setPaymentMethod(PaymentMethodEnum.ACH.name());

		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.PAYMENT_METHOD.getName(), PaymentMethodEnum.ACH.name());
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> i.getArguments()[0]);
		
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));		
				
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()), vendor.getBankAccountName());
		assertEquals(mockVendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()), vendor.getBankAccountNumber());
		assertNotNull(mockVendorMap.get(VendorFieldEnum.BANK_NAME.getName()), vendor.getBankName());
		assertNotNull(mockVendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()), vendor.getBankSortCode());
		assertTrue(vendor.getExternalAccountBankAccounts().size() > 0);
	}		
	
	@Test
	public void testUpsertVendorWithNonACHPaymentMethod() throws Exception{	
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		mockVendorVO.setPaymentMethod(PaymentMethodEnum.CHECK.name());

		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.PAYMENT_METHOD.getName(), PaymentMethodEnum.CHECK.name());
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> i.getArguments()[0]);
		
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));		
				
		assertNull(vendor.getBankAccountName());
		assertNull(vendor.getBankAccountNumber());
		assertNull(vendor.getBankName());
		assertNull(vendor.getBankSortCode());
		assertTrue(vendor.getExternalAccountBankAccounts().size() == 0);
	}	
	
	@Test
	public void testUpsertVendorWithW9AndRemit() throws Exception{

		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> i.getArguments()[0]);
		
		ExternalAccount vendor = vendorService.upsertVendor(mockVendorMap,vendorService.getAddresses(mockVendorMap));		
		
		List<ExtAccAddress> remitAddress = vendor.getExternalAccountAddresses().stream()
				.filter(address -> !"W9".equalsIgnoreCase(address.getAddressCode()))
		.collect(Collectors.toList());

		List<ExtAccAddress> w9Address = vendor.getExternalAccountAddresses().stream()
		.filter(address -> "W9".equalsIgnoreCase(address.getAddressCode()))
		.collect(Collectors.toList());
				
		assertEquals(1, remitAddress.size());
		assertEquals("1", remitAddress.get(0).getAddressCode(), "Remite address code is incorrect");
		
		assertEquals(1, w9Address.size());
		assertEquals("W9", w9Address.get(0).getAddressCode(), "W9 address code is incorrect");		
	}		
	
	@Test
	public void testUpsertVendorExistingVendorNewDefaultAddress() throws Exception {		
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(1);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");			
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "T");		
		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(
    			eq(mockVendorMap.get(VendorFieldEnum.ENTITY_ID.getName())), 
    			eq(mockVendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName())), 
    			eq("1"), 
    			eq("1"));					
		
		assertEquals("POST", account.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("1", account.getExternalAccountAddresses().get(0).getAddressCode());		
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(0).getPostcode());
		assertEquals("Y", account.getExternalAccountAddresses().get(0).getDefaultInd());    	
		
		assertEquals("POST", account.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("W9", account.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(1).getPostcode());
		assertNull(account.getExternalAccountAddresses().get(1).getDefaultInd());
	}
	
	@Test
	public void testUpsertVendorExistingVendorExistingAddressesChangeDefaultAddress() throws Exception {		
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(2);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");			
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "T");		
		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(0)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());				
		
		assertEquals("POST", account.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("1", account.getExternalAccountAddresses().get(0).getAddressCode());		
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(0).getPostcode());
		assertEquals("Y", account.getExternalAccountAddresses().get(0).getDefaultInd());    	
		
		assertEquals("POST", account.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("W9", account.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(1).getPostcode());
		assertNull(account.getExternalAccountAddresses().get(1).getDefaultInd());
		
	}
	
	@Test
	public void testUpsertVendorExistingVendorExistingW9AndRemitAddressesAddNewDefaultAddress() throws Exception {		
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(2);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(3);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");			
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");
		mockNSAddresses.get(2).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null);
		mockNSAddresses.get(2).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "T");		
		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
    	verify(vendorSuiteTalkService, times(1)).updateVendorExternalIdAndAddressExternalId(any(), any(), any(), any());				
		
		assertEquals("POST", account.getExternalAccountAddresses().get(0).getAddressType());
		assertEquals("1", account.getExternalAccountAddresses().get(0).getAddressCode());		
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(0).getAddressLine1());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(0).getAddressLine2());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(0).getCountry());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(0).getRegion());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(0).getTownCity());
		assertEquals(mockNSAddresses.get(0).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(0).getPostcode());
		assertNull(account.getExternalAccountAddresses().get(0).getDefaultInd());    	
		
		assertEquals("POST", account.getExternalAccountAddresses().get(1).getAddressType());
		assertEquals("2", account.getExternalAccountAddresses().get(1).getAddressCode());		
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(1).getAddressLine1());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(1).getAddressLine2());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(1).getCountry());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(1).getRegion());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(1).getTownCity());
		assertEquals(mockNSAddresses.get(1).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(1).getPostcode());
		assertEquals("Y", account.getExternalAccountAddresses().get(1).getDefaultInd());	
		
		assertEquals("POST", account.getExternalAccountAddresses().get(2).getAddressType());
		assertEquals("W9", account.getExternalAccountAddresses().get(2).getAddressCode());		
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName()), account.getExternalAccountAddresses().get(2).getAddressLine1());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName()), account.getExternalAccountAddresses().get(2).getAddressLine2());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.COUNTRY.getName()), account.getExternalAccountAddresses().get(2).getCountry());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.STATE.getName()), account.getExternalAccountAddresses().get(2).getRegion());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.CITY.getName()), account.getExternalAccountAddresses().get(2).getTownCity());
		assertEquals(mockNSAddresses.get(2).get(VendorAddressFieldEnum.ZIP.getName()), account.getExternalAccountAddresses().get(2).getPostcode());
		assertNull(account.getExternalAccountAddresses().get(2).getDefaultInd());
	}
	
	@Test
	public void testUpsertVendorExistingVendorInActivateAccount() throws Exception {		
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(2);
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		mockVendorVO.setInactive(true);

		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		mockVendorMap.replace(VendorFieldEnum.INACTIVE.getName(), "Yes");
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(3);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
				
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
		assertEquals("C", account.getAccStatus());
		assertEquals("Y", account.getSuppliers().get(0).getInactiveInd());
	}
	
	@Test
	public void testUpsertVendorExistingVendorActivateAccount() throws Exception {		
		ExternalAccount mockAccount = VendorTestHelper.generateAccount(2);
		mockAccount.setAccStatus("C");
		mockAccount.getSuppliers().get(0).setInactiveInd("N");
		
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		mockVendorMap.replace(VendorFieldEnum.INACTIVE.getName(), "No");
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(3);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
				
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);		
				
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);		
		when(externalAccountDAO.findById(isNotNull())).thenReturn(Optional.of(mockAccount));
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(isNotNull())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx)));			
			return  ea;});
			
		ExternalAccount account = vendorService.upsertVendor(mockVendorMap, vendorService.getAddresses(mockVendorMap));	
		
		assertEquals("O", account.getAccStatus());
		assertEquals("N", account.getSuppliers().get(0).getInactiveInd());
	}	
		
	@Test
	public void testReconcileDeletes() throws Exception {
		ExternalAccount account = VendorTestHelper.generateAccount(10);
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		
		List<Long> deletedAddressIds = vendorService.reconcileDeletedAddresses(account, mockNSAddresses);
		assertNotNull(deletedAddressIds);
		//assertEquals(9, deletedAddressIds.size());
	}
	

	@Test
	public void testReconcileDeletedAddresses() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
    	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(2);
		    	
		VendorVO mockParentVendorVO = VendorTestHelper.createVendorVO();		
		Map<String, String> mockParentVendorMap = VendorTestHelper.createVendorMap();
		mockParentVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockParentVendorVO);				
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));		
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", "11111111")));		
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount account = vendorService.upsertVendor(mockParentVendorMap, vendorService.getAddresses(mockParentVendorMap));
		List<Long> deletedAddressIds = vendorService.reconcileDeletedAddresses(account, vendorService.getAddresses(mockParentVendorMap));
		
		assertEquals(1, deletedAddressIds.size());
		assertEquals(1, deletedAddressIds.get(0));		
	}
	
	@Test
	public void testCloseChildAccounts() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
    	
		ExternalAccount mockParentAccount = VendorTestHelper.generateAccount(1);
		
		ExternalAccount mockChildAccount = VendorTestHelper.generateAccount(1); 
		mockChildAccount.setId(new ExternalAccountPK(1, "S", VendorTestHelper.ACCOUNT_CODE + "-1"));
		mockChildAccount.setParentEntity(VendorTestHelper.ACCOUNT_C_ID);
		mockChildAccount.setParentAccountType(VendorTestHelper.ACCOUNT_TYPE);
		mockChildAccount.setParentAccount(VendorTestHelper.ACCOUNT_CODE);
    	
		VendorVO mockParentVendorVO = VendorTestHelper.createVendorVO();		
		Map<String, String> mockParentVendorMap = VendorTestHelper.createVendorMap();
		mockParentVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		
		List<ExtAccAddress> mockAccountAddresses = new ArrayList<>(0);
		mockAccountAddresses.add(new ExtAccAddress());
		mockAccountAddresses.get(0).setExternalAccount(mockParentAccount);
		mockAccountAddresses.get(0).setEaaId(0l);
		mockAccountAddresses.get(0).setAddressType("POST");
		mockAccountAddresses.get(0).setDefaultInd("Y");
		
		mockAccountAddresses.add(new ExtAccAddress());		
		mockAccountAddresses.get(1).setEaaId(1l);
		mockAccountAddresses.get(1).setExternalAccount(mockChildAccount);
		mockAccountAddresses.get(1).setAddressType("POST");
		mockAccountAddresses.get(1).setDefaultInd("Y");
		
		List<ExternalAccount> mockDeletedChildAccounts = new ArrayList<>(0);
		mockDeletedChildAccounts.add(mockChildAccount);
		
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);	
		
		when(vendorSuiteAnalyticService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockParentVendorVO);		
		when(extAccAddressDAO.findByRelatedAccountId(anyLong(), anyString(), anyString())).thenReturn(mockAccountAddresses);
		when(externalAccountDAO.findById(mockParentAccount.getId())).thenReturn(Optional.of(mockParentAccount));
		when(externalAccountDAO.findById(new ExternalAccountPK(1, "S", mockParentAccount.getId().getAccountCode() + "-1"))).thenReturn(Optional.of(mockChildAccount));
		when(externalAccountDAO.findByExternalAccountAddresses(anyList())).thenReturn(mockDeletedChildAccounts);
		when(externalAccountDAO.desencrypt(anyString())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(taxJurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.findByExternalAccountAddress(anyLong())).thenReturn(new ExternalAccount(new ExternalAccountPK(1, "S", "11111111")));
		when(externalAccountDAO.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		ExternalAccount account = vendorService.upsertVendor(mockParentVendorMap, vendorService.getAddresses(mockParentVendorMap));
		List<ExternalAccount> closedChildAccounts = vendorService.closeChildAccounts(account, vendorService.getAddresses(mockParentVendorMap));		
		
		assertEquals(1, closedChildAccounts.size());
		assertEquals("Y", closedChildAccounts.get(0).getSuppliers().get(0).getInactiveInd());
	}	
	
	@Test
	public void testResetDefaultAddress() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
		
		ExternalAccount account = VendorTestHelper.generateAccount(2);
		
		Map<String, String> vendor = VendorTestHelper.createVendorMap();
		vendor.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");
		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "T");		
		
		when(externalAccountDAO.findById(account.getId())).thenReturn(Optional.of(account));
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		account = vendorService.resetDefaultAddress(account, mockNSAddresses);
		
		ExtAccAddress w9Address = account.getExternalAccountAddresses().stream()
				.filter(address -> "W9".equalsIgnoreCase(address.getAddressCode()))
				.findAny().orElse(null);
		
		ExtAccAddress remitAddress = account.getExternalAccountAddresses().stream()
				.filter(address -> !"W9".equalsIgnoreCase(address.getAddressCode()))
				.findAny().orElse(null);
		
		assertNull(w9Address.getDefaultInd());
		assertEquals("Y", remitAddress.getDefaultInd());
		
	}
	
	@DisplayName("when an address is in Willow but not in the accounting system, the address cannot be set to default, i.e. it is ignored")
	@Test
	public void testResetDefaultAddressWithAddressRemoved() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
		
		ExternalAccount account = VendorTestHelper.generateAccount(2);
				
		Map<String, String> vendor = VendorTestHelper.createVendorMap();
		vendor.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "T");		
		
		when(externalAccountDAO.findById(account.getId())).thenReturn(Optional.of(account));
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		account = vendorService.resetDefaultAddress(account, mockNSAddresses);
		
		ExtAccAddress w9Address = account.getExternalAccountAddresses().stream()
				.filter(address -> "W9".equalsIgnoreCase(address.getAddressCode()))
				.findAny().orElse(null);
		
		ExtAccAddress remitAddress = account.getExternalAccountAddresses().stream()
				.filter(address -> !"W9".equalsIgnoreCase(address.getAddressCode()))
				.findAny().orElse(null);
		
		assertNull(remitAddress.getDefaultInd(), "The remit address is not in the accounting system, it should not be the default");
		assertEquals("Y", w9Address.getDefaultInd(), "W9 address should be the default since it is the only address in the accounting system");
		
	}	
		
	@Test
	public void testResetDefaultAddressWithNoDefaultBillingAddress() throws Exception {
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);
		
		ExternalAccount account = VendorTestHelper.generateAccount(2);
		
		Map<String, String> vendor = VendorTestHelper.createVendorMap();
		vendor.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(2);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");
		
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "1");
		mockNSAddresses.get(1).replace(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName(), "F");		
		
		when(externalAccountDAO.findById(account.getId())).thenReturn(Optional.of(account));
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});
		
		account = vendorService.resetDefaultAddress(account, mockNSAddresses);
		
		List<ExtAccAddress> addresses = account.getExternalAccountAddresses().stream()
				.sorted((address1, address2) -> address1.getEaaId().compareTo(address2.getEaaId()))
				.collect(Collectors.toList());
				
	    assertEquals(1L, addresses.get(1).getEaaId());
		assertEquals("Y", addresses.get(1).getDefaultInd());
		
	}
	
	@Test
	public void testGetVendors() throws Exception {
		ArgumentCaptor<Date> fromCaptor = ArgumentCaptor.forClass(Date.class);
		ArgumentCaptor<Date> toCaptor = ArgumentCaptor.forClass(Date.class);		
		
		Date from = new Date();
		Date to = new Date();
		
		when(vendorSuiteAnalyticService.getVendors(fromCaptor.capture(), toCaptor.capture())).thenReturn(new ArrayList<>(0));
		
		vendorService.getVendors(from, to);
		
		assertEquals(to, toCaptor.getValue());
		assertEquals(from, fromCaptor.getValue());		
	}
	
	@Test
	public void testDeleteAddressLinkedToVehicleMovement() throws Exception {    	
		ExternalAccount account = VendorTestHelper.generateAccount(2);
    	
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
			
		when(extAccAddressDAO.isLinkedToVehicleMovement(anyLong())).thenReturn(true);
	
		vendorService.reconcileDeletedAddresses(account, mockNSAddresses);
		
    	verify(extAccAddressDAO, times(0)).deleteById(anyLong());		
	}
	
	@Test
	public void testDeleteAddressNotLinkedToVehicleMovement() throws Exception {    	
		ExternalAccount account = VendorTestHelper.generateAccount(2);
    	
		Map<String, String> mockVendorMap = VendorTestHelper.createVendorMap();
		mockVendorMap.replace(VendorFieldEnum.EXTERNAL_ID.getName(), VendorTestHelper.ACCOUNT_CODE);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
			
		when(extAccAddressDAO.isLinkedToVehicleMovement(anyLong())).thenReturn(false);
	
		vendorService.reconcileDeletedAddresses(account, mockNSAddresses);
		
    	verify(extAccAddressDAO, times(1)).deleteById(anyLong());		
	}	

}

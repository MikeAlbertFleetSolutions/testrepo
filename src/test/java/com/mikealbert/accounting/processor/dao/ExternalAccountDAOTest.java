package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;

@DataJpaTest
public class ExternalAccountDAOTest extends BaseTest{
	@Resource ExternalAccountDAO externalAccountDAO;
		
	static final Long C_ID = 1l;
	static final String ACCOUNT_TYPE = "S";
	
	@Test
	public void testCount() {
		long rowCount = externalAccountDAO.count();
		
		assertTrue(rowCount > 0, "Did not find record(s)");		
	}
	
	@Test
	@Transactional
	public void testFindOpenAccountByCidAndType() {		
		ExternalAccountPK pk = new ExternalAccountPK(1, "S", "00000000");
		ExternalAccount account = externalAccountDAO.findById(pk).orElse(null);
				
		assertNotNull(account, "Account record does not exists");
		assertEquals( 1, account.getExternalAccountAddresses().size(), "Account address record does not exist");
	}
		
	@Test
	public void testFinByAccountAddress() {		
		ExternalAccount account = externalAccountDAO.findByExternalAccountAddress(1L);
		assertNotNull(account, "Account record does not exists");
	}

	@Test
	public void testFinByAccountAddresses() {		
		List<Long> ids = new ArrayList<>(0);
		ids.add(1L);
		
		List<ExternalAccount> account = externalAccountDAO.findByExternalAccountAddresses(ids);
		assertEquals(1, account.size());
	}
	
	@Test
	public void testGetTaxJurisdictionUSA() {
		List<TaxJurisdictionVO> jurisdiction = externalAccountDAO.getTaxJurisdiction("usa", "oh", "symmes township", "45249");
		assertTrue(!jurisdiction.isEmpty(), "Did not find tax jurisdiction");
	}
	
	@Test
	public void testGetTaxJurisdictionCanada() {
		List<TaxJurisdictionVO> jurisdiction = externalAccountDAO.getTaxJurisdiction("cn", "on", "MISSISSAUga", "L5L");
		assertTrue(!jurisdiction.isEmpty(), "Did not find tax jurisdiction");
	}	
	
	@Test 
	public void testUpsertVendor() {
		ExternalAccount savedVendor = null; 
		ExternalAccountPK id = new ExternalAccountPK(1L, "S", "99999");
		
		ExternalAccount vendor = new ExternalAccount();
		vendor.setId(id);
		vendor.setAccStatus("O");
		vendor.setAccountName("TEST ACCOUNT NAME");
		vendor.setShortName("TEST SHORT NAME");
		vendor.setCurrencyCode("USD");
		vendor.setInternationalInd("N");
		vendor.setPaymentInd("Y");
		vendor.setUpfitInd("N");
		vendor.setWebQuotesReqCcApproval("N");
		vendor.setWebQuotesReqFaApproval("N");
		vendor.setEmail("blah@blah.com");
		vendor.setTaxRegNo("TAX ID");	
		vendor.setTelephoneNumber("(513 555-5555)");		
		vendor.setGroupCode("1099-NONE");
		vendor.setOrganisationType("CORP");
		
		vendor.setExternalAccountAddresses(new ArrayList<>());
		vendor.getExternalAccountAddresses().add(createExtAccAddress());		
		
	    savedVendor = externalAccountDAO.save(vendor);			

		assertNotNull(savedVendor, "Vendor record does not exist");			
	}
	
	@Test
	@Transactional
	public void updateVendor() {
		final String ACCOUNT_CODE = "00000000";
		final String EXPECTED_ACCOUNT_NAME = "EXPECTED NAME";
		final String EXPECTED_ADDRESS_LINE_1 = "EXPECTED ADDRESS LINE 1";
		
		ExternalAccountPK pk = new ExternalAccountPK(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE);
		ExternalAccount vendor = externalAccountDAO.findById(pk).orElse(null);
		vendor.setAccountName(EXPECTED_ACCOUNT_NAME);
		vendor.getExternalAccountAddresses().clear();
		vendor.getExternalAccountAddresses().add(createExtAccAddress());
		vendor.getExternalAccountAddresses().get(0).setAddressLine1(EXPECTED_ADDRESS_LINE_1);
		vendor = externalAccountDAO.save(vendor);
		
		assertNotNull(vendor, "Vendor did not return from update");
		assertEquals(EXPECTED_ACCOUNT_NAME, vendor.getAccountName(), "Vendor account name is incorrect");
		
		assertNotNull(vendor.getExternalAccountAddresses(), "Vendor addresses is null");
		assertEquals(1, vendor.getExternalAccountAddresses().size(), "Vendor address count is not correct");
		assertEquals(EXPECTED_ADDRESS_LINE_1, vendor.getExternalAccountAddresses().get(0).getAddressLine1(), "Vendor's address line 1s incorrect");
	}
		
	private ExtAccAddress createExtAccAddress() {
		ExtAccAddress address = new ExtAccAddress();
		address.setAddressCode("1");
		address.setAddressLine1("Address Line 1");
		address.setAddressLine2("Address Line 2");
		address.setAddressType("POST");
		address.setCountry("USA");
		address.setRegion("OH");
		address.setCountyCode("061");
		address.setTownCity("EVENDALE");
		address.setPostcode("45241");
		return address;
	}
}

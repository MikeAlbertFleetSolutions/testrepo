package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.VendorVO;

@SpringBootTest
public class VendorSuiteTalkServiceTest extends BaseTest{
	@Resource VendorSuiteTalkService vendorSuiteTalkService;
	
	@DisplayName("when requesting a vendor by id, then the correct vendor is returned")
	@Test
	public void testGet() throws Exception {
		final String VENDOR_INTERNAL_ID = "1186"; //00163453 AmeriFleet International Inc CAD

		VendorVO vendorVO = vendorSuiteTalkService.get(VENDOR_INTERNAL_ID, null);
		
		assertNotNull(vendorVO.getcId());
		assertNotNull(vendorVO.getAccountType());
		assertNotNull(vendorVO.getAccountCode());
		assertNotNull(vendorVO.getAccountName());
		assertNotNull(vendorVO.getShortName());
		assertNotNull(vendorVO.getAccStatus());
		//assertNotNull(vendorVO.getEmail()); 
		//assertNotNull(vendorVO.getTaxIdNum());
		assertNotNull(vendorVO.getRegName());
		assertNotNull(vendorVO.getEntityId());
		assertNotNull(vendorVO.getGroupCode());
		assertNotNull(vendorVO.getPaymentMethod());
		//assertNotNull(vendorVO.getPhone());
		//assertNotNull(vendorVO.getFax());
		assertNotNull(vendorVO.getOrgizationType());
		assertNotNull(vendorVO.getPaymentTerm());
		assertNotNull(vendorVO.getPayeeName());
		assertNotNull(vendorVO.getCurrencyCode());
		assertNotNull(vendorVO.getPaymentInd());
		assertNotNull(vendorVO.getUpfitInd());
		assertNotNull(vendorVO.getInternationalInd());
		assertNotNull(vendorVO.getWebQuotesReqCcApproval());
		assertNotNull(vendorVO.getWebQuotesReqFaApproval());
		assertNotNull(vendorVO.getTaxInd());
		assertNotNull(vendorVO.getBankName());
		assertNotNull(vendorVO.getBankAccountTypeId());
		assertNotNull(vendorVO.getBankAccountName());
		assertNotNull(vendorVO.getBankAccountNumber());
		assertNotNull(vendorVO.getBankSortCode());
		assertNotNull(vendorVO.getIsDeliveringDealer());
		//assertNotNull(vendorVO.getContactJobTitle());
		//assertNotNull(vendorVO.getContactFirstname());
		//assertNotNull(vendorVO.getContactLastName());
		assertNotNull(vendorVO.isInactive());
	} 

	@DisplayName("when requesting a vendor by id with no bank detail, then the correct vendor is returned")
	@Test
	public void testGetNoBankDetail() throws Exception {
		final String VENDOR_INTERNAL_ID = "19180"; //10001243 Metro Ford Inc.

		VendorVO vendorVO = vendorSuiteTalkService.get(VENDOR_INTERNAL_ID, null);
		
		assertNotNull(vendorVO.getcId());
		assertNotNull(vendorVO.getAccountType());
		assertNotNull(vendorVO.getAccountCode());
		assertNotNull(vendorVO.getAccountName());
		assertNotNull(vendorVO.getShortName());
		assertNotNull(vendorVO.getAccStatus());

		assertNull(vendorVO.getBankName());
		assertNull(vendorVO.getBankAccountTypeId());
		assertNull(vendorVO.getBankAccountName());
		assertNull(vendorVO.getBankAccountNumber());
		assertNull(vendorVO.getBankSortCode());
		

	}	

	@Disabled
	@Test
	public void testUpdateAddress() throws Exception {
		vendorSuiteTalkService.updateVendorExternalIdAndAddressExternalId("14890", "10000010", "28278", "UNIT-TEST");
	}
	
	@Disabled
	@Test
	public void testUpdateVendorExternalId() throws Exception {
		vendorSuiteTalkService.updateVendorExternalId("8081", "00088278");
	}	
}

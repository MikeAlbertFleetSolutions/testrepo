package com.mikealbert.accounting.processor.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorFieldEnum;

@SpringBootTest
public class InboundVendorDataHelperTest extends BaseTest{
	static final String Null_TAX_ID = null;
	static final String EMPTY_TAX_ID = null;
	static final String TAX_ID = "12-3456789";
	static final String TAX_ID_WITH_SPACES = "123 -456 - 789";	
	static final String DELIVERING_DEALER_TRUE = "T";
	static final String DELIVERING_DEALER_FALSE = "F";
	static final String ZIP_US = "12345-6789";
	static final String ZIP_CN = "V7J 2C1";
	
	@Test
	public void testConvertTaxId() {	
		String taxIdVendorWith, taxIdDeliveringDealerWith;
		
		Map<String, String> vendorWithTaxId = new HashMap<>();
		vendorWithTaxId.put(VendorFieldEnum.TAX_ID.getName(), TAX_ID);
		vendorWithTaxId.put(VendorFieldEnum.DELIVERING_DEALER.getName(), DELIVERING_DEALER_FALSE);				
		taxIdVendorWith = InboundVendorDataHelper.convertTaxId(vendorWithTaxId);

		Map<String, String> deliveringDealerWithTaxId = new HashMap<>();
		deliveringDealerWithTaxId.put(VendorFieldEnum.TAX_ID.getName(), TAX_ID);
		deliveringDealerWithTaxId.put(VendorFieldEnum.DELIVERING_DEALER.getName(), DELIVERING_DEALER_TRUE);				
		taxIdDeliveringDealerWith = InboundVendorDataHelper.convertTaxId(deliveringDealerWithTaxId);
		
		
		assertEquals("123456789", taxIdVendorWith);
		assertEquals("123456789", taxIdDeliveringDealerWith);		
	}	
	
	@Test
	public void testConvertTaxIdWithSpaces() {	
		String taxIdVendorWith, taxIdDeliveringDealerWith;
		
		Map<String, String> vendorWithTaxId = new HashMap<>();
		vendorWithTaxId.put(VendorFieldEnum.TAX_ID.getName(), TAX_ID_WITH_SPACES);
		vendorWithTaxId.put(VendorFieldEnum.DELIVERING_DEALER.getName(), DELIVERING_DEALER_FALSE);				
		taxIdVendorWith = InboundVendorDataHelper.convertTaxId(vendorWithTaxId);

		Map<String, String> deliveringDealerWithTaxId = new HashMap<>();
		deliveringDealerWithTaxId.put(VendorFieldEnum.TAX_ID.getName(), TAX_ID_WITH_SPACES);
		deliveringDealerWithTaxId.put(VendorFieldEnum.DELIVERING_DEALER.getName(), DELIVERING_DEALER_TRUE);				
		taxIdDeliveringDealerWith = InboundVendorDataHelper.convertTaxId(deliveringDealerWithTaxId);
		
		
		assertEquals("123456789", taxIdVendorWith);
		assertEquals("123456789", taxIdDeliveringDealerWith);		
	}		
	
	@Test
	public void testConvertZip() {
		String usZip, cnZip;
		
		Map<String, Object> usVendor = new HashMap<>();
		usVendor.put(VendorAddressFieldEnum.COUNTRY.getName(), "US");
		usVendor.put(VendorAddressFieldEnum.ZIP.getName(), ZIP_US);
		usZip = InboundVendorDataHelper.convertZip(usVendor);
		
		Map<String, Object> cnVendor = new HashMap<>();
		cnVendor.put(VendorAddressFieldEnum.COUNTRY.getName(), "CN");		
		cnVendor.put(VendorAddressFieldEnum.ZIP.getName(), ZIP_CN);
		cnZip = InboundVendorDataHelper.convertZip(cnVendor);
		
		assertEquals("12345", usZip);
		assertEquals(ZIP_CN, cnZip);		
	}
		
}

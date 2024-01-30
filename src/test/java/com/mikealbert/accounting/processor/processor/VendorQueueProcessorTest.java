package com.mikealbert.accounting.processor.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.VendorTestHelper;
import com.mikealbert.accounting.processor.client.suiteanalytics.VendorSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ExtAccAddressDAO;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.service.TaxJurisdictionService;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.accounting.processor.vo.VendorVO;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
@DisplayName("A vendor update")
class VendorQueueProcessorTest extends BaseTest {
	@Resource VendorQueueProcessor vendorQueueProcessor;
	@Resource CamelContext context;	

	@MockBean VendorSuiteAnalyticsService vendorSuiteAnalyticsService;
	@MockBean VendorSuiteTalkService vendorSuiteTalkService;
	@MockBean TaxJurisdictionService jurisdictionService;	
	@MockBean ExternalAccountDAO externalAccountDAO;
	@MockBean ExtAccAddressDAO extAccAddressDAO;
	
	static final String VENDOR_PAYLOAD = "{\"accountCode\":\"00000000\",\"accountName\":\"UNIT-TEST\",\"entityId\":\"7231\",\"vendorExtId\":\"00000000\"}";
	
	@Test
	@DisplayName("when update deletes the vendor's address that is linked to a vehicle movement, account is updated and exception is caught and suppressed")
	void testDeleteVehicleMovementAddress() throws Exception {
		VendorVO mockVendorVO = VendorTestHelper.createVendorVO();
		
		ArgumentCaptor<ExternalAccount> accountCaptor = ArgumentCaptor.forClass(ExternalAccount.class);		
		ExternalAccount expectedAccount = VendorTestHelper.generateAccount(2);
		
		List<Map<String, Object>> mockNSAddresses = VendorTestHelper.generateMockNSAddresses(1);
		mockNSAddresses.get(0).replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), "0");
		
		List<TaxJurisdictionVO> jurisdictions = new ArrayList<>();
		jurisdictions.add(VendorTestHelper.TAX_JURISDICTION);
				
		Exchange ex = new ExchangeBuilder(context)
				.withBody(VENDOR_PAYLOAD)
				.build();
		
		when(vendorSuiteAnalyticsService.getAddresses(anyString())).thenReturn(mockNSAddresses);
		when(vendorSuiteTalkService.get(any(), isNull())).thenReturn(mockVendorVO);
		when(externalAccountDAO.findById(any())).thenReturn(Optional.of(expectedAccount));
		when(externalAccountDAO.desencrypt(any())).thenReturn("");
		when(externalAccountDAO.getTaxJurisdiction(anyString(), anyString(), anyString(), anyString())).thenReturn(jurisdictions);
		when(jurisdictionService.find(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(VendorTestHelper.TAX_JURISDICTION);
		when(externalAccountDAO.save(accountCaptor.capture())).thenAnswer(i -> { 
			ExternalAccount ea = (ExternalAccount) i.getArguments()[0]; 			
			IntStream.range(0, ea.getExternalAccountAddresses().size()).forEach(idx -> {ea.getExternalAccountAddresses().get(idx).setEaaId(Long.valueOf(idx));});			
			return  ea;
		});		
		when(extAccAddressDAO.isLinkedToVehicleMovement(anyLong())).thenReturn(true);
			
		vendorQueueProcessor.process(ex);
		
		verify(extAccAddressDAO, times(0)).deleteById(anyLong());		
		
		assertNotNull(ex.getIn().getBody());
	}
}

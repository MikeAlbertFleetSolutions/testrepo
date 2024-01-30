package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.quote.ThisQuoteCostService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.LeaseSuiteTalkService;
import com.mikealbert.accounting.processor.dao.AssetItemDAO;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.QuotationDAO;
import com.mikealbert.accounting.processor.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelAccessoryDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.dao.QuotationStepStructureDAO;
import com.mikealbert.accounting.processor.entity.QuotationDealerAccessory;
import com.mikealbert.accounting.processor.entity.QuotationModel;
import com.mikealbert.accounting.processor.entity.QuotationModelAccessory;
import com.mikealbert.accounting.processor.vo.CapitalCostVO;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.constant.enumeration.ProductTypeEnum;


@SpringBootTest
public class LeaseServiceTest extends BaseTest{
	@Resource LeaseService leaseService;

	static final String QMD_ID = "12345";
	static final String CLIENT_INTERNAL_ID = "1";	
	static final String CLIENT_EXTERNAL_ID = "1C00000000";	
	static final CapitalCostVO capitalCostVO = new CapitalCostVO()
			.setTotalCostToPlaceInServiceDeal(BigDecimal.ONE)
			.setTotalCostToPlaceInServiceCustomer(BigDecimal.ONE);						

	@Resource RestTemplate restTemplate;
	
    @MockBean QuotationDAO quotationDAO;
	@MockBean QuotationModelDAO quotationModelDAO;
    @MockBean QuotationDealerAccessoryDAO quotationDealerAccessoryDAO;
    @MockBean QuotationModelAccessoryDAO quotationModelAccessoryDAO;
	@MockBean QuotationStepStructureDAO quotationStepStructureDAO;
    @MockBean AssetItemDAO assetItemDAO;
    @MockBean DocDAO docDAO;
    @MockBean LeaseSuiteTalkService leaseSuiteTalkService;
	@MockBean CustomerSuiteTalkService customerSuiteTalkService;
	@MockBean ThisQuoteCostService thisQuoteCostService;
    
    //@Mock JavaMailSender javaMailSender;

	ArgumentCaptor<LeaseVO> payloadCapture = ArgumentCaptor.forClass(LeaseVO.class);
	  
	@Test
    public void testGetLeaseRecord() throws Exception{
    	LeaseVO expectedLease = generateMockLease();

    	LeaseVO mockLease = generateMockLease();

		when(quotationDAO.findLease(any())).thenReturn(mockLease);
		when(quotationDAO.findLeaseAccountingSchedule(any(), any())).thenReturn(mockLease);

    	LeaseVO actualLease = leaseService.getLeaseRecord(QMD_ID);

		verify(quotationDAO, times(1)).findLease(eq(QMD_ID));
    	
		assertEquals(expectedLease, actualLease);
    }

	@Test
    public void testGetNewLeaseRecord() throws Exception{
		final Long EXPECTED_PARENT_CLN_ID = 0L;
		final String EXPECTED_PARENT_EXTERNAL_ID = "0-0";

    	LeaseVO expectedLease = generateMockLease()
				.setSubsidiary("4")
				.setExternalProductType("1")
				.setClientCapitalCost(BigDecimal.ONE)
				.setParentClnId(EXPECTED_PARENT_CLN_ID)
				.setParentExternalId(EXPECTED_PARENT_EXTERNAL_ID);

    	LeaseVO mockLease = generateMockLease()
				.setParentClnId(EXPECTED_PARENT_CLN_ID)
				.setExternalProductType(null);

		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findParentExternalIdByClnId(any())).thenReturn(EXPECTED_PARENT_EXTERNAL_ID);
		when(quotationDAO.findLeaseAccountingSchedule(any(), any())).thenReturn(mockLease);
    	when(assetItemDAO.getInitialValueOriginal(any(), any())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(any())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(any())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);

    	LeaseVO actualLease = leaseService.getNewLeaseRecord("12345");
    	
    	assertEquals(expectedLease.getExternalId(), actualLease.getExternalId(), "Unexpected lease record returned");    	
    	assertEquals(expectedLease.getExternalProductType(), actualLease.getExternalProductType(), "Incorrect Product Type");
    	assertEquals(expectedLease.getSubsidiary(), actualLease.getSubsidiary(), "Incorrect Company");
		assertEquals(expectedLease.getClientCapitalCost(), actualLease.getClientCapitalCost());
		assertEquals(expectedLease.getParentExternalId(), actualLease.getParentExternalId());
    }
    
	@Test
    public void testGetNovateLeaseRecord() throws Exception{
		LeaseVO expectedLease = generateMockLease()
				.setClientCapitalCost(BigDecimal.ONE);

    	LeaseVO mockLease = generateMockLease()
				.setExternalProductType(null);

		when(quotationDAO.findLease(any())).thenReturn(mockLease);
		when(quotationDAO.findLeaseAccountingSchedule(any(), any())).thenReturn(mockLease);
    	when(assetItemDAO.getInitialValueOriginal(any(), any())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(any())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(any())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);		

    	LeaseVO actualLease = leaseService.getNovateLeaseRecord("12345");
    	
		assertEquals(expectedLease.getClientExternalId(), actualLease.getClientExternalId());
		assertEquals(expectedLease.getClientCapitalCost(), actualLease.getClientCapitalCost());				
    }

	@Test
    public void testGetNovateLeaseRecordNullLease() throws Exception{

		when(quotationDAO.findLease(any())).thenReturn(null);

		assertNull(leaseService.getNovateLeaseRecord("12345"));
    }

    @Test
    public void testGetReviseLeaseRecord() throws Exception {
    	LeaseVO expectedLease = generateMockLease()
				.setSubsidiary("4")
				.setExternalProductType("1")
				.setClientCapitalCost(BigDecimal.ONE);

    	LeaseVO mockLease = generateMockLease()
				.setExternalProductType(null);

		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findParentExternalIdByQmdId(ArgumentMatchers.anyString())).thenReturn("10000000-1");
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);				
				
    	LeaseVO actualLease = leaseService.getReviseLeaseRecord("12345");
    	
    	assertEquals(expectedLease.getExternalId(), actualLease.getExternalId(), "Unexpected lease record returned");    	
    	assertEquals(expectedLease.getExternalProductType(), actualLease.getExternalProductType(), "Incorrect Product Type");
    	assertEquals(expectedLease.getSubsidiary(), actualLease.getSubsidiary(), "Incorrect Company");
		assertEquals(expectedLease.getClientExternalId(), actualLease.getClientExternalId());		
		assertEquals(expectedLease.getClientCapitalCost(), actualLease.getClientCapitalCost());						
    }

    @Test
    public void testGetAmendLeaseRecord() throws Exception {
    	LeaseVO expectedLease = generateMockLease()
				.setSubsidiary("4")
				.setExternalProductType("1")
				.setClientCapitalCost(BigDecimal.ZERO);

    	LeaseVO mockLease = generateMockLease()
				.setExternalProductType(null);

		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findParentExternalIdByQmdId(ArgumentMatchers.anyString())).thenReturn("10000000-1");
		when(quotationDAO.findPreviousQmdId(any())).thenReturn("0");	
		when(quotationDAO.getCustomerCapCostAmendment(any(), any())).thenReturn(BigDecimal.ZERO);
        when(quotationStepStructureDAO.findAllByQmdId(any())).thenReturn(new ArrayList<>(0));
		when(quotationModelDAO.findById(any())).thenReturn(Optional.of(new QuotationModel()));
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);
				
    	LeaseVO actualLease = leaseService.getAmendLeaseRecord("12345");
    	
    	assertEquals(expectedLease.getExternalId(), actualLease.getExternalId(), "Unexpected lease record returned");    	
    	assertEquals(expectedLease.getExternalProductType(), actualLease.getExternalProductType(), "Incorrect Product Type");
    	assertEquals(expectedLease.getSubsidiary(), actualLease.getSubsidiary(), "Incorrect Company");
		assertEquals(expectedLease.getClientExternalId(), actualLease.getClientExternalId());		
		assertEquals(expectedLease.getClientCapitalCost(), actualLease.getClientCapitalCost());						
    }	
    
    @Test
    @Disabled("If I create a Bean in BaseTest it results in error in few other tests. Also Disabled because over VPN I am not able to connect to mail server, I'll verify this in office")
    public void testAmendmentBeforeRevision() throws Exception {
    	LeaseVO mockLease = generateMockLease();
    	mockLease.setExternalProductType(null);
		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLease);
		
    	when(quotationDAO.isPriorAmendmentExist(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(Boolean.TRUE);

    	//SimpleMailMessage mail = new SimpleMailMessage();
    	//doNothing().when(javaMailSender).send(mail);
    	
    	mockLease = leaseService.getReviseLeaseRecord("12345");
    	
    	Assertions.assertTrue(mockLease.isAmendmentBeforeRevision());

}

    @Test
    public void testGetLeaseRecordWithVariablePayment() throws Exception{
    	LeaseVO expectedLease = generateMockLease();

    	LeaseVO mockLease = generateMockLease();
    	mockLease.setExternalProductType(null);
    	mockLease.setVariablePayment("True");
    	mockLease.setInterestType("LIBOR_30");

		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLease);
		when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(mockLease);
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);

    	LeaseVO actualLease = leaseService.getNewLeaseRecord("12345");
    	
    	assertEquals(expectedLease.getExternalId(), actualLease.getExternalId(), "Unexpected lease record returned");    	
    	assertEquals("3", actualLease.getVariableRateIndex(), "Incorrect Interest Type");    	    	
    }
    
    @Test
    public void testUpdatePrePayment() throws Exception {
    	LeaseVO expectedLease = generateMockLease();
    	LeaseVO mockLease = generateMockLease();
    	List<QuotationDealerAccessory> listQda = generateMockQuotationDealerAccessory();
    	List<QuotationModelAccessory> listQma = generateMockQuotationModelAccessory();
    	
    	mockLease.setPrePayment(mockLease.getCapitalContribution()
    			          .add(mockLease.getRechargeDealerAccessoryAmount())
    			          .add(mockLease.getRechargeModelAccessoryAmount()));

    	when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(quotationDealerAccessoryDAO.findByQmdId(ArgumentMatchers.anyLong())).thenReturn(listQda);
    	when(quotationModelAccessoryDAO.findByQmdId(ArgumentMatchers.anyLong())).thenReturn(listQma);
    	when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);

    	LeaseVO actualLease = leaseService.getNewLeaseRecord("12345");
    	
    	assertEquals(mockLease.getPrePayment(), actualLease.getPrePayment(), "Unexpected Capital Contribution found in lease record returned");
    }
    
    @Test
    public void testCeLeaseUpdateResidualAmount() throws Exception {
    	LeaseVO expectedLease = generateMockLease();
    	LeaseVO mockLease = generateMockLease();

    	when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);
    	
    	LeaseVO actualLease = leaseService.getNewLeaseRecord("12345");
    	
    	assertEquals(mockLease.getResidualValueEstimate(), actualLease.getResidualValueEstimate(), "Unexpected Residual Value found in close-end lease record returned");
    }
    
    @Test
    public void testOeLeaseUpdateResidualAmount() throws Exception {
    	LeaseVO expectedLease = generateMockLease();
    	LeaseVO mockLease = generateMockLease();
    	
    	expectedLease.setExternalProductType(ProductTypeEnum.OE.toString());
    	expectedLease.setProductCode(ProductEnum.OE_LTD.toString());

    	mockLease.setExternalProductType(ProductTypeEnum.OE.toString());
    	mockLease.setProductCode(ProductEnum.OE_LTD.toString());

    	when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(expectedLease);
    	when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
    	when(quotationDAO.getCustomerCapCost(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);
    	
    	LeaseVO actualLease = leaseService.getNewLeaseRecord("12345");
    	
    	assertEquals(mockLease.getResidualValueEstimate(), actualLease.getResidualValueEstimate(), "Unexpected Residual Value found in open-end lease record returned");
    }
    
    @Test
    public void testTerminateLease() throws Exception {
    	final String LEASE_PRODUCT_CODE = "CE_MAL";
    	
    	ArgumentCaptor<String> quoIdCaptor = ArgumentCaptor.forClass(String.class);    	
    	LeaseVO mockLease = generateMockLease();
    	String quoId = mockLease.getExternalId().replaceFirst("-.*", "");
    	
    	when(quotationDAO.getProductCodeByQuoId(ArgumentMatchers.anyLong())).thenReturn(LEASE_PRODUCT_CODE);
    	when(leaseSuiteTalkService.terminateLease(quoIdCaptor.capture())).thenReturn(new ArrayList<>(0));
    	
    	leaseService.terminateLease(quoId);
    	
    	assertEquals(quoId, quoIdCaptor.getValue());    	
    }
    
    @Test
    public void testTerminateLeaseWhereLeaseIsExcluded() throws Exception {
    	final String EXCLUDED_LEASE_PRODUCT_CODE = "ST";
    	
    	LeaseVO mockLease = generateMockLease();
    	String quoId = mockLease.getExternalId().replaceFirst("-.*", "");
    	
    	when(quotationDAO.getProductCodeByQuoId(ArgumentMatchers.anyLong())).thenReturn(EXCLUDED_LEASE_PRODUCT_CODE);
    	    	
    	List<Map<String, String>> terminatedLeases = leaseService.terminateLease(quoId);
    	
    	verify(leaseSuiteTalkService, times(0)).terminateLease(ArgumentMatchers.anyString());
    	
    	assertTrue(terminatedLeases.isEmpty());
    }    
    
    @Test
    public void testUpsertLease() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();
		
    	ClientVO mockClient = new ClientVO()
		        .setInternalId(CLIENT_INTERNAL_ID)
				.setExternalId(CLIENT_EXTERNAL_ID);

    	when(leaseSuiteTalkService.upsertLease(payloadCapture.capture())).thenReturn(mockLease.getExternalId());
		when(customerSuiteTalkService.getCustomer(any(), any(), anyBoolean())).thenReturn(mockClient);
    	
    	leaseService.upsertLease(mockLease);

		verify(customerSuiteTalkService, times(1)).getCustomer(isNull(), eq(mockClient.getExternalId()), eq(false));
    	
    	assertEquals(mockLease, payloadCapture.getValue());   
    } 
    
    @Test
    public void testAmendLease() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();

    	ClientVO mockClient = new ClientVO()
		        .setInternalId(CLIENT_INTERNAL_ID)
				.setExternalId(CLIENT_EXTERNAL_ID);		
    	
    	when(leaseSuiteTalkService.amendLease(payloadCapture.capture())).thenReturn(mockLease.getExternalId());
		when(customerSuiteTalkService.getCustomer(any(), any(), anyBoolean())).thenReturn(mockClient);
    	
    	leaseService.amendLease(mockLease);

		verify(customerSuiteTalkService, times(1)).getCustomer(isNull(), eq(mockClient.getExternalId()), eq(false));		
    	
    	assertEquals(mockLease, payloadCapture.getValue());    	
		assertTrue(mockLease.isAmendment());
    } 
    
    @Test
    public void testModifyLease() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();
    	
		ClientVO mockClient = new ClientVO()
				.setInternalId(CLIENT_INTERNAL_ID)
				.setExternalId(CLIENT_EXTERNAL_ID);

    	when(leaseSuiteTalkService.modifyLease(payloadCapture.capture())).thenReturn(mockLease.getExternalId());
		when(customerSuiteTalkService.getCustomer(any(), any(), anyBoolean())).thenReturn(mockClient);
    	
    	leaseService.modifyLease(mockLease);
    	
    	assertEquals(mockLease, payloadCapture.getValue());    	
    }   

	@DisplayName("when novation, then the appropriate call is made to novate then lease")
    @Test
    public void testNovateLease() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();
		List<LeaseVO> mockLeases = Arrays.asList(new LeaseVO[]{mockLease});

		ClientVO mockClient = new ClientVO()
				.setInternalId(CLIENT_INTERNAL_ID)
				.setExternalId(CLIENT_EXTERNAL_ID);
    	
		when(leaseSuiteTalkService.getLease(any())).thenReturn(mockLeases);
    	doNothing().when(leaseSuiteTalkService).novateLease(payloadCapture.capture());
		when(customerSuiteTalkService.getCustomer(any(), any(), anyBoolean())).thenReturn(mockClient);
    	
    	leaseService.novateLease(mockLease);
    	
		verify(customerSuiteTalkService, times(1)).getCustomer(isNull(), eq(CLIENT_EXTERNAL_ID), eq(false));

    	assertEquals(mockLease, payloadCapture.getValue()); 

    } 	

	@DisplayName("when update lease interest rate, then the appropriate call is made to update the interest rate on the lease")
    @Test
    public void testUpdateInterestRate() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();
		List<LeaseVO> mockLeases = Arrays.asList(new LeaseVO[]{mockLease});
   	
		when(leaseSuiteTalkService.getLease(any())).thenReturn(mockLeases);
    	doNothing().when(leaseSuiteTalkService).updateInterestRate(any());
    	
    	leaseService.updateInterestRate(mockLease);

		verify(leaseSuiteTalkService, times(1)).updateInterestRate(eq(mockLease));
    } 	

	@DisplayName("when update lease actual end date, then the appropriate call is made to update the actual end date on the lease")
    @Test
    public void testUpdateActualEndDate() throws Exception {    	
    	LeaseVO mockLease = generateMockLease();
		List<LeaseVO> mockLeases = Arrays.asList(new LeaseVO[]{mockLease});
   	
		when(leaseSuiteTalkService.getLease(any())).thenReturn(mockLeases);
    	doNothing().when(leaseSuiteTalkService).updateActualEndDate(any());
    	
    	leaseService.updateActualEndDate(mockLease);

		verify(leaseSuiteTalkService, times(1)).updateActualEndDate(eq(mockLease));
    } 

	@Test
	public void testNewLease() throws Exception{
		LeaseVO mockLeaseVO = generateMockLease();
		String qmdId = String.valueOf("715773");

		when(quotationDAO.findLease(ArgumentMatchers.anyString())).thenReturn(mockLeaseVO);
		when(quotationDAO.findLeaseAccountingSchedule(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenReturn(mockLeaseVO);
		when(quotationDAO.getCustomerCapCost(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.valueOf(10000l));
		when(assetItemDAO.getInitialValueOriginal(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(docDAO.getUnpaidPOTotal(ArgumentMatchers.anyLong())).thenReturn(BigDecimal.ZERO);
		when(thisQuoteCostService.thisQuoteCost(any())).thenReturn(capitalCostVO);

		LeaseVO leaseVO = leaseService.getNewLeaseRecord(qmdId);		
		assertEquals(mockLeaseVO, leaseVO);
	} 

	@Test
	public void testExternalLease() throws Exception {
		List<LeaseVO> mockLeases = new ArrayList<>(0);
		LeaseVO mockLease = generateMockLease();
		mockLease.getLeaseAccountingSchedule().add(new LeaseAccountingScheduleVO());

		mockLeases.add(mockLease);

		when(leaseSuiteTalkService.getLease(mockLease.getExternalId())).thenReturn(mockLeases);
		when(leaseSuiteTalkService.getSchedules(any())).thenReturn(mockLease.getLeaseAccountingSchedule());

		List<LeaseVO> actualLeases = leaseService.getExternalLease(mockLease.getExternalId(), true);

		verify(leaseSuiteTalkService, times(1)).getLease(ArgumentMatchers.eq(mockLease.getExternalId()));
		verify(leaseSuiteTalkService, times(1)).getSchedules(ArgumentMatchers.eq(mockLease));

		assertEquals(mockLeases, actualLeases);
		assertFalse(actualLeases.get(0).getLeaseAccountingSchedule().isEmpty());

	}
	
	private LeaseVO generateMockLease() {
		LeaseVO leaseVO = new LeaseVO();
		try {
			leaseVO.setExternalId("10000000-1");
			leaseVO.setName(leaseVO.getExternalId().toString());
			leaseVO.setProductCode("CE_LTD");
			leaseVO.setExternalProductType(ProductTypeEnum.CE.toString());
			leaseVO.setCommencementDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"));
			leaseVO.setEndDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2023"));
			leaseVO.setTerm(new BigDecimal(36));
			leaseVO.setLeaseAssetFairValue(null);
			leaseVO.setLeaseAssetCostCarrying(null);
			leaseVO.setCapitalContribution(BigDecimal.valueOf(3000));
			leaseVO.setRechargeDealerAccessoryAmount(BigDecimal.valueOf(1000));
			leaseVO.setRechargeModelAccessoryAmount(BigDecimal.valueOf(500));
			leaseVO.setResidualValueEstimate(BigDecimal.ZERO);
			leaseVO.setVariablePayment("False");
			leaseVO.setUnitNo("00000000");
			leaseVO.setSubsidiary("2");
			leaseVO.setQuoId(12345l);
			leaseVO.setClientExternalId(CLIENT_EXTERNAL_ID);
			leaseVO.setParentClnId(Long.valueOf(1440));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return leaseVO;		
	}

	private List<QuotationDealerAccessory> generateMockQuotationDealerAccessory() {
		List<QuotationDealerAccessory> listQda = new ArrayList<>();;
		try {
			QuotationDealerAccessory qda1 = new QuotationDealerAccessory();
			qda1.setDacDacId(123l);
			qda1.setDriverRechargeYn("Y");
			qda1.setRechargeAmount(BigDecimal.valueOf(1000));
			listQda.add(qda1);
			
			QuotationDealerAccessory qda2 = new QuotationDealerAccessory();
			qda2.setDacDacId(456l);
			qda2.setDriverRechargeYn("N");
			qda2.setRechargeAmount(BigDecimal.valueOf(500));
			listQda.add(qda2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listQda;		
	}
    
	private List<QuotationModelAccessory> generateMockQuotationModelAccessory() {
		List<QuotationModelAccessory> listQma = new ArrayList<>();;
		try {
			QuotationModelAccessory qma1 = new QuotationModelAccessory();
			qma1.setQmaId(123l);
			qma1.setDriverRechargeYn("Y");
			qma1.setRechargeAmount(BigDecimal.valueOf(500));
			listQma.add(qma1);
			
			QuotationModelAccessory qma2 = new QuotationModelAccessory();
			qma2.setQmaId(456l);
			qma2.setDriverRechargeYn("N");
			qma2.setRechargeAmount(BigDecimal.valueOf(1000));
			listQma.add(qma2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listQma;		
	}	
}
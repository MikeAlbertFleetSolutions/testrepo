package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.enumeration.LeaseFieldEnum;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;

@Disabled("Temp disable.  Need to figure out why it's failing")
@DisplayName("Given a lease")
@SpringBootTest
public class LeaseSuiteTalkServiceTest extends BaseTest{
	@Resource LeaseSuiteTalkService leaseSuiteTalkService;
	@Resource UnitSuiteTalkService unitSuiteTalkService;
	
	static String LEASE_TYPE_CE = "1";
	static String LEASE_TYPE_EQUIPMENT = "3";	
	static String LEASE_TYPE_OE = "3";

	static final String CLIENT_INTERNAL_ID = "13040";
	static final String QUO_ID = String.format("-%d-%d", System.currentTimeMillis(), 1);	
	static final String LEASE_TYPE = LEASE_TYPE_OE;	
	static final String CLASSIFICATION = "Operating";	
	static final String SUBSIDIARY = "4";	
	static final String CURRENCY = LeaseFieldEnum.CURRENCY.getInternalId();	
	static final Long TERM = 60l; 
	static final Calendar COMMENCEMENT_DATE = Calendar.getInstance();
	static final Calendar END_DATE = Calendar.getInstance();	
	static final Double ASSET_FAIR_VALUE = 33001.0D;
	static final Double ASSET_CARRYING_COST = 33001.0;
	static final Double PREPAYMENT = 2000.0;
	static final Double LESEE_RESIDUAL = 7750.25;
	static final Double THIRD_PARTY_RESIDUAL = 0.0;
	static final Double TOTAL_RESIDUAL = 7750.25;
	static final boolean VARIABLE_RATE_LEASE = true;
	static final String VARIABLE_RATE_INDEX = "1";	
	static final Double DEPOSIT_REFUNABLE_AMOUNT = 1000.0;
	static final boolean COLLECTIBILITY_PROBABLE = true;
	static final boolean LEASE_TRANSFER_OWNERSHIP = false;
	static final boolean PURCHASE_OPTION = false;
	static final boolean UNDERLYING_ASSET = false;
	static final String CLASS_BUSINESS_UNIT = LeaseFieldEnum.CLASS_BUSINESS_UNIT.getInternalId();	
	//static final String T_CATEGORY = LeaseFieldEnum.CATEGORY.getInternalId();	
	static final Double LEASE_PAYMENT_AMOUNT = 1001.23;
	static final Calendar LEASE_PAYMENT_DATE = Calendar.getInstance();
	static final Calendar IN_SERVICE_DATE = Calendar.getInstance();
	static final Calendar EFFECTIVE_DATE = Calendar.getInstance();	
	
	UnitVO mockUnit;
		
	@BeforeEach
	void putUnit() throws Exception {
		mockUnit = generateMockUnit();
		unitSuiteTalkService.putUnit(mockUnit);		
	}
	
	@AfterEach
	void deleteUnit() throws Exception {
		unitSuiteTalkService.deleteUnit(mockUnit);		
	}
	
	@Test
	public void testUpsertLease() throws Exception{		
		LeaseVO lease = null;
		Map<String, String> leaseMap;

		try {
			lease = generateMockLease();

			leaseSuiteTalkService.upsertLease(lease);
			leaseMap = leaseSuiteTalkService.getLease(lease.getExternalId(), 3);
		} finally {
			leaseSuiteTalkService.deleteLease(lease);
		}
				
		assertEquals(lease.getExternalId(), leaseMap.get(LeaseFieldEnum.EXTERNAL_ID.name()));
		assertTrue(lease.getLeaseAssetCostCarrying().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.ASSET_CARRING_COST.name()))) == 0);
		assertTrue(lease.getLeaseAssetFairValue().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.ASSET_FAIR_VALUE.name()))) == 0);
		assertEquals(lease.getLeaseTransferOwnership(), leaseMap.get(LeaseFieldEnum.LEASE_TRANSFER_OWNERSHIP.name()));
		assertEquals(lease.getBusinessUnit(), leaseMap.get(LeaseFieldEnum.CLASS_BUSINESS_UNIT.name()));
		//assertEquals(lease.getClassName(), leaseMap.get(LeaseFieldEnum.CATEGORY.name())); // TODO Where is the class name?
		assertEquals(lease.getCurrency(), leaseMap.get(LeaseFieldEnum.CURRENCY.name()));
		assertEquals(lease.getExternalProductType(), leaseMap.get(LeaseFieldEnum.LEASE_TYPE.name()));
		assertEquals(lease.getSubsidiary(), leaseMap.get(LeaseFieldEnum.SUBSIDIARY.name()));
		assertEquals(lease.getVariableRateIndex(), leaseMap.get(LeaseFieldEnum.VARIABLE_RATE_INDEX.name()));
		assertEquals(lease.getClientInternalId(), leaseMap.get(LeaseFieldEnum.CUSTOMER.name()));
		assertEquals(lease.isCbvImpact(), DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.CBV_IMPACT.name())));

		assertTrue(lease.getTerm().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.TERM.name()))) == 0);		
		assertTrue(lease.getPrePayment().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.PREPAYMENT.name()))) == 0);
		assertTrue(lease.getResidualValueGuranteeByLesee().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.LESEE_RESIDUAL.name()))) == 0);
		assertTrue(lease.getResidualValueGuaranteeBy3rdParty().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.THIRD_PARTY_RESIDUAL.name()))) == 0);	
		assertTrue(lease.getResidualValueEstimate().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.TOTAL_RESIDUAL.name()))) == 0);	
		assertTrue(lease.getDepositAmount().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.DEPOSIT_REFUNABLE_AMOUNT.name()))) == 0);
		assertTrue(DataUtil.convertToBoolean(lease.getVariablePayment()) == DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.VARIABLE_RATE_LEASE.name())));		
		assertTrue(DataUtil.convertToBoolean(lease.getCollectibilityProbable()) == DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.COLLECTIBILITY_PROBABLE.name())));
		assertTrue(DataUtil.convertToBoolean(lease.getLeaseTransferOwnership()) == DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.LEASE_TRANSFER_OWNERSHIP.name())));
		assertTrue(DataUtil.convertToBoolean(lease.getPurchaseOptionReasonablyCertain()) == DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.PURCHASE_OPTION.name())));
		assertTrue(DataUtil.convertToBoolean(lease.getUnderlyingAssestSpecialized()) == DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.UNDERLYING_ASSET.name())));		
		assertTrue(lease.getClientCapitalCost().compareTo(new BigDecimal(leaseMap.get(LeaseFieldEnum.CUSTOMER_CAP_COST.name()))) == 0);
		assertTrue(lease.getInterestRate().compareTo(Double.valueOf(leaseMap.get(LeaseFieldEnum.INTEREST_RATE.name()))) == 0);
		assertTrue(lease.getDepreciationFactor().compareTo(Double.valueOf(leaseMap.get(LeaseFieldEnum.DEPRECIATION_RATE.name()))) == 0);		
		
		//assertEquals(lease.getUnitNo(), leaseMap.get(LeaseFieldEnum.UNIT_NO.name()));
		//assertEquals(lease.getCommencementDate(), DataUtil.convertToDate(leaseMap.get(LeaseFieldEnum.COMMENCEMENT_DATE.name())));
		//assertEquals(lease.getInServiceDate(), DataUtil.convertToDate(leaseMap.get(LeaseFieldEnum.IN_SERVICE_DATE.name())));		
		
	}

	@DisplayName("when client changes on the lease, then the lease is updated with the new client")
	@Test
	public void testNovateLease() throws Exception {
		final String NEW_CLIENT_INTERNAL_ID = "13030";

		LeaseVO lease = generateMockLease();
		
		String leaseIntId = leaseSuiteTalkService.upsertLease(lease);

		lease 
		    .setInternalId(leaseIntId)
		    .setClientInternalId(NEW_CLIENT_INTERNAL_ID);

		leaseSuiteTalkService.novateLease(lease);

		lease = leaseSuiteTalkService.getLease(lease.getExternalId()).get(0);

		leaseSuiteTalkService.deleteLease(lease);

		assertEquals(NEW_CLIENT_INTERNAL_ID, lease.getClientInternalId());
	}
	
	@DisplayName("when request to update interest rate on lease, then the interest rate field on the lease is updated")
	@Test
	public void testUpdateInterestRate() throws Exception {
		final Double EXPECTED_INTEREST_RATE = 1.01D;

		LeaseVO lease = generateMockLease()
		        .setInterestRate(EXPECTED_INTEREST_RATE);
		
		try {
			String leaseIntId = leaseSuiteTalkService.upsertLease(lease);
	
			lease.setInternalId(leaseIntId);

			leaseSuiteTalkService.updateInterestRate(lease);			

			lease = leaseSuiteTalkService.getLease(lease.getExternalId()).get(0);
		} finally {
			leaseSuiteTalkService.deleteLease(lease);
		}

		assertEquals(EXPECTED_INTEREST_RATE, lease.getInterestRate());
	}	

	@DisplayName("when request to update actual end date on lease, then the actual end date field on the lease is updated")
	@Test
	public void testUpdateActualEndDate() throws Exception {
		final Date EXPECTED_ACTUAL_END_DATE = new Date();

		LeaseVO lease = generateMockLease();
		
		try {
			String leaseIntId = leaseSuiteTalkService.upsertLease(lease);
	
			lease
					.setInternalId(leaseIntId)
					.setActualEndDate(EXPECTED_ACTUAL_END_DATE);

			leaseSuiteTalkService.updateActualEndDate(lease);			

			lease = leaseSuiteTalkService.getLease(lease.getExternalId()).get(0);
		} finally {
			leaseSuiteTalkService.deleteLease(lease);
		}

		assertEquals(DateUtil.convertToDateString(EXPECTED_ACTUAL_END_DATE), DateUtil.convertToDateString(lease.getActualEndDate()));
	}

	@Test
	public void testAmendLease() throws Exception {
		Map<String, String> amendLeaseMap = null;

		LeaseVO lease = generateMockLease();

		String leaseId = leaseSuiteTalkService.upsertLease(lease);		

		LeaseVO amendLease = amendMockLease(generateMockLease())
				.setAmendment(true);

		leaseSuiteTalkService.amendLease(amendLease);

		try {
			amendLeaseMap = leaseSuiteTalkService.getLease(amendLease.getExternalId(), 3);
		} finally {
			leaseSuiteTalkService.deleteLease(amendLease);
			leaseSuiteTalkService.deleteLease(lease);
		}
			
		assertEquals(leaseId, amendLeaseMap.get(LeaseFieldEnum.LEASE_PAYMENT_PARENT.name()));				
		assertEquals(lease.getClientInternalId(), amendLeaseMap.get(LeaseFieldEnum.CUSTOMER.name()));	
		assertEquals(lease.getClientCapitalCost(), new BigDecimal(amendLeaseMap.get(LeaseFieldEnum.CUSTOMER_CAP_COST.name())));	
		assertEquals(lease.getInterestRate(), Double.valueOf(amendLeaseMap.get(LeaseFieldEnum.INTEREST_RATE.name())));
		assertEquals(amendLease.isCbvImpact(), DataUtil.convertToBoolean(amendLeaseMap.get(LeaseFieldEnum.CBV_IMPACT.name())));	
		assertTrue(amendLease.isAmendment());		
	}

	@Test
	public void testTerminateLease() throws Exception {
		LeaseVO lease = generateMockLease();
		
		String leaseId = leaseSuiteTalkService.upsertLease(lease);
		List<Map<String, String>> terminatedLeaseIds = leaseSuiteTalkService.terminateLease(lease.getExternalId());

		Map<String, String> leaseMap = leaseSuiteTalkService.getLease(lease.getExternalId(), 3);
		
		leaseSuiteTalkService.deleteLease(lease);

		assertTrue(DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.TERMINATION_FLAG.name())));
		assertEquals(leaseId, terminatedLeaseIds.get(0).get(LeaseFieldEnum.INTERNAL_ID.name()));
		assertEquals(lease.isCbvImpact(), DataUtil.convertToBoolean(leaseMap.get(LeaseFieldEnum.CBV_IMPACT.name())));					
	}
	
	@Test
	public void testTerminateLeaseWithBogusQuoId() {
		assertThrows(Exception.class, () -> {
			leaseSuiteTalkService.terminateLease("00000000");		
		});				
	}
	
	@Disabled("The lease must be in Commenced status to be modified. How to do this?")
	@Test
	public void testModifyLease() throws Exception {
		LeaseVO lease = generateMockLease();
		lease.getLeaseAccountingSchedule().clear();
		
		
		String leaseId = null;
		try {			
			leaseId = leaseSuiteTalkService.upsertLease(lease);
			lease.setInternalId(leaseId);
			lease.setTerm(new BigDecimal("60"));

			lease = leaseSuiteTalkService.getLease(lease.getExternalId()).get(0);

			lease.setClientInternalId("13030");
			lease.setClientExternalId(null);

			leaseSuiteTalkService.modifyLease(lease);
		} finally {
			leaseSuiteTalkService.deleteLease(lease);
		}
		
		//assertTrue(leaseMap.get("altName").contains("TERM"));
		assertEquals(leaseId, lease.getExternalId());
	}		
		
	@Disabled 
	@Test
	public void testDeleteLease() throws Exception {
		LeaseVO lease = generateMockLease();
		leaseSuiteTalkService.deleteLease(lease);
	}
	
	@Test
	public void testGetLease(){
		assertThrows(Exception.class, () -> {
			leaseSuiteTalkService.getLease(QUO_ID, 3);
		});
	}

	@Test
	public void testGetLeasePayment() throws Exception{	
		leaseSuiteTalkService.getLeasePayments();
	}

	@Test
	public void testDeleteCurrentAndFutruePaymentSchedules() throws Exception {
		LeaseVO lease = generateMockLease();
		leaseSuiteTalkService.upsertLease(lease);
		
		try {
			leaseSuiteTalkService.deleteCurrentAndFutruePaymentSchedules(lease);
		} finally {
			leaseSuiteTalkService.deleteLease(lease);			
		}
		
	}

	@Test
	public void testGetLease2() throws Exception {
		LeaseVO mockLease = generateMockLease();
		mockLease.getLeaseAccountingSchedule().clear();
		
		leaseSuiteTalkService.upsertLease(mockLease);
		
		try {
			LeaseVO lease = leaseSuiteTalkService.getLease(mockLease.getExternalId()).get(0);

			assertNotNull(lease.getInternalId());
			
			assertEquals(mockLease.getExternalId(), lease.getExternalId());
			assertEquals(mockLease.getName(), lease.getName());
			assertEquals(mockLease.getExternalProductType(), lease.getExternalProductType());
			//assertEquals(mockLease.getClassification(), lease.getClassification()); //Can't test this as NS will change the value, see NS workflow
			assertEquals(mockLease.getSubsidiary(), lease.getSubsidiary());
			assertEquals(mockLease.getCurrency(), lease.getCurrency());
			assertEquals(DateUtil.convertToDateString(mockLease.getCommencementDate()), DateUtil.convertToDateString(lease.getCommencementDate()));
			assertEquals(DateUtil.convertToDateString(mockLease.getEndDate()), DateUtil.convertToDateString(lease.getEndDate()));
			assertEquals(DateUtil.convertToDateString(mockLease.getInServiceDate()), DateUtil.convertToDateString(lease.getInServiceDate()));
            assertEquals(0, mockLease.getLeaseAssetFairValue().compareTo(lease.getLeaseAssetFairValue()));
            assertEquals(0, mockLease.getLeaseAssetCostCarrying().compareTo(lease.getLeaseAssetCostCarrying()));
            assertEquals(0, mockLease.getCapitalContribution().compareTo(lease.getCapitalContribution()));
            assertEquals(0, mockLease.getPrePayment().compareTo(lease.getPrePayment()));
            assertEquals(0, mockLease.getResidualValueGuaranteeBy3rdParty().compareTo(lease.getResidualValueGuaranteeBy3rdParty()));
            assertEquals(0, mockLease.getResidualValueGuranteeByLesee().compareTo(lease.getResidualValueGuranteeByLesee()));
            assertEquals(0, mockLease.getResidualValueEstimate().compareTo(lease.getResidualValueEstimate()));						
            assertEquals(0, mockLease.getDepositAmount().compareTo(lease.getDepositAmount()));								
			assertEquals(mockLease.getLeaseAccountingSchedule(), lease.getLeaseAccountingSchedule());	
			assertEquals(mockLease.getVariablePayment(), lease.getVariablePayment());
			assertEquals(mockLease.getVariableRateIndex(), lease.getVariableRateIndex());
			assertEquals(mockLease.getUnitNo(), lease.getUnitNo());	
			assertEquals(mockLease.getCollectibilityProbable(), lease.getCollectibilityProbable());			
			assertEquals(mockLease.getLeaseTransferOwnership(), lease.getLeaseTransferOwnership());
			assertEquals(DataUtil.convertToBoolean(mockLease.getPurchaseOptionReasonablyCertain()), DataUtil.convertToBoolean(lease.getPurchaseOptionReasonablyCertain()));
			assertEquals(mockLease.getUnderlyingAssestSpecialized(), lease.getUnderlyingAssestSpecialized());
			//assertEquals(mockLease.getBusinessUnit(), lease.getBusinessUnit()); //Can't test this as NS will change the value, see NS workflow
			//assertEquals(mockLease.getClassName(), lease.getClassName()); //Can't test this as NS will change the value, see NS workflow
			assertEquals(mockLease.getInterestType(), lease.getInterestType());	
			assertEquals(mockLease.getClientInternalId(), lease.getClientInternalId());	

		    assertNull(lease.getInterestType());
			assertNull(lease.getEffectiveDate());
			assertNull(lease.getRechargeDealerAccessoryAmount());
			assertNull(lease.getRechargeModelAccessoryAmount());

			assertFalse(lease.isAmendmentBeforeRevision());


		} finally {
			leaseSuiteTalkService.deleteLease(mockLease);
		}
		
	}	

	@Test
	public void testGetSchedules() throws Exception {
		LeaseVO leaseVO = new LeaseVO();
		leaseVO.setInternalId("53");

		List<LeaseAccountingScheduleVO> schedules = leaseSuiteTalkService.getSchedules(leaseVO);

		assertTrue(schedules.size() > 0);
	}	
	
	private LeaseVO generateMockLease() {
	    Calendar cal = Calendar.getInstance();
	    
		LeaseVO lease = new LeaseVO()
		        .setClientInternalId(CLIENT_INTERNAL_ID)
				.setExternalId(QUO_ID)
				.setName(QUO_ID)
				.setExternalProductType(LEASE_TYPE)
				.setClassification(CLASSIFICATION)
				.setSubsidiary(SUBSIDIARY)
				.setCurrency(CURRENCY)
				.setTerm(new BigDecimal(TERM))
				.setCommencementDate(COMMENCEMENT_DATE.getTime())
				.setEndDate(END_DATE.getTime())
				.setLeaseAssetFairValue(new BigDecimal(ASSET_FAIR_VALUE))
				.setLeaseAssetCostCarrying(new BigDecimal(ASSET_CARRYING_COST))
				.setPrePayment(new BigDecimal(PREPAYMENT))
				.setResidualValueEstimate(new BigDecimal(LESEE_RESIDUAL))
				.setResidualValueGuaranteeBy3rdParty(new BigDecimal(THIRD_PARTY_RESIDUAL))
				.setResidualValueEstimate(new BigDecimal(TOTAL_RESIDUAL))
				.setVariablePayment(Boolean.toString(VARIABLE_RATE_LEASE))
				.setVariableRateIndex(VARIABLE_RATE_INDEX)
				.setCollectibilityProbable(Boolean.toString(COLLECTIBILITY_PROBABLE))
				.setLeaseTransferOwnership(Boolean.toString(LEASE_TRANSFER_OWNERSHIP))
				.setPurchaseOptionReasonablyCertain(Boolean.toString(PURCHASE_OPTION))
				.setUnderlyingAssestSpecialized(Boolean.toString(UNDERLYING_ASSET))
				.setBusinessUnit(CLASS_BUSINESS_UNIT)
		      //.setClassName(T_CATEGORY);
				.setUnitNo(mockUnit.getUnitNo())
				.setDepositAmount(new BigDecimal(DEPOSIT_REFUNABLE_AMOUNT))
				.setInServiceDate(IN_SERVICE_DATE.getTime())
				.setEffectiveDate(EFFECTIVE_DATE.getTime())
				.setClientCapitalCost(new BigDecimal("1.01"))
				.setInterestRate(1.2345D)
				.setDepreciationFactor(1.2345D);
		
		LeaseAccountingScheduleVO schedule = new LeaseAccountingScheduleVO();
		schedule.setAmount(new BigDecimal(LEASE_PAYMENT_AMOUNT));
		schedule.setTransDate(cal.getTime() );
		
		lease.getLeaseAccountingSchedule().add(schedule);
		
		return lease;
	}

	private LeaseVO amendMockLease(LeaseVO lease) {
		String[] array = lease.getExternalId().split("-");
		lease.setParentExternalId(lease.getExternalId());
		lease.setExternalId(String.format("-%s-%d", array[array.length - 2], Integer.valueOf(array[array.length - 1]) + 1));
		lease.setName(lease.getExternalId());
        lease.setLeaseAssetFairValue(new BigDecimal(ASSET_FAIR_VALUE).multiply(new BigDecimal(0.1)));
        lease.setLeaseAssetCostCarrying(lease.getLeaseAssetFairValue());
        lease.setResidualValueEstimate(lease.getLeaseAssetCostCarrying().multiply(new BigDecimal(0.5)));
        
		LeaseAccountingScheduleVO schedule = new LeaseAccountingScheduleVO();
		schedule.setAmount(new BigDecimal(LEASE_PAYMENT_AMOUNT).multiply(new BigDecimal(0.1)));
		schedule.setTransDate(COMMENCEMENT_DATE.getTime() );
		
		lease.getLeaseAccountingSchedule().clear();
		lease.getLeaseAccountingSchedule().add(schedule);

		lease.setCbvImpact(true);
		
		return lease;
	}
}

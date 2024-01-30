package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.quote.ThisQuoteCostService;
import com.mikealbert.accounting.processor.client.suiteanalytics.LeaseSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.LeaseSuiteTalkService;
import com.mikealbert.accounting.processor.dao.AssetItemDAO;
import com.mikealbert.accounting.processor.dao.ContractLineDAO;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.dao.QuotationDAO;
import com.mikealbert.accounting.processor.dao.QuotationDealerAccessoryDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelAccessoryDAO;
import com.mikealbert.accounting.processor.dao.QuotationModelDAO;
import com.mikealbert.accounting.processor.dao.QuotationStepStructureDAO;
import com.mikealbert.accounting.processor.entity.QuotationDealerAccessory;
import com.mikealbert.accounting.processor.entity.QuotationModel;
import com.mikealbert.accounting.processor.entity.QuotationModelAccessory;
import com.mikealbert.accounting.processor.entity.QuotationStepStructure;
import com.mikealbert.accounting.processor.exception.NoDataFoundException;
import com.mikealbert.accounting.processor.exception.RetryableException;
import com.mikealbert.accounting.processor.exception.RetryableSuiteTalkException;
import com.mikealbert.accounting.processor.vo.AmendmentLeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.CapitalCostVO;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseTerminationRequestVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.constant.enumeration.ProductTypeEnum;
import com.mikealbert.util.data.DateUtil;


@Service("leaseService")
public class LeaseServiceImpl extends BaseService implements LeaseService {	
	@Resource AssetItemDAO assetItemDao;
	@Resource DocDAO docDao;
	@Resource XRefService xRefService;
	@Resource QuotationDAO quotationDao;
	@Resource QuotationModelDAO quotationModelDao;
	@Resource QuotationStepStructureDAO quotationStepStructureDao;
	@Resource QuotationDealerAccessoryDAO quotationDealerAccessoryDao;
	@Resource QuotationModelAccessoryDAO quotationModelAccessoryDao;
	@Resource LeaseSuiteTalkService leaseSuiteTalkService;
	@Resource ContractLineDAO contractLineDao;
	@Resource LeaseSuiteAnalyticsService leaseSuiteAnalyticsService;
	@Resource CustomerSuiteTalkService customerSuiteTalkService;
	@Resource ThisQuoteCostService thisQuoteCostService;

	
	@Value("${mafs.business.exception.email}")
	String emailTo;
		
	final Logger LOG = LogManager.getLogger(this.getClass());
	
	/**
	 * Gets plain internal lease record without context specific updates
	 * @param qmdId
	 */
	@Override
	public LeaseVO getLeaseRecord(String qmdId) throws Exception {
		LeaseVO leaseVO = quotationDao.findLease(qmdId);

		if(leaseVO == null) throw new NoDataFoundException("Could not find lease for qmdId: " + qmdId);
		
		leaseVO = quotationDao.findLeaseAccountingSchedule(leaseVO, qmdId);
								
		this.validate(leaseVO);
		
		return leaseVO;
	}

	@Override
	public LeaseVO getNewLeaseRecord(String qmdId) throws Exception {
		LOG.info("Getting new lease information for qmdId: " + qmdId);
		LeaseVO leaseVO = quotationDao.findLease(qmdId);

		if(leaseVO.getParentClnId() != null)
			leaseVO.setParentExternalId(quotationDao.findParentExternalIdByClnId(leaseVO.getParentClnId()));		

		leaseVO = quotationDao.findLeaseAccountingSchedule(leaseVO, qmdId);
				
		leaseVO = updateRechargeDealerAccessory(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateRechargeModelAccessory(leaseVO, Long.valueOf(qmdId));
		leaseVO = updatePrePayment(leaseVO);
		leaseVO = updateFairValueOriginal(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateCarryingCostOriginal(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateResidualValueGuranteeByLesee(leaseVO);		
		leaseVO = updateVariableRateIndex(leaseVO);		
		leaseVO = updateCompany(leaseVO); 
		leaseVO = updateCurrency(leaseVO);		
		leaseVO = updateProductType(leaseVO); //Updating the Product Type at the end because it changes the Type to Description from XRef table
		leaseVO = updateCapitalCost(leaseVO, Long.valueOf(qmdId));
				
		this.validate(leaseVO);
		
		return leaseVO;
	}

	@Override
	public LeaseVO getNovateLeaseRecord(String qmdId) throws Exception {
		LOG.info("Getting Novate lease information for qmdId: " + qmdId);

		LeaseVO leaseVO = quotationDao.findLease(qmdId);

		if(leaseVO != null) {
			leaseVO = quotationDao.findLeaseAccountingSchedule(leaseVO, qmdId);					
			leaseVO = updateRechargeDealerAccessory(leaseVO, Long.valueOf(qmdId));
			leaseVO = updateRechargeModelAccessory(leaseVO, Long.valueOf(qmdId));
			leaseVO = updatePrePayment(leaseVO);
			leaseVO = updateFairValueOriginal(leaseVO, Long.valueOf(qmdId));
			leaseVO = updateCarryingCostOriginal(leaseVO, Long.valueOf(qmdId));
			leaseVO = updateResidualValueGuranteeByLesee(leaseVO);		
			leaseVO = updateVariableRateIndex(leaseVO);		
			leaseVO = updateCompany(leaseVO); 
			leaseVO = updateCurrency(leaseVO);		
			leaseVO = updateProductType(leaseVO); //Updating the Product Type at the end because it changes the Type to Description from XRef table
			leaseVO = updateCapitalCost(leaseVO, Long.valueOf(qmdId));
					
			this.validate(leaseVO);
		}
		
		return leaseVO;
	}		
	
	@Override
	public LeaseVO getReviseLeaseRecord(String qmdId) throws Exception {
		LOG.info("Getting Revise lease information for qmdId: " + qmdId);
		LeaseVO leaseVO = quotationDao.findLease(qmdId);
						
		/*
		 * Check if an Amendment exists, if so don't do anything just send an information email to business
		 * This is a special case that happens in-frequently.
		 * Per LAFS-136/137 It was decided that it will be handled manually 
		 */
		Boolean priorAmendExist = quotationDao.isPriorAmendmentExist(qmdId, leaseVO.getQuoId());
		if(priorAmendExist) {
			String msgBody = "This " + leaseVO.getInternalProductType() + " lease was revised but there is an existing amendment lease. Please update the lease manually.";
			this.sendtextEmail(emailTo, "Contract Revision for Unit: " + leaseVO.getUnitNo(), msgBody);
			leaseVO.setAmendmentBeforeRevision(Boolean.TRUE);
			return leaseVO;
		}
		
		//Changing externalId to be the original lease. In case of Revision we update original lease.
		leaseVO.setExternalId(quotationDao.findParentExternalIdByQmdId(qmdId));
		leaseVO.setName(leaseVO.getExternalId());
		
		leaseVO = quotationDao.findLeaseAccountingSchedule(leaseVO, qmdId);
		
		leaseVO = updateRechargeDealerAccessory(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateRechargeModelAccessory(leaseVO, Long.valueOf(qmdId));
		leaseVO = updatePrePayment(leaseVO);
		leaseVO = updateFairValueRevision(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateCarryingCostRevision(leaseVO, Long.valueOf(qmdId));
		leaseVO = updateResidualValueGuranteeByLesee(leaseVO);		
		leaseVO = updateVariableRateIndex(leaseVO);		
		leaseVO = updateCompany(leaseVO); 
		leaseVO = updateCurrency(leaseVO);		
		leaseVO = updateProductType(leaseVO); //Updating the Product Type at the end because it changes the Type to Description from XRef table
		leaseVO = updateCapitalCost(leaseVO, Long.valueOf(qmdId));
		
		this.validate(leaseVO);		
		return leaseVO;
	}
	
	@Override
	public LeaseVO getAmendLeaseRecord(String qmdId) throws Exception {
		LOG.info("Getting Amended lease information for qmdId: " + qmdId);
		
		LeaseVO leaseVO = quotationDao.findLease(qmdId);
		
		leaseVO.setParentExternalId(quotationDao.findParentExternalIdByQmdId(qmdId));

		//qmdId of the record which was quoteStatus 6 just before amendment. This will be used for comparison to find out what was added as part of amendment.
		String previousQmdId = quotationDao.findPreviousQmdId(qmdId);
				
		BigDecimal totalResidual = BigDecimal.ZERO;				//Total residual of the added dealer accessories
		BigDecimal totalMonthlyLeaseAmount = BigDecimal.ZERO;	//Total delta on monthly lease rate of the added dealer accessories
		BigDecimal totalRechargeAmount = BigDecimal.ZERO;		//Total re-charge amount of the added dealer accessories
		//BigDecimal totalCostOfEquipment = BigDecimal.ZERO;		//Total cost of equipment added
		Boolean rechargeEquipPresent = Boolean.FALSE;			//Initialize variables to False
		Boolean nonRechargeEquipPresent = Boolean.FALSE;		//Initialize variables to False
		Boolean stepLease = Boolean.FALSE;						//Initialize variables to False
		List<BigDecimal> nonRechargeAmendQel = new ArrayList<>();
		
		leaseVO = updateVariableRateIndex(leaseVO);
		
		List<QuotationStepStructure> quotationStepStructure;
		quotationStepStructure =  quotationStepStructureDao.findAllByQmdId(new BigDecimal(qmdId));
		if (quotationStepStructure.size() > 1) {
			stepLease = Boolean.TRUE;
		}
		
		//Get Amendment QuotationModel Record
		Optional<QuotationModel> amendedQmdRec = quotationModelDao.findById(Long.valueOf(qmdId));
		if (amendedQmdRec.isEmpty()) {
			throw new Exception("Unable to find QUOTATION_MODEL record for qmdId: " + qmdId);
		}

		//Update Amendment specific columns
		leaseVO = updateTerm(leaseVO, amendedQmdRec);
		leaseVO = updateLeaseCommencementDate(leaseVO, amendedQmdRec);
		
		
		List<AmendmentLeaseAccountingScheduleVO> amendmentleaseAcctSchedules = quotationDao.getNonRechargeAmendLeaseAccountingSchedule(qmdId, previousQmdId);
		amendmentleaseAcctSchedules.addAll(quotationDao.getRechargeAmendLeaseAccountingSchedule(qmdId, previousQmdId));
		
		for (AmendmentLeaseAccountingScheduleVO amendmentSchedule : amendmentleaseAcctSchedules) {
			//totalCostOfEquipment = totalCostOfEquipment.add(amendmentSchedule.getRechargeAmount());
			//Recharge Indicator 
			if("Y".equals(amendmentSchedule.getRechargeInd())) {
				totalRechargeAmount = totalRechargeAmount.add(amendmentSchedule.getRechargeAmount());
				rechargeEquipPresent = Boolean.TRUE;
			}
			else { //Non Recharge Equipment
				totalMonthlyLeaseAmount = totalMonthlyLeaseAmount.add(amendmentSchedule.getMonthlyLeaseAmount());
				totalResidual = totalResidual.add(amendmentSchedule.getResidual());
				nonRechargeEquipPresent = Boolean.TRUE;
				nonRechargeAmendQel.add(amendmentSchedule.getQelId());
			}
		}
			
		//No added equipment is recharged
		if(!rechargeEquipPresent && nonRechargeEquipPresent) {
			leaseVO.getLeaseAccountingSchedule().add(new LeaseAccountingScheduleVO(totalMonthlyLeaseAmount, leaseVO.getCommencementDate()));
		}
		//All of added equipment is recharged
		else if (rechargeEquipPresent && !nonRechargeEquipPresent) {
			//Creating First Record with Recharge Amount
			leaseVO.getLeaseAccountingSchedule().add(new LeaseAccountingScheduleVO(totalRechargeAmount, leaseVO.getCommencementDate()));

			//Creating Second Record with 0 Amount
			leaseVO.getLeaseAccountingSchedule().add(getZeroDollorNextMonthSchedule(leaseVO.getCommencementDate()));
		}
		//Some added equipment is recharged and some is not
		else if (rechargeEquipPresent && nonRechargeEquipPresent) {
			//Creating First Record which includes Recharge Amount and Non Recharge amount
			leaseVO.getLeaseAccountingSchedule().add(new LeaseAccountingScheduleVO(totalRechargeAmount.add(totalMonthlyLeaseAmount), leaseVO.getCommencementDate()));

			//Creating Second Record with just Monthly Recharge Amount
			leaseVO.getLeaseAccountingSchedule().add(new LeaseAccountingScheduleVO(totalMonthlyLeaseAmount,DateUtil.addMonths(leaseVO.getCommencementDate(), 1l)));
		}
		
		//In case we have a Stepped Lease and we are adding a non recharge equipment then Add rest of the schedules as well to the lease
		if (nonRechargeEquipPresent && stepLease) {
			leaseVO.getLeaseAccountingSchedule().addAll(quotationDao.getRemainingStepSchedule(qmdId, nonRechargeAmendQel, totalMonthlyLeaseAmount));
		}
		
		leaseVO.setPrePayment(BigDecimal.ZERO); //Incase of Amendments PrePayment is set to Zero
		leaseVO = updateFairValueAmendment(leaseVO, Long.valueOf(qmdId), Long.valueOf(previousQmdId), totalRechargeAmount);
		leaseVO.setLeaseAssetCostCarrying(leaseVO.getLeaseAssetFairValue());

		leaseVO = updateResidualAmount(leaseVO, totalResidual);
		leaseVO = updateResidualValueGuranteeByLesee(leaseVO); //This needs to be called after updateResidualAmount
		leaseVO = updateCompany(leaseVO);
		leaseVO = updateCurrency(leaseVO);				
		leaseVO = updateProductType(leaseVO); //Updating the Product Type at the end because it changes the Type to Description from XRef table
		leaseVO = updateAmendmentCapitalCost(leaseVO, Long.valueOf(qmdId));
		
		this.validate(leaseVO);
		return leaseVO;
	}
		
	@Override
	public List<LeaseTerminationRequestVO> initializeLeaseTerminationRequests(List<Long> clnIds) throws Exception {       
        return quotationDao.findQuoIdsByClnIds(clnIds).stream()
        		.map(quoId -> new LeaseTerminationRequestVO(quoId))
        		.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, String>> terminateLease(String quoId) throws Exception {
		List<Map<String, String>> terminatedLeases;
		
		/** Until the business can figure out what to do...exclude leases 
		    for product code: ST, DEMO or INV_EXCLUS. At the time of writing, 
		    the aforementioned type leases do not exist in the accounting system. 
		    Sending them to the accounting system will result in an error/HD ticket.
		 */
		String productCode = quotationDao.getProductCodeByQuoId(Long.valueOf(quoId));
		switch(ProductEnum.valueOf(productCode)) {
		    case DEMO:
		    case ST:
		    case INV_EXCLUS:
		    	LOG.warn("Cannot terminate in accounting system because quoId {} product code is {}", quoId, productCode);
				terminatedLeases = new ArrayList<>(0);
			    break;
			default:
				terminatedLeases = leaseSuiteTalkService.terminateLease(quoId);
		}
		
		return terminatedLeases;
	}

	@Override
	public String upsertLease(LeaseVO lease) throws Exception {
		return leaseSuiteTalkService.upsertLease(enrichWithClient(lease));
	}

	@Override
	public String amendLease(LeaseVO lease) throws Exception {
		return leaseSuiteTalkService.amendLease(enrichWithClient(lease.setAmendment(true)));
	}

	@Override
	public String modifyLease(LeaseVO lease) throws Exception {
		return leaseSuiteTalkService.modifyLease(enrichWithClient(lease));
	}

	@Override
	public void novateLease(LeaseVO lease) throws Exception {
		String leaseExtId = lease.getExternalId();

		if(leaseExtId.contains("-")) {
			leaseExtId = leaseExtId.split("-")[0];
		}

		leaseSuiteTalkService.getLease(leaseExtId).stream()
		        .map(l -> lease.setInternalId(l.getInternalId()))
		        .forEach(l -> {
					try {
						leaseSuiteTalkService.novateLease(enrichWithClient(l));
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});		
	}	

	/**
	 * Updates the interest rate on all lease associated with the quote in the accounting system.
	 * 
	 * @param lease Really, just the external id property is used.
	 */
	@Override
	public void updateInterestRate(LeaseVO lease) throws Exception {
		String leaseExtId = lease.getExternalId();

		if(leaseExtId.contains("-")) {
			leaseExtId = leaseExtId.split("-")[0];
		}

		leaseSuiteTalkService.getLease(leaseExtId).stream()
		        .map(l -> lease.setInternalId(l.getInternalId()))
		        .forEach(l -> {
					try {
						leaseSuiteTalkService.updateInterestRate(l);
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});		
	}
	
	/**
	 * Updates the actual end date on all lease associated with the quote in the accounting system.
	 * 
	 * @param lease Really, just the external id property is used.
	 */
	@Override
	public void updateActualEndDate(LeaseVO lease) throws Exception {
		String leaseExtId = lease.getExternalId();

		if(leaseExtId.contains("-")) {
			leaseExtId = leaseExtId.split("-")[0];
		}

		leaseSuiteTalkService.getLease(leaseExtId).stream()
		        .map(l -> lease.setInternalId(l.getInternalId()))
		        .forEach(l -> {
					try {
						leaseSuiteTalkService.updateActualEndDate(l);
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				});		
	}	
	
	/**
	 * @author Saket.Maheshwary
	 * @return A list of map which has details of the leases.
	 */
	@Override
	public List<Map<String, Object>> getExternalLeases() {
		return leaseSuiteAnalyticsService.getAllExternalLeases();		
	}
	
	/**
	 * @author Saket.Maheshwary
	 * @param externalId. This is externalId of the lease that you wish to pull. * means to pull all leases
	 * @return A list of map which has details of the leases. In case you are pulling details of a single externalId, list will have a single record
	 */
	@Override
	public List<Map<String, Object>> getExternalLeases(String externalId) {
		return leaseSuiteAnalyticsService.getExternalLeaseByExternalId(externalId);
	}

	/**
	 * Retrieves the lease(s)from the external system based on the externalId including or excluding the dash extension.
	 * @param String externalId of the lease in the external systemt
	 * @param boolean loadSchedule when set to TRUE, the schedules are loaded
	 * @return List<LeaseVO> The lease(s) from the external system
	 */
	@Override
	public List<LeaseVO> getExternalLease(String externalId, boolean loadSchedule) throws Exception {
		List<LeaseVO> leases = leaseSuiteTalkService.getLease(externalId);

		if(loadSchedule) {
			leases = leases.stream().parallel()
			        .map(lease -> {
						try {
							lease.setLeaseAccountingSchedule(leaseSuiteTalkService.getSchedules(lease));
							return lease;
						} catch(Exception e){
							throw new RuntimeException(e);
						} 
					})
					.collect(Collectors.toList());
		}

		return leases;
	}
	
	private void validate(LeaseVO leaseVO) throws Exception {
		if(leaseVO == null) {
			throw new Exception("Lease record could not be generated from the quote");
		} else {
			super.validate(leaseVO);
		}
	}
	
	private LeaseVO updateVariableRateIndex(LeaseVO lease) throws Exception {
		if(lease.getVariablePayment().equalsIgnoreCase("TRUE")) {
			String interestType = xRefService.getExternalValue(XRefGroupNameEnum.INTEREST_TYPE, lease.getInterestType());
			lease.setVariableRateIndex(interestType);
		}
		
		return lease;
	}

	//Used in Amend Lease 
	private LeaseVO updateResidualAmount(LeaseVO lease, BigDecimal residualAmout) {
		if (ProductTypeEnum.OE.name().equals(lease.getInternalProductType()))
			lease.setResidualValueEstimate(residualAmout);
		else if(ProductTypeEnum.CE.name().equals(lease.getInternalProductType()))
			lease.setResidualValueEstimate(BigDecimal.ZERO);
		return lease;
	}

	private LeaseVO updateProductType(LeaseVO lease) throws Exception {
        String leaseType = xRefService.getExternalValue(XRefGroupNameEnum.LEASE_TYPE, lease.getProductCode());
		lease.setExternalProductType(leaseType);
		
		return lease;
	}
	
	private LeaseVO updateResidualValueGuranteeByLesee(LeaseVO lease) {
		if (ProductTypeEnum.OE.name().equals(lease.getInternalProductType()))
			lease.setResidualValueGuranteeByLesee(lease.getResidualValueEstimate());
		if (ProductTypeEnum.CE.name().equals(lease.getInternalProductType()))
			lease.setResidualValueGuranteeByLesee(BigDecimal.ZERO);
		return lease;
	}
	
	private LeaseVO updateCompany(LeaseVO lease) throws Exception {
		String company = xRefService.getExternalValue(XRefGroupNameEnum.COMPANY, lease.getSubsidiary());
		lease.setSubsidiary(company);
		return lease;
	}

	private LeaseVO updateTerm(LeaseVO lease, Optional<QuotationModel> currentQmdRec) {
		if (ProductTypeEnum.CE.name().equals(lease.getInternalProductType()))
			lease.setTerm(currentQmdRec.get().getContractPeriod().subtract(currentQmdRec.get().getContractChangeEventPeriod()).add(BigDecimal.ONE));
		return lease;
	}

	private LeaseVO updateLeaseCommencementDate(LeaseVO lease, Optional<QuotationModel> currentQmdRec) {
		if (ProductTypeEnum.CE.name().equals(lease.getInternalProductType()))
			lease.setCommencementDate(currentQmdRec.get().getAmendmentEffDate());
		return lease;
	}
			
	private LeaseVO updateCurrency(LeaseVO lease) throws Exception {
        String currency = xRefService.getExternalValue(XRefGroupNameEnum.CURRENCY, lease.getCurrency());
		lease.setCurrency(currency);
		
		return lease;
	}	
	
	private LeaseVO updatePrePayment(LeaseVO lease) {
		lease.setPrePayment(lease.getCapitalContribution()
				                .add(lease.getRechargeModelAccessoryAmount())
				                .add(lease.getRechargeDealerAccessoryAmount()));
		return lease;
	}	
	
	//This creates a $Zero schedule for next month
	private LeaseAccountingScheduleVO getZeroDollorNextMonthSchedule(Date transDate) throws Exception {
		LeaseAccountingScheduleVO leaseAcctScheduleVO = new LeaseAccountingScheduleVO();
		leaseAcctScheduleVO.setTransDate(DateUtil.addMonths(transDate, 1l));
		leaseAcctScheduleVO.setAmount(BigDecimal.ZERO);
		return leaseAcctScheduleVO;
	}
	
	private LeaseVO updateFairValueOriginal(LeaseVO lease, Long qmdId) {		
		lease.setLeaseAssetFairValue(quotationDao.getCustomerCapCost(qmdId)
				                     .add(lease.getCapitalContribution())
				                     .add(lease.getRechargeDealerAccessoryAmount())
				                     .add(lease.getRechargeModelAccessoryAmount())
				                    );
		return lease;
	}
	
	private LeaseVO updateFairValueRevision(LeaseVO lease, Long qmdId) {
		if (ProductTypeEnum.OE.name().equals(lease.getInternalProductType())) {
			lease.setLeaseAssetFairValue(quotationDao.getCustomerCapCost(qmdId));
		}
		else if (ProductTypeEnum.CE.name().equals(lease.getInternalProductType())) {
			lease.setLeaseAssetFairValue(assetItemDao.getCurrentValue(lease.getFmsId(), qmdId, lease.getInternalProductType()));
			BigDecimal unPaidPOTotal = docDao.getUnpaidPOTotal(qmdId);
			if(unPaidPOTotal != null) {
				lease.setLeaseAssetFairValue(lease.getLeaseAssetFairValue().add(unPaidPOTotal));
			}
		}
		return lease;
	}
	
	private LeaseVO updateCarryingCostRevision(LeaseVO lease, Long qmdId) {
		lease.setLeaseAssetCostCarrying(assetItemDao.getCurrentValue(lease.getFmsId(), qmdId, lease.getInternalProductType()));
		lease.setLeaseAssetCostCarrying(lease.getLeaseAssetCostCarrying().add(docDao.getUnpaidPOTotal(lease.getQuoId())));
		return lease;
	}
	
	private LeaseVO updateCarryingCostOriginal(LeaseVO lease, Long qmdId) {
		lease.setLeaseAssetCostCarrying(assetItemDao.getInitialValueOriginal(lease.getFmsId(), qmdId));
		lease.setLeaseAssetCostCarrying(lease.getLeaseAssetCostCarrying().add(docDao.getUnpaidPOTotal(lease.getQuoId())));
		return lease;
	}
	
	private LeaseVO updateFairValueAmendment(LeaseVO lease, Long qmdId, Long previousQmdId, BigDecimal totalRechargeAmount) {
		lease.setLeaseAssetFairValue(totalRechargeAmount
										.add(quotationDao.getCustomerCapCostAmendment(qmdId, previousQmdId)));
		return lease;
	}
		
	private LeaseVO updateRechargeDealerAccessory(LeaseVO leaseVO, Long qmdId) {
		List<QuotationDealerAccessory> qdaList = quotationDealerAccessoryDao.findByQmdId(qmdId);
		BigDecimal qdaRechargeAmount = qdaList.parallelStream()
									          .filter(qda -> "Y".equals(qda.getDriverRechargeYn()))
										      .map(qda -> qda.getRechargeAmount())
										      .reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
		leaseVO.setRechargeDealerAccessoryAmount((qdaRechargeAmount == null ? BigDecimal.ZERO : qdaRechargeAmount));
		return leaseVO; 
	}
	
	private LeaseVO updateRechargeModelAccessory(LeaseVO leaseVO, Long qmdId) {
		List<QuotationModelAccessory> qmaList = quotationModelAccessoryDao.findByQmdId(qmdId);
		BigDecimal qmaRechargeAmount = qmaList.parallelStream()
									          .filter(qma -> "Y".equals(qma.getDriverRechargeYn()))
										      .map(qma -> qma.getRechargeAmount())
										      .reduce(BigDecimal.ZERO, (a,b) -> a.add(b));
		leaseVO.setRechargeModelAccessoryAmount(qmaRechargeAmount  == null ? BigDecimal.ZERO : qmaRechargeAmount);
		return leaseVO; 
	}

	private LeaseVO enrichWithClient(LeaseVO lease) throws Exception {
		
		if(lease.getClientExternalId() != null) {
			ClientVO clientVO = customerSuiteTalkService.getCustomer(null, lease.getClientExternalId(), false);
			if(clientVO == null) {
				throw new RetryableSuiteTalkException("Client not found when upserting lease. Lease data:  " + lease);
			}
			
			lease.setClientInternalId(clientVO.getInternalId());
		}

		return lease;
	}
	
	private LeaseVO updateCapitalCost(LeaseVO leaseVO, Long qmdId) throws Exception {		
		CapitalCostVO capitalCostVO = thisQuoteCostService.thisQuoteCost(qmdId);
		if(capitalCostVO == null) throw new NoDataFoundException("Quote service responded with a null quote captial cost");			

		leaseVO.setClientCapitalCost(capitalCostVO.getTotalCostToPlaceInServiceCustomer());
		
		return leaseVO;
	}

	private LeaseVO updateAmendmentCapitalCost(LeaseVO leaseVO, Long qmdId) throws Exception {
		CapitalCostVO previousTotalCapCost = null;
		CapitalCostVO currentTotalCapCost = null;
		
		try {
			currentTotalCapCost = thisQuoteCostService.thisQuoteCost(qmdId);
		} catch(Exception e) {
			throw new RetryableException("Quote service responded with an error while requesting current (amendment) quote's cost ", e);
		}

		try {
			previousTotalCapCost = thisQuoteCostService.thisQuoteCost(Long.valueOf(quotationDao.findPreviousQmdId(qmdId.toString())));
		} catch (Exception e) {
			throw new RetryableException("Quote service responded with an error while requesting previous quote's cost ", e);
		}
		
		if(currentTotalCapCost == null || previousTotalCapCost == null) {
			throw new NoDataFoundException("Quote service responded with a null quote captial cost");
		}
		
		leaseVO.setClientCapitalCost(currentTotalCapCost.getTotalCostToPlaceInServiceCustomer().subtract(previousTotalCapCost.getTotalCostToPlaceInServiceCustomer()));
				
		return leaseVO;
	}	

}
package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suitetalk.StateSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

@Service("billingReportEnrichmentService")
public class BillingReportEnrichmentServiceImpl extends BaseService implements BillingReportEnrichmentService{
    @Resource ServiceCache serviceCache;
	@Resource StateSuiteTalkService stateSuiteTalkService; //TODO Use for State
	@Resource SuiteTalkCacheService suiteTalkCacheService; //TODO Use for State

    private final Logger LOG = LogManager.getLogger(this.getClass());
    
	//TODO Centric is working on stamp the transaction with the DUH information we are retriving below. This may go away once Centric is done.
    @Override
    public List<BillingReportTransactionVO> enrichWithDriverInfo(List<BillingReportTransactionVO> billingReportTransactionVOs) throws Exception {
		return billingReportTransactionVOs.stream().parallel()
		    .map(vo -> {
				try {
					if(vo.getDriverId() == null) {
						DriverUnitHistoryVO duh = serviceCache.findDuhByUnitInternalIdAndDate(vo.getUnitInternalId(), vo.getMaTransactionDate());

						if(duh != null) {
							vo
								.setDriverId(duh.getDriverId())
								.setDriverName(String.format("%s, %s", duh.getDriverLastName(), duh.getDriverFirstName().substring(0, 1)))
								.setDriverCostCenterCode(duh.getCostCenterCode())
								.setDriverCostCenterDescription(duh.getCostCenterDescription())
								.setRechargeCode(duh.getDriverRechargeCode())
								.setDriverAddressState(stateSuiteTalkService.getShortNameByLongName(suiteTalkCacheService.getStates(), duh.getDriverAddressState()))
								.setFleetRefNo(duh.getDriverFleetNo());
						} else {
							LOG.warn("DUH record was not found for billing report transaction {}", vo);
						}
					}
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
				return vo;				
			})
			.collect(Collectors.toList());
    }  
}

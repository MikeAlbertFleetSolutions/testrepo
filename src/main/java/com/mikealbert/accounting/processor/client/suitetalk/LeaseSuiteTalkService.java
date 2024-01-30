package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;

public interface LeaseSuiteTalkService  {
	
	public String upsertLease(LeaseVO lease) throws Exception;
	
	public String amendLease(LeaseVO lease) throws Exception;
	
	public List<Map<String, String>> terminateLease(String quoId) throws Exception;
	
	public String modifyLease(LeaseVO lease) throws Exception;	

	public void novateLease(LeaseVO lease) throws Exception;

	public void updateInterestRate(LeaseVO lease) throws Exception;

	public void updateActualEndDate(LeaseVO lease) throws Exception;
	
	//Used by JUnit Tests to clean up
	public void deleteLease(LeaseVO lease) throws Exception;
	
	public Map<String, String> getLease(String externalId, int numberOfRetries) throws Exception;

	public List<LeaseVO> getLease(String externalId) throws Exception;
	
	public Map<String, String> getLeasePayments() throws Exception;

	public List<LeaseAccountingScheduleVO> getSchedules(LeaseVO leaseVO) throws Exception;	
	
	public void deleteCurrentAndFutruePaymentSchedules(LeaseVO lease) throws Exception;
	
}

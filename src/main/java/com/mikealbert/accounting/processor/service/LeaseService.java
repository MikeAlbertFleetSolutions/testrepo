package com.mikealbert.accounting.processor.service;

import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.LeaseTerminationRequestVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;

public interface LeaseService {
	LeaseVO getLeaseRecord(String qmdId) throws Exception;
	LeaseVO getNewLeaseRecord(String qmdId) throws Exception;
	LeaseVO getAmendLeaseRecord(String qmdId) throws Exception;
	LeaseVO getReviseLeaseRecord(String qmdId) throws Exception;
	LeaseVO getNovateLeaseRecord(String qmdId) throws Exception;
	
	String upsertLease(LeaseVO lease) throws Exception;
	String amendLease(LeaseVO lease) throws Exception;	
	String modifyLease(LeaseVO lease) throws Exception;
	void novateLease(LeaseVO lease) throws Exception;
	void updateInterestRate(LeaseVO lease) throws Exception;
	void updateActualEndDate(LeaseVO lease) throws Exception;
	
	List<Map<String, String>> terminateLease(String quoId) throws Exception;
	
	List<LeaseTerminationRequestVO> initializeLeaseTerminationRequests(List<Long> clnIds) throws Exception;
	
	List<Map<String, Object>> getExternalLeases();
	List<Map<String, Object>> getExternalLeases(String externalId);

	List<LeaseVO> getExternalLease(String externalId, boolean loadSchedule) throws Exception;
}

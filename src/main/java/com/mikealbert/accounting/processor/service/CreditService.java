package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.accounting.processor.vo.CreditVO;

public interface CreditService {	
	static final String MAINTENANCE_AI_FEE_MEMO = "AI Shop Fee";
	
	CreditVO getCredit(Long docId) throws Exception;	
	
	void updateGlAccToOne(Long docId) throws Exception;

	void create(CreditVO credit) throws Exception;
	
	List<Long> getMaintenanceCreditIds(Date start, Date end);
	
}

package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;

public interface PurchaseOrderService {
	public PurchaseOrderVO get(Long docId) throws Exception;	
	public void add(PurchaseOrderVO poVO) throws Exception;
	public void closeExternal(Long docId) throws Exception;
	public void revise(Long docId) throws Exception;
	public void update(Long docId) throws Exception;
	public BigDecimal getUnpaidPOTotalByFmsId(Long fmsId);

	public List<Map<String, Object>> findClosedPOs(Date from, Date to) throws Exception;
	public void closeInternal(Long docId) throws Exception;
  
}

package com.mikealbert.accounting.processor.service;

import java.util.List;

import com.mikealbert.accounting.processor.vo.CacheVO;

public interface CacheService {
	public void evict(String name);
	
	public void evictAll();
	
	public List<CacheVO> getAll();
}

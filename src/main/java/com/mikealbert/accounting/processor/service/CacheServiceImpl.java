package com.mikealbert.accounting.processor.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.vo.CacheVO;

@Service("cacheService")
public class CacheServiceImpl implements CacheService {
	@Resource CacheManager cacheManager; 
	
	@Override
	public void evict(String name) {
		Cache cache = cacheManager.getCache(name);
		if(cache !=  null) {
			cache.invalidate();
		}
	}
	
	@Override
	public void evictAll() {
		cacheManager.getCacheNames().stream()
		.filter(name -> cacheManager.getCache(name) == null ? false : true)
		.map(name -> cacheManager.getCache(name))
		.forEach(cache -> cache.invalidate());
	}
	
	@Override
	public List<CacheVO> getAll() {
		List<CacheVO> names = new ArrayList<>();		
		
		cacheManager.getCacheNames().stream()
		.map(name -> new CacheVO(name))
		.forEach(names::add);
		
		return names;
	}	

}

package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("suiteAnalyticsCacheService")
public class SuiteAnalyiticsCacheServiceImpl implements SuiteAnalyticsCacheService {
	@Resource ItemSuiteAnalyticsService itemSuiteAnalyticsService;
	
	@Cacheable(value = "getItem_cache")
	@Override
	public Map<String, Object> getItem(String name) throws Exception {
		return itemSuiteAnalyticsService.get(name);
	}
}

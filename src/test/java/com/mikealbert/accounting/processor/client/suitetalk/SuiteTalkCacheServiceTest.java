package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("A cacheable method")
public class SuiteTalkCacheServiceTest {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	
	@Test
	@DisplayName("when called muliplte times, each subsequent call returns the cached value")
	public void testSearchDepartmentInternalIdByName() throws Exception {
		String ref1 = suiteTalkCacheService.searchDepartmentInternalIdByName("Business Development Managers");
		String ref2 = suiteTalkCacheService.searchDepartmentInternalIdByName("Business Development Managers");
		
		assertTrue(ref1 == ref2);
	}

	@Test
	@DisplayName("when called muliplte times, each subsequent call returns the cached value")
	public void testGetStates() throws Exception {
		List<Map<String, Object>> ref1 = suiteTalkCacheService.getStates();
		List<Map<String, Object>> ref2 = suiteTalkCacheService.getStates();
		
		assertTrue(ref1 == ref2);
	}	
		
}

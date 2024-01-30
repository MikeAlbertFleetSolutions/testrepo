package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.accounting.processor.vo.CacheVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@SpringBootTest
public class CacheServiceTest {
	@Resource XRefService xRefService; 
	@Resource CacheService cacheService;
	
	@BeforeEach
	void setup() throws Exception {
		cacheService.evictAll();
	}
	
	@Test
	@DisplayName("when evicting a paticular cache, the cache is evicted")
	public void testEvict() throws Exception {
		List<XRef>  ref1 = xRefService.getByGroupName(XRefGroupNameEnum.COUNTRY);
		
		cacheService.evict("getByGroupName_cache");
		
		List<XRef>  ref2 = xRefService.getByGroupName(XRefGroupNameEnum.COUNTRY);
		
		assertTrue(ref1.get(0) != ref2.get(0));
	}
	
    @Test
	@DisplayName("when cache exists, they are returned")    
    public void testGetAll() throws Exception {
		xRefService.getByGroupName(XRefGroupNameEnum.COUNTRY);
		
		List<CacheVO> caches = cacheService.getAll();
		
		assertTrue(caches.size() > 0);
    }	
		
}

package com.mikealbert.accounting.processor.config;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
		caffeineCacheManager.setCaffeine(caffeineConfig());
		return caffeineCacheManager;
	}

	private Caffeine<Object, Object> caffeineConfig() {
		return Caffeine.newBuilder()
				.expireAfterAccess(1, TimeUnit.HOURS);
	}
}

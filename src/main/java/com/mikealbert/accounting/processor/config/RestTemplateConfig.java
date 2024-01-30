package com.mikealbert.accounting.processor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author wayne.sibley
 *
 */
@Configuration
public class RestTemplateConfig {	
	
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}
	
}

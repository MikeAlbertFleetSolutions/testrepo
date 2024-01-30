package com.mikealbert.accounting.processor.config;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteMeterRegistry;

@Configuration
@ConditionalOnProperty("management.endpoint.shutdown.enabled")
public class MonitoringConfiguration {
	@Autowired BuildProperties buildProperties;
	
	@Value("${spring.profiles.active}")
	private String environment;
	
	//TODO Parameterize tag parts
	@Bean
	public GraphiteMeterRegistry graphiteMeterRegistry(GraphiteConfig config, Clock clock) {
		String host = "unknown";

		try {			
			host = InetAddress.getLocalHost().getHostName();
		} catch(Exception e) {
			host = "error";
		}

		final String finalHost = host;
		return  new GraphiteMeterRegistry(
				config, 
				clock, 
				(id, convention) -> String.format("%s.application.%s.%s.%s", environment, finalHost, buildProperties.getName(), HierarchicalNameMapper.DEFAULT.toHierarchicalName(id, convention)));

	}		

}

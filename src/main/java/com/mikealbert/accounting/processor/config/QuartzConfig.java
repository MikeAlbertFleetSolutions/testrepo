package com.mikealbert.accounting.processor.config;

import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.quartz2.QuartzComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.mikealbert.util.data.DataUtil;

/**
 * Force configuring the Quartz2 component. 
 * I expected the starter component to auto configure the  
 * quartz2 component fully, however, there appears to be a gap 
 * between spring boot and camel quartz2 component contexts.
 * 
 * @author wayne.sibley
 *
 */
@Configuration
public class QuartzConfig {	
	
	@Autowired Environment env;
	
	@Bean 
	QuartzComponent configureCamelQuartzComponent(final CamelContext camelContext) {
		Properties properties = new Properties();
		// Scheduler
		properties.setProperty("org.quartz.scheduler.instanceName", DataUtil.nvl(env.getProperty("mafs.scheduler.instance.name"), ""));
		properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		// Thread Pool
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", DataUtil.nvl(env.getProperty("spring.datasource.hikari.maximum-pool-size"), ""));
		// Job Store
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
		properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
		properties.setProperty("org.quartz.jobStore.useProperties", "false");
		properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
		properties.setProperty("org.quartz.jobStore.isClustered", "true");
		properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
		properties.setProperty("org.quartz.jobStore.dataSource", "quartzDataSource");
		// Data Source
		properties.setProperty("org.quartz.dataSource.quartzDataSource.provider", "hikaricp");
		properties.setProperty("org.quartz.dataSource.quartzDataSource.driver", DataUtil.nvl(env.getProperty("spring.datasource.driver-class-name"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.URL", DataUtil.nvl(env.getProperty("spring.datasource.url"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.user", DataUtil.nvl(env.getProperty("spring.datasource.username"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.password", DataUtil.nvl(env.getProperty("spring.datasource.password"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.minimumIdle", DataUtil.nvl(env.getProperty("spring.datasource.hikari.minimumIdle"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.maximumPoolSize", DataUtil.nvl(env.getProperty("spring.datasource.hikari.maximum-pool-size"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.idleTimeout", DataUtil.nvl(env.getProperty("spring.datasource.hikari.idleTimeout"), ""));
		properties.setProperty("org.quartz.dataSource.quartzDataSource.connectionTimeout", DataUtil.nvl(env.getProperty("spring.datasource.hikari.connection-timeout"), ""));		
		// Plugins
		properties.setProperty("org.quartz.plugin.shutdownhook.class", "org.quartz.plugins.management.ShutdownHookPlugin");
		properties.setProperty("org.quartz.plugin.shutdownhook.cleanShutdown", "true");		
		
		QuartzComponent quartzComponent = (QuartzComponent)camelContext.getComponent("quartz2");
		quartzComponent.setProperties(properties);	
		
		return quartzComponent;
	}
	
}

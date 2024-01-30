package com.mikealbert.accounting.processor.client.suiteanalytics;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.suiteanalytics.client.SuiteAnalyticsClientService;

public abstract class BaseSuiteAnalyticsService {
	@Value("${mafs.suiteanalytics.account.id}" )
	String accountId;
	
	@Value("${mafs.suiteanalytics.host}")
	String host;

	@Value("${mafs.suiteanalytics.port}")
	int port;
		
	@Value( "${mafs.suiteanalytics.username}" )
	String username;
	
	@Value( "${mafs.suiteanalytics.password}" )
	String password;
	
	@Value( "${mafs.suiteanalytics.roleId}" )
	int roleId;
			
	static final int TRAILING_THRESHOLD_DAYS = 90;

	final Logger LOG = LogManager.getLogger(this.getClass());	
	
	private SuiteAnalyticsClientService suiteAnalyticsClientService;
	
	@PostConstruct
	private void init() {
		this.suiteAnalyticsClientService = new SuiteAnalyticsClientService(accountId, host, port, username, password, roleId);
	}
	
	protected List<Map<String, Object>> execute(String sql) {
		return suiteAnalyticsClientService.execute(sql);
	}
	
}

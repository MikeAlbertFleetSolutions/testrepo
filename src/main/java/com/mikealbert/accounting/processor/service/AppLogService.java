package com.mikealbert.accounting.processor.service;

import java.util.Date;

import com.mikealbert.accounting.processor.entity.AppLog;

public interface AppLogService {
	public AppLog getLatestLog(String name);
	public String log(String jobName, String payload, Date createDate) throws Exception;
	
	public Date getStartDate(String appName);
	
	public Date getEndDate();
}

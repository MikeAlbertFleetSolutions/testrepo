package com.mikealbert.accounting.processor.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.dao.AppLogDAO;
import com.mikealbert.accounting.processor.entity.AppLog;

@Service("appLogService")
public class AppLogServiceImpl implements AppLogService  {
	@Resource AppLogDAO appLogDAO;

	@Override
	public AppLog getLatestLog(String name) {
		AppLog log = appLogDAO.findTopByNameOrderByPlgIdDesc(name);
		
		return log;
	}	
	
	@Override
	public String log(String jobName, String payload, Date createDate) throws Exception {
		String retVal = null;
		
		AppLog appLog = new AppLog(jobName, payload, createDate);
		appLogDAO.save(appLog);
		
		return retVal;
	}

	@Override
	public Date getStartDate(String name) {
		AppLog appLog = appLogDAO.findTopByNameOrderByPlgIdDesc(name);
		
		return appLog == null ? new Date() : appLog.getCreateDate();
	}

	@Override
	public Date getEndDate() {
		return new Date();
	}

}

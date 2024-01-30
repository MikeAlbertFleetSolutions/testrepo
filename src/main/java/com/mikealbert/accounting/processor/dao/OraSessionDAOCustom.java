package com.mikealbert.accounting.processor.dao;

import java.util.Date;

public interface OraSessionDAOCustom  {	
	public String getReleaseVersion();
	public String getOptimizerVersion();
	public Date getReleaseDate();
	public String getWillowConfigValue(String propertyName);
	public String getDbRefreshInfo(Long contextId);
}

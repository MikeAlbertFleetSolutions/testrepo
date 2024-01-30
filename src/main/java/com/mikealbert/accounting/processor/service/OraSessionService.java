package com.mikealbert.accounting.processor.service;

import java.io.Serializable;
import java.util.Date;

public interface OraSessionService extends Serializable {
	public Date getDatabaseDate();
	public String getDBRefreshdate();
}


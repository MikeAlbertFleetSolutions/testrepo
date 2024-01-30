package com.mikealbert.accounting.processor.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.dao.OraSessionDAO;

@Service("oraSessionService")
public class OraSessionServiceImpl implements OraSessionService {

	@Resource
	OraSessionDAO oraSessionDAO;
		
	private static final long serialVersionUID = 1L;
	
	@Override
	public Date getDatabaseDate() {
		return oraSessionDAO.getReleaseDate();
	}

	@Override
	public String getDBRefreshdate() {
		return oraSessionDAO.getDbRefreshInfo(1l);
	}
}
package com.mikealbert.accounting.processor.dao;

import java.util.Date;

import com.mikealbert.accounting.processor.entity.OraSession;

public  class OraSessionDAOImpl extends GenericDAOImpl<OraSession, Long> implements OraSessionDAOCustom {
	
	private static final long serialVersionUID = 1L;
	
	public String getReleaseVersion(){
		String version = null;
		String stmt = "SELECT wih_version "
                      + "FROM WILLOW_INSTALL_HISTORY "
                      + "WHERE WIH_INSTALL_DATE IN (SELECT max(WIH_INSTALL_DATE) FROM willow_install_history)";
        version = (String)entityManager.createNativeQuery(stmt).getSingleResult();	
		return version;
	}
	
	public String getOptimizerVersion(){
		String version = null;
		String stmt = "SELECT value "
                      + " FROM v$parameter2 "
                      + " WHERE name = 'optimizer_features_enable'";	
        version = (String)entityManager.createNativeQuery(stmt).getSingleResult();			
		return version;				
	}
	
	public Date getReleaseDate(){
		Date date = null;
		String stmt = "SELECT wih_install_date "
                      + "FROM WILLOW_INSTALL_HISTORY "
                      + "WHERE wih_install_date IN (SELECT max(WIH_INSTALL_DATE) FROM willow_install_history)";
        date = (Date)entityManager.createNativeQuery(stmt).getSingleResult();			
		return date;
	}

	public String getDbRefreshInfo(Long contextId){
		
		String dbRefreshInfo = null;
		String stmt = "SELECT heading "
                   	  + "FROM control_contexts "
                      + "WHERE c_id = ?1";
		dbRefreshInfo = (String)entityManager.createNativeQuery(stmt).setParameter(1, contextId).getSingleResult();
		return dbRefreshInfo;
	}
	
	/**
	 * Retrieves Willow's config value based on property name.
	 * @param Name of the willow property
	 */
	public String getWillowConfigValue(String propertyName){
		String dbName = null;
		String stmt = "SELECT config_value FROM willow_config WHERE config_name = ?";		
		dbName = (String)entityManager.createNativeQuery(stmt).setParameter(1, propertyName).getSingleResult();		
		return dbName;
	}
	
}

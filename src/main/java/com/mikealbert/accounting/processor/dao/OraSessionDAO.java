package com.mikealbert.accounting.processor.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.accounting.processor.entity.OraSession;

public interface OraSessionDAO extends JpaRepository<OraSession,Long>, OraSessionDAOCustom {
	
}


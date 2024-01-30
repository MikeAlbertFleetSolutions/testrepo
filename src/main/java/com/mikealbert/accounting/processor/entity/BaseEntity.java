package com.mikealbert.accounting.processor.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

public class BaseEntity {
	@Version
	@Column(name="VERSIONTS")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date versionts;

	public Date getVersionts() {
		return versionts;
	}	
}


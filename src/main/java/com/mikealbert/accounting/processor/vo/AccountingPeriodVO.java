package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class AccountingPeriodVO implements Serializable {

    private String internalId;
	
	private String name;

	private Date start;

	private Date end;

	public AccountingPeriodVO() {}

	public String getInternalId() {
		return internalId;
	}

	public AccountingPeriodVO setInternalId(String internalId) {
		this.internalId = internalId;
		return this;
	}

	
	public String getName() {
		return name;
	}

	public AccountingPeriodVO setName(String name) {
		this.name = name;
		return this;
	}

	public Date getStart() {
		return DateUtil.clone(start);
	}

	public AccountingPeriodVO setStart(Date start) {
		this.start = DateUtil.clone(start);
		return this;
	}

	public Date getEnd() {
		return DateUtil.clone(end);
	}

	public AccountingPeriodVO setEnd(Date end) {
		this.end = DateUtil.clone(end);
		return this;
	}

	@Override
	public String toString() {
		return "AccountingPeriodVO [end=" + end + ", internalId=" + internalId + ", name=" + name + ", start=" + start
				+ "]";
	}
	
}

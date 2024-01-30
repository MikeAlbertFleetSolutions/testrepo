package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class LeaseAccountingScheduleVO implements Serializable {
	
	private String leaseInternalId;

	private BigDecimal amount;
	
	private Date transDate;
	
	public LeaseAccountingScheduleVO() {
		this.amount = BigDecimal.ZERO;
		this.transDate = null;
	}
	
	public LeaseAccountingScheduleVO(BigDecimal amount, Date transDate) {
		this.amount = amount;
		this.transDate = DateUtil.clone(transDate);
	}

	public LeaseAccountingScheduleVO(LeaseAccountingScheduleVO leaseAcctScheduleVO) {
		this.amount = leaseAcctScheduleVO.getAmount();
		this.transDate = DateUtil.clone(leaseAcctScheduleVO.getTransDate());
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTransDate() {
		return DateUtil.clone(transDate);
	}

	public void setTransDate(Date transDate) {
		this.transDate = DateUtil.clone(transDate);
	}

	public String getLeaseInternalId() {
		return leaseInternalId;
	}

	public void setLeaseInternalId(String leaseInternalId) {
		this.leaseInternalId = leaseInternalId;
	}

	@Override
	public String toString() {
		return "LeaseAccountingScheduleVO [amount=" + amount + ", leaseInternalId=" + leaseInternalId + ", transDate="
				+ transDate + "]";
	}

		
}

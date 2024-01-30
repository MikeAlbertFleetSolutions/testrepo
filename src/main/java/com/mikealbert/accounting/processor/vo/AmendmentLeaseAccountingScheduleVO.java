package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class AmendmentLeaseAccountingScheduleVO {
	
	private BigDecimal qelId;
	private BigDecimal residual;
	private BigDecimal rechargeAmount;	
	private String rechargeInd;
	private Date transDate;
	private BigDecimal monthlyLeaseAmount;
	
	public AmendmentLeaseAccountingScheduleVO() {
		this.residual = BigDecimal.ZERO;
		this.rechargeAmount = BigDecimal.ZERO;
		this.rechargeInd = "";
		this.transDate = null;
		this.monthlyLeaseAmount = BigDecimal.ZERO;
	}

	public BigDecimal getQelId() {
		return qelId;
	}

	public void setQelId(BigDecimal qelId) {
		this.qelId = qelId;
	}

	public BigDecimal getResidual() {
		return residual;
	}

	public void setResidual(BigDecimal residual) {
		this.residual = residual;
	}

	public BigDecimal getRechargeAmount() {
		return rechargeAmount;
	}

	public void setRechargeAmount(BigDecimal rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getRechargeInd() {
		return rechargeInd;
	}

	public void setRechargeInd(String rechargeInd) {
		this.rechargeInd = rechargeInd;
	}

	public Date getTransDate() {
		return DateUtil.clone(transDate);
	}

	public void setTransDate(Date transDate) {
		this.transDate = DateUtil.clone(transDate);
	}

	public BigDecimal getMonthlyLeaseAmount() {
		return monthlyLeaseAmount;
	}

	public void setMonthlyLeaseAmount(BigDecimal monthlyLeaseAmount) {
		this.monthlyLeaseAmount = monthlyLeaseAmount;
	}

	@Override
	public String toString() {
		return "AmendmentLeaseAccountingScheduleVO [qelId=" + qelId + ", residual=" + residual + ", rechargeAmount="
				+ rechargeAmount + ", rechargeInd=" + rechargeInd + ", transDate=" + transDate + ", monthlyLeaseAmount="
				+ monthlyLeaseAmount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((monthlyLeaseAmount == null) ? 0 : monthlyLeaseAmount.hashCode());
		result = prime * result + ((qelId == null) ? 0 : qelId.hashCode());
		result = prime * result + ((rechargeAmount == null) ? 0 : rechargeAmount.hashCode());
		result = prime * result + ((rechargeInd == null) ? 0 : rechargeInd.hashCode());
		result = prime * result + ((residual == null) ? 0 : residual.hashCode());
		result = prime * result + ((transDate == null) ? 0 : transDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmendmentLeaseAccountingScheduleVO other = (AmendmentLeaseAccountingScheduleVO) obj;
		if (monthlyLeaseAmount == null) {
			if (other.monthlyLeaseAmount != null)
				return false;
		} else if (!monthlyLeaseAmount.equals(other.monthlyLeaseAmount))
			return false;
		if (qelId == null) {
			if (other.qelId != null)
				return false;
		} else if (!qelId.setScale(0).equals(other.qelId.setScale(0)))
			return false;
		if (rechargeAmount == null) {
			if (other.rechargeAmount != null)
				return false;
		} else if (!rechargeAmount.equals(other.rechargeAmount))
			return false;
		if (rechargeInd == null) {
			if (other.rechargeInd != null)
				return false;
		} else if (!rechargeInd.equals(other.rechargeInd))
			return false;
		if (residual == null) {
			if (other.residual != null)
				return false;
		} else if (!residual.equals(other.residual))
			return false;
		if (transDate == null) {
			if (other.transDate != null)
				return false;
		} else if (!transDate.equals(other.transDate))
			return false;
		return true;
	}


}

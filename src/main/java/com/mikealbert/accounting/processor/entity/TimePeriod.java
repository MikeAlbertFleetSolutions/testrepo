package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the TIME_PERIODS database table.
 * 
 */
@Entity
@Table(name="TIME_PERIODS")
public class TimePeriod implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SEQUENCE_NO")
	private Long sequenceNo;

	@Column(name="AP_STATUS")
	private String apStatus;

	@Column(name="AR_STATUS")
	private String arStatus;

	@Temporal(TemporalType.DATE)
	@Column(name="BILLING_DATE")
	private Date billingDate;

	@Column(name="CB_STATUS")
	private String cbStatus;

	@Column(name="CD_STATUS")
	private String cdStatus;

	@Temporal(TemporalType.DATE)
	@Column(name="END_DATE")
	private Date endDate;

	@Column(name="GL_STATUS")
	private String glStatus;

	@Column(name="NO_OF_WEEKS")
	private BigDecimal noOfWeeks;

	@Temporal(TemporalType.DATE)
	@Column(name="PAY_DATE")
	private Date payDate;

	@Column(name="SO_STATUS")
	private String soStatus;

	@Temporal(TemporalType.DATE)
	@Column(name="START_DATE")
	private Date startDate;

	@Column(name="TIME_PERIOD")
	private String timePeriod;

	@Column(name="TIME_PERIOD_SEQUENCE")
	private BigDecimal timePeriodSequence;

	/*@Column(name="YEAR")
	private String year;*/

	//bi-directional many-to-one association to CalendarYear
	@JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "CALENDAR", referencedColumnName = "CALENDAR"),
        @JoinColumn(name = "YEAR",referencedColumnName= "YEAR")
      })
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
	private CalendarYear calendarYear;
	
	
	public TimePeriod() {
	}

	public Long getSequenceNo() {
		return this.sequenceNo;
	}

	public void setSequenceNo(Long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getApStatus() {
		return this.apStatus;
	}

	public void setApStatus(String apStatus) {
		this.apStatus = apStatus;
	}

	public String getArStatus() {
		return this.arStatus;
	}

	public void setArStatus(String arStatus) {
		this.arStatus = arStatus;
	}

	public Date getBillingDate() {
		return DateUtil.clone(this.billingDate);
	}

	public void setBillingDate(Date billingDate) {
		this.billingDate = DateUtil.clone(billingDate);
	}

	public String getCbStatus() {
		return this.cbStatus;
	}

	public void setCbStatus(String cbStatus) {
		this.cbStatus = cbStatus;
	}

	public String getCdStatus() {
		return this.cdStatus;
	}

	public void setCdStatus(String cdStatus) {
		this.cdStatus = cdStatus;
	}

	public Date getEndDate() {
		return DateUtil.clone(this.endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
	}

	public String getGlStatus() {
		return this.glStatus;
	}

	public void setGlStatus(String glStatus) {
		this.glStatus = glStatus;
	}

	public BigDecimal getNoOfWeeks() {
		return this.noOfWeeks;
	}

	public void setNoOfWeeks(BigDecimal noOfWeeks) {
		this.noOfWeeks = noOfWeeks;
	}

	public Date getPayDate() {
		return DateUtil.clone(this.payDate);
	}

	public void setPayDate(Date payDate) {
		this.payDate = DateUtil.clone(payDate);
	}

	public String getSoStatus() {
		return this.soStatus;
	}

	public void setSoStatus(String soStatus) {
		this.soStatus = soStatus;
	}

	public Date getStartDate() {
		return DateUtil.clone(this.startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
	}

	public String getTimePeriod() {
		return this.timePeriod;
	}

	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}

	public BigDecimal getTimePeriodSequence() {
		return this.timePeriodSequence;
	}

	public void setTimePeriodSequence(BigDecimal timePeriodSequence) {
		this.timePeriodSequence = timePeriodSequence;
	}

	/*public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}*/

	public CalendarYear getCalendarYear() {
		return this.calendarYear;
	}

	public void setCalendarYear(CalendarYear calendarYear) {
		this.calendarYear = calendarYear;
	}

	
}
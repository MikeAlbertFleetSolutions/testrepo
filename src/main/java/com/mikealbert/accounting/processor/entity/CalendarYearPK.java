package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the CALENDAR_YEARS database table.
 * 
 */
@Embeddable
public class CalendarYearPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long cId;
	
	@Column(name="CALENDAR")
	private String calendar;

	@Column(name="YEAR")
	private String year;

	public CalendarYearPK() {
	}
	public long getCId() {
		return this.cId;
	}
	public void setCId(long cId) {
		this.cId = cId;
	}
	public String getCalendar() {
		return this.calendar;
	}
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	public String getYear() {
		return this.year;
	}
	public void setYear(String year) {
		this.year = year;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CalendarYearPK)) {
			return false;
		}
		CalendarYearPK castOther = (CalendarYearPK)other;
		return 
			(this.cId == castOther.cId)
			&& this.calendar.equals(castOther.calendar)
			&& this.year.equals(castOther.year);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.calendar.hashCode();
		hash = hash * prime + this.year.hashCode();
		
		return hash;
	}
}
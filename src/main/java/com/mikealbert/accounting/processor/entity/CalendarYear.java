package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the CALENDAR_YEARS database table.
 * 
 */
@Entity
@Table(name="CALENDAR_YEARS")
public class CalendarYear implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CalendarYearPK id;

	private String description;

	@Column(name="YEAR_SEQUENCE")
	private BigDecimal yearSequence;

	//bi-directional many-to-one association to TimePeriod
	@OneToMany(mappedBy="calendarYear")
	private List<TimePeriod> timePeriods;

	public CalendarYear() {
	}

	public CalendarYearPK getId() {
		return this.id;
	}

	public void setId(CalendarYearPK id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getYearSequence() {
		return this.yearSequence;
	}

	public void setYearSequence(BigDecimal yearSequence) {
		this.yearSequence = yearSequence;
	}

	public List<TimePeriod> getTimePeriods() {
		return this.timePeriods;
	}

	public void setTimePeriods(List<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}

	
	public TimePeriod addTimePeriods(TimePeriod timePeriods) {
		getTimePeriods().add(timePeriods);
		timePeriods.setCalendarYear(this);

		return timePeriods;
	}

	public TimePeriod removeTimePeriods(TimePeriod timePeriods) {
		getTimePeriods().remove(timePeriods);
		timePeriods.setCalendarYear(null);

		return timePeriods;
	}
}
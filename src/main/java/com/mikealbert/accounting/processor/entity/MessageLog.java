package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistance class for the MESSAGE_LOG database table.
 * 
 */
@Entity
@Table(name="MESSAGE_LOG", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_name", "message_id"})})
public class MessageLog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MLG_SEQ")    
    @SequenceGenerator(name="MLG_SEQ", sequenceName="MLG_SEQ", allocationSize=1)	
	@Column(name="MLG_ID")
	private Long mlgId;

	@NotNull(message = "Event Name is required")
	@Column(name="EVENT_NAME")
	private String eventName;

	@NotNull(message = "Message Id is required")
	@Column(name="MESSAGE_ID")
	private String messageId;

	@NotNull(message = "Start Date is required")
	@Column(name="START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name="END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
	private Date endDate;	

	public MessageLog() {
		this.startDate = DateUtil.clone(new Date());		
	}
	public MessageLog(String eventName, String messageId) {
		this.eventName = eventName;
		this.messageId = messageId;
		this.startDate = DateUtil.clone(new Date());
	}	

	public Long getMlgId() {
		return mlgId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Date getStartDate() {
		return DateUtil.clone(startDate);
	}

	public void setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
	}

	public Date getEndDate() {
		return DateUtil.clone(endDate);
	}

	public void setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
	}

	@Override
	public String toString() {
		return "MessageLog [endDate=" + endDate + ", eventName=" + eventName + ", messageId=" + messageId + ", mlgId="
				+ mlgId + ", startDate=" + startDate + "]";
	}	
		
}
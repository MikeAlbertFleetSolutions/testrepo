package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


@Entity
@Table(name = "DIST")
public class Dist  extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DIS_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIS_ID_SEQ")
    @SequenceGenerator(name = "DIS_ID_SEQ", sequenceName = "DIS_ID_SEQ", allocationSize = 1)
	private long disId;

	@Column(name="CDB_CODE_1")
	private String cdbCode1;
	
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="DOC_ID", insertable=false, updatable=false)
	private Doc doc;

	public Dist() {}

	public long getDisId() {
		return this.disId;
	}
	
	public void setDisId(long disId) {
		this.disId = disId;
	}

	public String getCdbCode1() {
		return cdbCode1;
	}

	public void setCdbCode1(String cdbCode1) {
		this.cdbCode1 = cdbCode1;
	}

	public Date getVersionts() {
		return DateUtil.clone(this.versionts);
	}

	public Doc getDoc() {
		return this.doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}
	
}
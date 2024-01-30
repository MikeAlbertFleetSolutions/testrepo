package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the UNIT_TITLE_HISTORY database table.
 * 
 */
@Entity
@Table(name="UNIT_TITLE_HISTORY")
public class UnitTitleHistory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "UTH_ID")
    private Long uthId;

	@Column(name="FMS_FMS_ID")
	private Long fmsFmsId;

	public UnitTitleHistory() {}

	public Long getUthId() {
		return uthId;
	}

	public void setUthId(Long uthId) {
		this.uthId = uthId;
	}

	public Long getFmsFmsId() {
		return fmsFmsId;
	}

	public void setFmsFmsId(Long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	@Override
	public String toString() {
		return "UnitTitleHistory [uthId=" + uthId + ", fmsFmsId=" + fmsFmsId + "]";
	}
	
}
package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the DOC_LINKS database table.
 * 
 */
@Embeddable
public class DocLinkPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PARENT_DOC_ID")
	private long parentDocId;

	@Column(name="CHILD_DOC_ID")
	private long childDocId;

	public DocLinkPK() {
	}
	public long getParentDocId() {
		return this.parentDocId;
	}
	public void setParentDocId(long parentDocId) {
		this.parentDocId = parentDocId;
	}
	public long getChildDocId() {
		return this.childDocId;
	}
	public void setChildDocId(long childDocId) {
		this.childDocId = childDocId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DocLinkPK)) {
			return false;
		}
		DocLinkPK castOther = (DocLinkPK)other;
		return 
			(this.parentDocId == castOther.parentDocId)
			&& (this.childDocId == castOther.childDocId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.parentDocId ^ (this.parentDocId >>> 32)));
		hash = hash * prime + ((int) (this.childDocId ^ (this.childDocId >>> 32)));
		
		return hash;
	}
}
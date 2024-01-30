package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

/**
 * The primary key class for the DOCL database table.
 * 
 */
@Embeddable
public class DoclPK implements Serializable {
	private static final long serialVersionUID = -1329161834104750761L;

	@Column(name="DOC_ID", insertable=false, updatable=false)
	private long docId;

	@Column(name="LINE_ID")
	private long lineId;

	public DoclPK() {}
	
	public long getDocId() {
		return this.docId;
	}
	
	public void setDocId(long docId) {
		this.docId = docId;
	}
	
	public long getLineId() {
		return this.lineId;
	}
	
	public void setLineId(long lineId) {
		this.lineId = lineId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(docId, lineId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoclPK other = (DoclPK) obj;
		return docId == other.docId && lineId == other.lineId;
	}

	@Override
	public String toString() {
		return "DoclPK [docId=" + docId + ", lineId=" + lineId + "]";
	}

	
}
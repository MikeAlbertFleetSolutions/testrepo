package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;


/**
 * The persistent class for the XREF database table.
 * 
 */
@Entity
public class XRef implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private XRefPK xRefPK;

	public XRef() {}
	
	public XRef(String groupName, String internalValue, String externalValue) {
		this.xRefPK = new XRefPK(groupName, internalValue, externalValue);
	}
	
	public XRef(Map<String, String> xRefMap) {
		this.xRefPK = new XRefPK(xRefMap.get("groupName"), xRefMap.get("internalValue"), xRefMap.get("externalValue"));		
	}
		
	@Override
	public int hashCode() {
		return Objects.hash(xRefPK);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof XRef))
			return false;
		XRef other = (XRef) obj;
		return Objects.equals(xRefPK, other.xRefPK);
	}	
	
	@Override
	public String toString() {
		return "XRef [xRefPK=" + xRefPK + "]";
	}

	public XRefPK getxRefPK() {
		return xRefPK;
	}

	public void setxRefPK(XRefPK xRefPK) {
		this.xRefPK = xRefPK;
	}
	
	
}
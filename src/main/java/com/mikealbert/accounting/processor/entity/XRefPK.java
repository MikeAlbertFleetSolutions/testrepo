package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * The primary key class for the XREF database table.
 * 
 */
@Embeddable
public class XRefPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(name="GROUP_NAME")
	private String groupName;

	@NotNull
	@Column(name="INTERNAL_VALUE")
	private String internalValue;

	@NotNull
	@Column(name="EXTERNAL_VALUE")
	private String externalValue;
	
	public XRefPK() {}

	public XRefPK(String groupName, String internalValue, String externalValue) {
		this.groupName = groupName;
		this.internalValue = internalValue;
		this.externalValue = externalValue;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(externalValue, groupName, internalValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof XRefPK))
			return false;
		XRefPK other = (XRefPK) obj;
		return Objects.equals(externalValue, other.externalValue) && Objects.equals(groupName, other.groupName)
				&& Objects.equals(internalValue, other.internalValue);
	}
	
	@Override
	public String toString() {
		return "XRefPK [groupName=" + groupName + ", internalValue=" + internalValue + ", externalValue="
				+ externalValue + "]";
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getInternalValue() {
		return internalValue;
	}

	public void setInternalValue(String internalValue) {
		this.internalValue = internalValue;
	}

	public String getExternalValue() {
		return externalValue;
	}

	public void setExternalValue(String externalValue) {
		this.externalValue = externalValue;
	}	
}
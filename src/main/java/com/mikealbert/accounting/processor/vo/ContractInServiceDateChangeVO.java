package com.mikealbert.accounting.processor.vo;

import java.util.Objects;

public class ContractInServiceDateChangeVO {

	private Long id;
	
	public ContractInServiceDateChangeVO() {}
	public ContractInServiceDateChangeVO(Long id) {
		this.id = id;
	}	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContractInServiceDateChangeVO other = (ContractInServiceDateChangeVO) obj;
		return Objects.equals(id, other.id);
	}
	
	@Override
	public String toString() {
		return "ContractInServiceDateChangeVO [id=" + id + "]";
	}	

}

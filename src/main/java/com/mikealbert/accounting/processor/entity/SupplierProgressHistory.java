package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name="SUPPLIER_PROGRESS_HISTORY")
public class SupplierProgressHistory extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SPH_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPH_SEQ")
    @SequenceGenerator(name = "SPH_SEQ", sequenceName = "SPH_SEQ", allocationSize = 1)
	private Long sphId;

	public SupplierProgressHistory() {}
	
	public Long getSphId() {
		return sphId;
	}

	public void setSphId(Long sphId) {
		this.sphId = sphId;
	}

	@Override
	public String toString() {
		return "SupplierProgressHistory [sphId=" + sphId + "]";
	}

}
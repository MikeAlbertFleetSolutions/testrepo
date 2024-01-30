package com.mikealbert.accounting.processor.vo;

public class LeaseTerminationRequestVO {
	private Long quoId;

	public LeaseTerminationRequestVO(Long quoId) {
		this.quoId = quoId;
	}

	public Long getQuoId() {
		return quoId;
	}

	public LeaseTerminationRequestVO setQuoId(Long quoId) {
		this.quoId = quoId;
		return this;
	}

	@Override
	public String toString() {
		return "LeaseTerminiationVO [quoId=" + quoId + "]";
	}	
}

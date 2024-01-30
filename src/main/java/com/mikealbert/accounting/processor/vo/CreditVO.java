package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mikealbert.util.data.DataUtil;

public class CreditVO extends PayableTransactionVO<CreditVO, CreditLineVO> {	
	private static final long serialVersionUID = 6054175600740748632L;

	private Long parentExternalId;
	
	private String payableAccount;
		
	private List<CreditLineVO> lines;	
	
	public CreditVO() {
		super.setAutoApprove(true);
		this.lines = new ArrayList<>();		
	}

	public BigDecimal rolledUpLineRate() {
		return DataUtil.getNullSafeStream(this.getLines())
				.map(line -> line.getRate())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	public String rolledUpLineUnitNo() {
		return this.lines.get(0).getUnit();
	}
	
	public String rolledUpLineDepartment() {
		return this.lines.get(0).getDepartment();
	}	
	
	public String rolledUpLineBusinessUnit() {
		return this.lines.get(0).getBusinessUnit().getName();
	}
	
	public Long getParentExternalId() {
		return parentExternalId;
	}
	
	public void setParentExternalId(Long parentExternalId) {
		this.parentExternalId = parentExternalId;
	}
	
	public String externalAssetType() {
		return this.lines.get(0).getExternalAssetType();
	}
	
	public String getPayableAccount() {
		return payableAccount;
	}

	public CreditVO setPayableAccount(String payableAccount) {
		this.payableAccount = payableAccount;
		return this;		
	}

	@Override
	public List<CreditLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CreditVO setLines(List<? extends TransactionLineVO<CreditLineVO>> lines) {
		this.lines = (List<CreditLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "CreditVO [parentExternalId=" + parentExternalId + ", payableAccount=" + payableAccount + ", lines="
				+ lines + ", toString()=" + super.toString() + "]";
	}
		
}

package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mikealbert.util.data.DataUtil;

public class InvoiceVO extends PayableTransactionVO<InvoiceVO, InvoiceLineVO> {
	private static final long serialVersionUID = -5154452200138932792L;			
		
	private String payableAccount;
			
	private boolean createFromPurchaseOrder;

	private boolean grouped;

	private String groupNumber;
	
	private List<InvoiceLineVO> lines;	
		
	public InvoiceVO() {
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
		return this.lines.get(0).getBusinessUnit() == null ?  null : this.lines.get(0).getBusinessUnit().getName();
	}
	
	public String externalAssetType() {
		return this.lines.get(0).getExternalAssetType();
	}
	
	public String getPayableAccount() {
		return payableAccount;
	}

	public InvoiceVO setPayableAccount(String payableAccount) {
		this.payableAccount = payableAccount;
		return this;		
	}

	public boolean isCreateFromPurchaseOrder() {
		return createFromPurchaseOrder;
	}

	public InvoiceVO setCreateFromPurchaseOrder(boolean createFromPurchaseOrder) {
		this.createFromPurchaseOrder = createFromPurchaseOrder;
		return this;
	}

	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}

	@Override
	public List<InvoiceLineVO> getLines() {
		return this.lines;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InvoiceVO setLines(List<? extends TransactionLineVO<InvoiceLineVO>> lines) {
		this.lines = (List<InvoiceLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "InvoiceVO [createFromPurchaseOrder=" + createFromPurchaseOrder + ", groupNumber=" + groupNumber
				+ ", grouped=" + grouped + ", lines=" + lines + ", payableAccount=" + payableAccount  + ", toString()=" + super.toString() + "]";
	}
	
}

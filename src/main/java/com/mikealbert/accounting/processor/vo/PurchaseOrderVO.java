package com.mikealbert.accounting.processor.vo;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderVO extends PayableTransactionVO<PurchaseOrderVO, PurchaseOrderLineVO> {
	private static final long serialVersionUID = -7858999347886188498L; 
						
	private String unit;
	
	private boolean main;
	
	List<PurchaseOrderLineVO> lines;
	
	public PurchaseOrderVO() {
		super.setAutoApprove(true);
		this.lines = new ArrayList<>();
	}
	
	public String getUnit() {
		return unit;
	}

	public PurchaseOrderVO setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public boolean isMain() {
		return main;
	}

	public PurchaseOrderVO setMain(boolean main) {
		this.main = main;
		return this;
	}

	@Override
	public List<PurchaseOrderLineVO> getLines() {
		return this.lines;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PurchaseOrderVO setLines(List<? extends TransactionLineVO<PurchaseOrderLineVO>> lines) {
		this.lines = (List<PurchaseOrderLineVO>)lines;
		return this;
	}

	@Override
	public String toString() {
		return "PurchaseOrderVO [unit=" + unit + ", main=" + main + ", lines=" + lines + ", toString()="
				+ super.toString() + "]";
	}
	
}

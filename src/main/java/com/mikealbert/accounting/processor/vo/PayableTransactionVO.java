package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;

@SuppressWarnings("unchecked")
public abstract class PayableTransactionVO<T, P> extends TransactionVO<T, P> {
	
	private String vendor;	
	
	private Long vendorEaaId;
	
	private Long vendorAddressInternalId;	
	
	private String approvalDepartment;
	
	private boolean autoApprove;
	
	private BigDecimal discount;

    
	public String getVendor() {
		return vendor;
	}

	public T setVendor(String vendor) {
		this.vendor = vendor;
		return (T)this;
	}
	
	public Long getVendorEaaId() {
		return vendorEaaId;
	}

	public T setVendorEaaId(Long vendorEaaId) {
		this.vendorEaaId = vendorEaaId;
		return (T)this;
	}	
	
	public Long getVendorAddressInternalId() {
		return vendorAddressInternalId;
	}

	public T setVendorAddressInternalId(Long vendorAddressInternalId) {
		this.vendorAddressInternalId = vendorAddressInternalId;
		return (T)this;
	}	
	
	public String getApprovalDepartment() {
		return approvalDepartment;
	}

	public T setApprovalDepartment(String approvalDepartment) {
		this.approvalDepartment = approvalDepartment;
		return (T)this;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public T setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
		return (T)this;
	}	
	
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}    
}

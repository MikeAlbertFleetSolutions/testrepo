package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class AssetDisposalVO extends AssetBaseVO {
	
	private BigDecimal disposalProceeds;
	
	private Date disposalDate;
	
	private String disposalStatus;
	
	private Boolean disposalFlag;
	
	private String customForm;

	public BigDecimal getDisposalProceeds() {
		return disposalProceeds;
	}

	public AssetDisposalVO setDisposalProceeds(BigDecimal disposalProceeds) {
		this.disposalProceeds = disposalProceeds;
		return this;
	}

	public Date getDisposalDate() {
		return DateUtil.clone(disposalDate);
	}

	public AssetDisposalVO setDisposalDate(Date disposalDate) {
		this.disposalDate = DateUtil.clone(disposalDate);
		return this;
	}

	public String getDisposalStatus() {
		return disposalStatus;
	}

	public AssetDisposalVO setDisposalStatus(String disposalStatus) {
		this.disposalStatus = disposalStatus;
		return this;
	}

	public Boolean isDisposalFlag() {
		return disposalFlag;
	}

	public AssetDisposalVO setDisposalFlag(Boolean disposalFlag) {
		this.disposalFlag = disposalFlag;
		return this;
	}
	
	public String getCustomForm() {
		return customForm;
	}

	public AssetDisposalVO setCustomForm(String customForm) {
		this.customForm = customForm;
		return this;
	}
	
}

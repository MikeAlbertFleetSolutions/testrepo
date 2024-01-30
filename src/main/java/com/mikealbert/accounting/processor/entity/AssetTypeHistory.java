package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the ASSET_TYPE_HISTORY database table.
 * 
 */
@Entity
@Table(name="ASSET_TYPE_HISTORY")
public class AssetTypeHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long athId;
	
    @JoinColumn(name = "ASSET_ID", referencedColumnName = "ASSET_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetItem assetItem;

	@Column(name="ASSET_TYPE")
	private String assetType;

	@Column(name="START_DATE")
	private Date startDate;

	@Column(name="END_DATE")
	private Date endDate;

	@Column(name="OP_CODE")
	private String opCode;

	public AssetTypeHistory() {

	}

	public Long getAthId() {
		return athId;
	}

	public AssetItem getAssetItem() {
		return assetItem;
	}

	public AssetTypeHistory setAssetItem(AssetItem assetItem) {
		this.assetItem = assetItem;
		return this;
	}

	public String getAssetType() {
		return this.assetType;
	}

	public AssetTypeHistory setAssetType(String assetType) {
		this.assetType = assetType;
		return this;
	}

	public Date getStartDate() {
		return DateUtil.clone(this.startDate);
	}

	public AssetTypeHistory setStartDate(Date startDate) {
		this.startDate = DateUtil.clone(startDate);
		return this;
	}

	public String getOpCode() {
		return this.opCode;
	}

	public AssetTypeHistory setOpCode(String opCode) {
		this.opCode = opCode;
		return this;
	}

	public Date getEndDate() {
		return DateUtil.clone(this.endDate);
	}

	public AssetTypeHistory setEndDate(Date endDate) {
		this.endDate = DateUtil.clone(endDate);
		return this;
	}

}
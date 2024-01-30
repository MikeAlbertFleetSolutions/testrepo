package com.mikealbert.accounting.processor.vo;

import java.util.Map;

public class SuiteTalkDiagnostic {
	private boolean leaseRecord;
	private boolean leaseModificationRecord;
	private boolean leasePaymentRecord;
	private boolean unitCustomSegment;
	private boolean assetTypeRecord;
	private boolean assetRecord;
	private Map<String, String> dataCenterInfo;
	
	public SuiteTalkDiagnostic() {}

	public boolean isLeaseRecord() {
		return leaseRecord;
	}

	public SuiteTalkDiagnostic setLeaseRecord(boolean leaseRecord) {
		this.leaseRecord = leaseRecord;
		return this;
	}

	public boolean isLeaseModificationRecord() {
		return leaseModificationRecord;
	}

	public SuiteTalkDiagnostic setLeaseModificationRecord(boolean leaseModificationRecord) {
		this.leaseModificationRecord = leaseModificationRecord;
		return this;
	}

	public boolean isLeasePaymentRecord() {
		return leasePaymentRecord;
	}

	public SuiteTalkDiagnostic setLeasePaymentRecord(boolean leasePaymentRecord) {
		this.leasePaymentRecord = leasePaymentRecord;
		return this;
	}

	public boolean isUnitCustomSegment() {
		return unitCustomSegment;
	}

	public SuiteTalkDiagnostic setUnitCustomSegment(boolean unitCustomSegment) {
		this.unitCustomSegment = unitCustomSegment;
		return this;
	}

	public boolean isAssetTypeRecord() {
		return assetTypeRecord;
	}

	public SuiteTalkDiagnostic setAssetTypeRecord(boolean assetTypeRecord) {
		this.assetTypeRecord = assetTypeRecord;
		return this;
	}

	public boolean isAssetRecord() {
		return assetRecord;
	}

	public SuiteTalkDiagnostic setAssetRecord(boolean assetRecord) {
		this.assetRecord = assetRecord;
		return this;
	}

	public Map<String, String> getDataCenterInfo() {
		return dataCenterInfo;
	}

	public SuiteTalkDiagnostic setDataCenterInfo(Map<String, String> dataCenterInfo) {
		this.dataCenterInfo = dataCenterInfo;
		return this;
	}
}

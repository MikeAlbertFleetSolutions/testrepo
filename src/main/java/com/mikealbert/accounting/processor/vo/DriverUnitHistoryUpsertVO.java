package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.util.Date;

import com.mikealbert.accounting.processor.validation.DriverUnitValidator;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.constant.enumeration.ProductTypeEnum;
import com.mikealbert.util.data.DateUtil;

@DriverUnitValidator
public class DriverUnitHistoryUpsertVO implements Serializable {
    private static final long serialVersionUID = -4693182495245891578L;

    private AccountingNounEnum noun; 
	
	private EventEnum event;

	private Long aetId;
	
	private Long drvId;
	
	private Long dalId;
	
	private Long qmdId;
	
	private Long quoId;
	
	private Long clnId;
	
	private Long fmsId;
	
	private Long docId;

	private String unitNo;

	private String firstName;

	private String lastName;

	private String accountCode;

	private String businessAddressLine;

	private String costCenter;
	
	private String costCenterDesc;
	
	private AddressVO driverAddress;

	private AddressVO supplierAddress;

	private Date effectiveDate;
	
	private ProductTypeEnum productType;
	
	private String custRecordDuhDriverRechargeCode;
	
	private String custRecordDuhFleetRefNo;
	
	public DriverUnitHistoryUpsertVO() {
		this.driverAddress = new AddressVO();
		this.supplierAddress = new AddressVO();
	}

	public DriverUnitHistoryUpsertVO(Long aetId, AccountingNounEnum noun, EventEnum event, String entityId, Date effectiveDate) {
		
		this.aetId = aetId;
		this.event = event;
		this.effectiveDate = DateUtil.clone(effectiveDate);
		this.noun = noun;

		switch(noun) {
		case DRIVER:
			this.drvId = Long.valueOf(entityId);
			break;
		case DRIVER_ALLOCATION:
			this.dalId = Long.valueOf(entityId);
			break;
		case QUOTE:
			if(EventEnum.ACCEPT.equals(this.event))
				this.qmdId = Long.valueOf(entityId);
			else if(EventEnum.DRIVER_CHANGE.equals(this.event))
				this.quoId = Long.valueOf(entityId);
			break;
		case DOC:
			this.docId = Long.valueOf(entityId);
			break;
		case UNIT:
			this.fmsId = Long.valueOf(entityId);
			break;
		default:
			//Don't expect to drop here because we have controlled which nouns we are processing
			break;
		}
		
	}

	public AccountingNounEnum getNoun() {
		return noun;
	}

	public DriverUnitHistoryUpsertVO setNoun(AccountingNounEnum noun) {
		this.noun = noun;
		return this;
	}

	public EventEnum getEvent() {
		return event;
	}

	public DriverUnitHistoryUpsertVO setEvent(EventEnum event) {
		this.event = event;
		return this;
	}

	public Long getAetId() {
		return aetId;
	}

	public DriverUnitHistoryUpsertVO setAetId(Long aetId) {
		this.aetId = aetId;
		return this;
	}

	public Long getDrvId() {
		return drvId;
	}

	public DriverUnitHistoryUpsertVO setDrvId(Long drvId) {
		this.drvId = drvId;
		return this;
	}

	public Long getDalId() {
		return dalId;
	}

	public DriverUnitHistoryUpsertVO setDalId(Long dalId) {
		this.dalId = dalId;
		return this;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public DriverUnitHistoryUpsertVO setQmdId(Long qmdId) {
		this.qmdId = qmdId;
		return this;
	}

	public Long getQuoId() {
		return quoId;
	}

	public DriverUnitHistoryUpsertVO setQuoId(Long quoId) {
		this.quoId = quoId;
		return this;
	}

	public Long getClnId() {
		return clnId;
	}

	public DriverUnitHistoryUpsertVO setClnId(Long clnId) {
		this.clnId = clnId;
		return this;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public DriverUnitHistoryUpsertVO setFmsId(Long fmsId) {
		this.fmsId = fmsId;
		return this;
	}

	public Long getDocId() {
		return docId;
	}

	public DriverUnitHistoryUpsertVO setDocId(Long docId) {
		this.docId = docId;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public DriverUnitHistoryUpsertVO setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public DriverUnitHistoryUpsertVO setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public DriverUnitHistoryUpsertVO setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public DriverUnitHistoryUpsertVO setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getBusinessAddressLine() {
		return businessAddressLine;
	}

	public DriverUnitHistoryUpsertVO setBusinessAddressLine(String businessAddressLine) {
		this.businessAddressLine = businessAddressLine;
		return this;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public DriverUnitHistoryUpsertVO setCostCenter(String costCenter) {
		this.costCenter = costCenter;
		return this;
	}

	public String getCostCenterDesc() {
		return costCenterDesc;
	}

	public DriverUnitHistoryUpsertVO setCostCenterDesc(String costCenterDesc) {
		this.costCenterDesc = costCenterDesc;
		return this;
	}

	public AddressVO getDriverAddress() {
		return driverAddress;
	}

	public DriverUnitHistoryUpsertVO setDriverAddress(AddressVO driverAddress) {
		this.driverAddress = driverAddress;
		return this;
	}

	public AddressVO getSupplierAddress() {
		return supplierAddress;
	}

	public DriverUnitHistoryUpsertVO setSupplierAddress(AddressVO supplierAddress) {
		this.supplierAddress = supplierAddress;
		return this;
	}

	public Date getEffectiveDate() {
		return DateUtil.clone(effectiveDate);
	}

	public DriverUnitHistoryUpsertVO setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = DateUtil.clone(effectiveDate);
		return this;
	}

	public ProductTypeEnum getProductType() {
		return productType;
	}

	public DriverUnitHistoryUpsertVO setProductType(ProductTypeEnum productType) {
		this.productType = productType;
		return this;
	}	

	public String getCustRecordDuhDriverRechargeCode() {
		return custRecordDuhDriverRechargeCode;
	}

	public DriverUnitHistoryUpsertVO setCustRecordDuhDriverRechargeCode(String custRecordDuhDriverRechargeCode) {
		this.custRecordDuhDriverRechargeCode = custRecordDuhDriverRechargeCode;
		return this;
	}

	public String getCustRecordDuhFleetRefNo() {
		return custRecordDuhFleetRefNo;
	}

	public DriverUnitHistoryUpsertVO setCustRecordDuhFleetRefNo(String custRecordDuhFleetRefNo) {
		this.custRecordDuhFleetRefNo = custRecordDuhFleetRefNo;
		return this;
	}

	@Override
	public String toString() {
		return "DriverUnitHistoryUpsertVO [noun=" + noun + ", event=" + event + ", aetId=" + aetId + ", drvId=" + drvId
				+ ", dalId=" + dalId + ", qmdId=" + qmdId + ", quoId=" + quoId + ", clnId=" + clnId + ", fmsId=" + fmsId
				+ ", docId=" + docId + ", unitNo=" + unitNo + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", accountCode=" + accountCode + ", businessAddressLine=" + businessAddressLine + ", costCenter="
				+ costCenter + ", costCenterDesc=" + costCenterDesc + ", driverAddress=" + driverAddress
				+ ", supplierAddress=" + supplierAddress + ", effectiveDate=" + effectiveDate + ", productType="
				+ productType + "]";
	}
	
}
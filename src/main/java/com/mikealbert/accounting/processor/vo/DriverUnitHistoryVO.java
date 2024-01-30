package com.mikealbert.accounting.processor.vo;

import java.util.Date;

import com.mikealbert.util.data.DateUtil;

public class DriverUnitHistoryVO {

    private String internalId;

    private String externalId;

    private String accountCode;

    private String unitNo;

    private Long driverId;

    private String driverFirstName;

    private String driverLastName;

    private String costCenterCode;

    private String costCenterDescription;

    private String driverRechargeCode;
    
    private String driverFleetNo;

    private String driverAddressState;    

    private Date effectiveDate;


    public DriverUnitHistoryVO() {}

    public String getInternalId() {
        return internalId;
    }

    public DriverUnitHistoryVO setInternalId(String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public DriverUnitHistoryVO setExternalId(String externalId) {
        this.externalId = externalId;
        return this;        
    }
      
    public String getAccountCode() {
        return accountCode;
    }

    public DriverUnitHistoryVO setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public DriverUnitHistoryVO setUnitNo(String unitNo) {
        this.unitNo = unitNo;
        return this;
    }

    public Long getDriverId() {
        return driverId;
    }

    public DriverUnitHistoryVO setDriverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public DriverUnitHistoryVO setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
        return this;        
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public DriverUnitHistoryVO setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
        return this;        
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public DriverUnitHistoryVO setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
        return this;        
    }

    public String getCostCenterDescription() {
        return costCenterDescription;
    }

    public DriverUnitHistoryVO setCostCenterDescription(String costCenterDescription) {
        this.costCenterDescription = costCenterDescription;
        return this;        
    }

    public String getDriverRechargeCode() {
        return driverRechargeCode;
    }

    public DriverUnitHistoryVO setDriverRechargeCode(String driverRechargeCode) {
        this.driverRechargeCode = driverRechargeCode;
        return this;        
    }
    
	public String getDriverFleetNo() {
		return driverFleetNo;
	}

    public String getDriverAddressState() {
        return driverAddressState;
    }

    public DriverUnitHistoryVO setDriverAddressState(String driverAddressState) {
        this.driverAddressState = driverAddressState;
        return this;
    }     

	public DriverUnitHistoryVO setDriverFleetNo(String driverFleetNo) {
		this.driverFleetNo = driverFleetNo;
		return this;
	}

    public Date getEffectiveDate() {
        return DateUtil.clone(effectiveDate);
    }

    public DriverUnitHistoryVO setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = DateUtil.clone(effectiveDate);
        return this;
    }
         
}

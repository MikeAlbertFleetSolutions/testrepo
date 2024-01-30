package com.mikealbert.accounting.processor.vo;

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("unchecked")
public abstract class ReceivableTransactionLineVO<T> extends TransactionLineVO<T> {	
    private Long docId;
    private Long docLineId;
    private BigDecimal linePaidAmount;
    private String maType;
    private Long driverId;
    private String driverFirstName;
    private String driverLastName;
    private String driverState;
    private String driverCostCenter;
    private String driverCostCenterDescription;
    private String driverRechargeCode;
    private String driverFleetRefNo;
    private Date monthServiceDate;
	private Date transactionLineDate;

    public BigDecimal getLinePaidAmount() {
        return linePaidAmount;
    }

    public T setLinePaidAmount(BigDecimal linePaidAmount) {
        this.linePaidAmount = linePaidAmount;
        return (T)this;
    }

    public Long getDocId() {
        return docId;
    }

    public T setDocId(Long docId) {
        this.docId = docId;
        return (T)this;
    }

    public Long getDocLineId() {
        return docLineId;
    }

    public T setDocLineId(Long docLineId) {
        this.docLineId = docLineId;
        return (T)this;
    }

    public String getMaType() {
        return maType;
    }

    public T setMaType(String maType) {
        this.maType = maType;
        return (T)this;
    }

    public Long getDriverId() {
        return driverId;
    }

    public T setDriverId(Long driverId) {
        this.driverId = driverId;
        return (T)this;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public T setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
        return (T)this;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public T setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
        return (T)this;
    }

    public String getDriverState() {
        return driverState;
    }

    public T setDriverState(String driverState) {
        this.driverState = driverState;
        return (T)this;
    }

    public String getDriverCostCenter() {
        return driverCostCenter;
    }

    public T setDriverCostCenter(String driverCostCenter) {
        this.driverCostCenter = driverCostCenter;
        return (T)this;
    }

    public String getDriverCostCenterDescription() {
        return driverCostCenterDescription;
    }

    public T setDriverCostCenterDescription(String driverCostCenterDescription) {
        this.driverCostCenterDescription = driverCostCenterDescription;
        return (T)this;
    }

    public String getDriverRechargeCode() {
        return driverRechargeCode;
    }

    public T setDriverRechargeCode(String driverRechargeCode) {
        this.driverRechargeCode = driverRechargeCode;
        return (T)this;
    }

    public String getDriverFleetRefNo() {
        return driverFleetRefNo;
    }

    public T setDriverFleetRefNo(String driverFleetRefNo) {
        this.driverFleetRefNo = driverFleetRefNo;
        return (T)this;
    }

    public Date getMonthServiceDate() {
        return monthServiceDate;
    }

    public T setMonthServiceDate(Date monthServiceDate) {
        this.monthServiceDate = monthServiceDate;
        return (T)this;
    }

    public Date getTransactionLineDate() {
        return transactionLineDate;
    }

    public T setTransactionLineDate(Date transactionLineDate) {
        this.transactionLineDate = transactionLineDate;
        return (T)this;
    }   
    
}

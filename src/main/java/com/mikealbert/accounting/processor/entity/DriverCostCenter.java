package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the DRIVER_COST_CENTRES database table.
 * 
 */
@Entity
@Table(name="DRIVER_COST_CENTRES")
public class DriverCostCenter extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRCC_SEQ")    
    @SequenceGenerator(name="DRCC_SEQ", sequenceName="DRCC_SEQ", allocationSize=1)      
    @Column(name = "DRCC_ID")
    private Long id;

    @Column(name = "COCC_C_ID")
    private Long cId;
    
    @Column(name = "COCC_ACCOUNT_TYPE")
    private String accountType;
    
    @Column(name = "COCC_ACCOUNT_CODE")
    private String accountCode;
    
    @Column(name = "DRV_DRV_ID")
    private Long drvId;    

	@Column(name="COST_CENTRE_CODE")
	private String costCenterCode;    
	
	@Column(name="EFFECTIVE_TO_DATE")
	private Date effectiveToDate;    
    
	public DriverCostCenter() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Long getDrvId() {
		return drvId;
	}

	public void setDrvId(Long drvId) {
		this.drvId = drvId;
	}

	public String getCostCenterCode() {
		return costCenterCode;
	}

	public void setCostCenterCode(String costCenterCode) {
		this.costCenterCode = costCenterCode;
	}

	public Date getEffectiveToDate() {
		return DateUtil.clone(effectiveToDate);
	}

	public void setEffectiveToDate(Date effectiveToDate) {
		this.effectiveToDate = DateUtil.clone(effectiveToDate);
	}
	
	
}
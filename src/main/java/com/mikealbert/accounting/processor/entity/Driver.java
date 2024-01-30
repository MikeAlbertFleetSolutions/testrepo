package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.util.data.DateUtil;

/**
 * Mapped to DRIVERS Table
 * @author maheshwary
 */
@Entity
@Table(name = "DRIVERS")
public class Driver extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
   
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRV_SEQ")    
    @SequenceGenerator(name="DRV_SEQ", sequenceName="DRV_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "DRV_ID")
    private Long drvId;

    @Basic(optional = false)    
    @Size(max = 30)
    @Column(name = "DRIVER_FORENAME")
    private String driverForename;

    @Basic(optional = false)    
    @Size(max = 40)
    @Column(name = "DRIVER_SURNAME")
    private String driverSurname;

    @Size(max = 30)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @Size(max = 30)
    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "DATE_JOINED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateJoined;

    @Size(max = 1)
    @Column(name = "POOL_MGR")
    private String poolManager;

    @Size(max = 80)
    @Column(name = "DRIVER_MIDDLENAME")
    private String driverMiddlename;
    
    @Basic(optional = false)       
    @Column(name = "TITLE")    
    private String title;

    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
    
	public Driver() {}

    public Driver(Long drvId) {
        this.drvId = drvId;
    }

    public Driver(Long drvId, ExternalAccount externalAccount, String driverForename, String driverSurname) {
        this.drvId = drvId;
        this.externalAccount = externalAccount;
        this.driverForename = driverForename;
        this.driverSurname = driverSurname;
    }

    public Long getDrvId() {
        return drvId;
    }

    public void setDrvId(Long drvId) {
        this.drvId = drvId;
    }

    public String getDriverForename() {
        return driverForename;
    }

    public void setDriverForename(String driverForename) {
        this.driverForename = driverForename;
    }

    public String getDriverSurname() {
        return driverSurname;
    }

    public void setDriverSurname(String driverSurname) {
        this.driverSurname = driverSurname;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Date getDateJoined() {
        return DateUtil.clone(dateJoined);
    }

    public void setDateJoined(Date dateJoined) {
        this.dateJoined = DateUtil.clone(dateJoined);
    }

    public String getDriverMiddlename() {
        return driverMiddlename;
    }

    public void setDriverMiddlename(String driverMiddlename) {
        this.driverMiddlename = driverMiddlename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExternalAccount getExternalAccount() {
        return externalAccount;
    }

    public void setExternalAccount(ExternalAccount externalAccount) {
        this.externalAccount = externalAccount;
    }

	public String getPoolManager() {
		return poolManager;
	}

	public void setPoolManager(String poolManager) {
		this.poolManager = poolManager;
	}

}


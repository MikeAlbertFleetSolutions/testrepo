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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.util.data.DateUtil;

/**
 * Mapped to DRIVER_ALLOCATIONS table
 * @author maheshwary
 */
@Entity
@Table(name = "DRIVER_ALLOCATIONS")
public class DriverAllocation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DAL_SEQ")    
    @SequenceGenerator(name="DAL_SEQ", sequenceName="DAL_SEQ", allocationSize=1)    
    @Basic(optional = false)
    @NotNull
    @Column(name = "DAL_ID")
    private Long dalId;
    
    @Size(max = 1)
    @Column(name = "FUEL_IND_FLAG")
    private String fuelIndFlag;
    
    @Column(name = "FROM_ODO_READING")
    private Long fromOdoReading;
    
    @Column(name = "FROM_DATE")
    private Date allocationDate;
    
    @Column(name = "TO_DATE")
    private Date deallocationDate;
    
    @Size(max = 25)
    @Column(name = "OP_CODE")
    private String opCode;
    
    @Size(max = 100)
    @Column(name = "LAST_ACTION_USER")
    private String lastActionUser;
    
    @Column(name = "LAST_ACTION_DATE")
    private Date lastActionDate;
    
    @Size(max = 10)   
    @Column(name = "ODO_UOM")    
    private String odoUom;
          
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private FleetMaster fleetMaster;
    
    @JoinColumn(name = "DRV_DRV_ID", referencedColumnName = "DRV_ID")          
    @ManyToOne(optional = false, fetch = FetchType.EAGER)  
    private Driver driver;    
    
    public DriverAllocation() {}

    public DriverAllocation(Long dalId) {
        this.dalId = dalId;
    }
    
    public Long getDalId() {
        return dalId;
    }

    public void setDalId(Long dalId) {
        this.dalId = dalId;
    }

    public String getFuelIndFlag() {
        return fuelIndFlag;
    }

    public void setFuelIndFlag(String fuelIndFlag) {
        this.fuelIndFlag = fuelIndFlag;
    }

    public Long getFromOdoReading() {
        return fromOdoReading;
    }

    public void setFromOdoReading(Long fromOdoReading) {
        this.fromOdoReading = fromOdoReading;
    }

    public Date getAllocationDate() {
        return DateUtil.clone(allocationDate);
    }

    public void setAllocationDate(Date fromDate) {
        this.allocationDate = DateUtil.clone(fromDate);
    }

    public Date getDeallocationDate() {
        return DateUtil.clone(deallocationDate);
    }

    public void setDeallocationDate(Date toDate) {
        this.deallocationDate = DateUtil.clone(toDate);
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getLastActionUser() {
        return lastActionUser;
    }

    public void setLastActionUser(String lastActionUser) {
        this.lastActionUser = lastActionUser;
    }

    public Date getLastActionDate() {
        return DateUtil.clone(lastActionDate);
    }

    public void setLastActionDate(Date lastActionDate) {
        this.lastActionDate = DateUtil.clone(lastActionDate);
    }

	public String getOdoUom() {
		return odoUom;
	}

	public void setOdoUom(String odoUom) {
		this.odoUom = odoUom;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}
	
    public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
}

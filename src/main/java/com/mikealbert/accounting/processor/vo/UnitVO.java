package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.mikealbert.constant.accounting.enumeration.EventEnum;

public class UnitVO implements Serializable{

	private static final long serialVersionUID = -7073068601441119975L;

	private Long fmsId;

	private String unitNo;

	private String vin;

	private String year;

	private String make;

	private String model;
	
	private String modelTypeDesc;

	private String fuelType;

	private Long gvr;

	private BigDecimal horsePower;

	private BigDecimal msrp;

	private String newUsed;

	private String deliverAccCode;

	private int contractCount;
	
	private String equipmentFlag;

	private BigDecimal cbv;

	private String vehicleClassification;

	private String plbType;
	
	public UnitVO() {}

	public UnitVO(Long fmsId, String unitNo, String vin, String year, String make, String model, String modelTypeDesc, String fuelType,
			Long gvr, BigDecimal horsePower, BigDecimal msrp, String newUsed, String deliverAccCode,
			int contractCount, String equipmentFlag, EventEnum sourceContext) {
		this.fmsId = fmsId;
		this.unitNo = unitNo;
		this.vin = vin;
		this.year = year;
		this.make = make;
		this.model = model;
		this.modelTypeDesc = modelTypeDesc;
		this.fuelType = fuelType;
		this.gvr = gvr;
		this.horsePower = horsePower;
		this.msrp = msrp;
		this.newUsed = newUsed;
		this.deliverAccCode = deliverAccCode;
		this.contractCount = contractCount;
		this.equipmentFlag = equipmentFlag;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public UnitVO setFmsId(Long fmsId) {
		this.fmsId = fmsId;
		return this;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public UnitVO setUnitNo(String unitNo) {
		this.unitNo = unitNo;
		return this;
	}

	public String getVin() {
		return vin;
	}

	public UnitVO setVin(String vin) {
		this.vin = vin;
		return this;
	}

	public String getYear() {
		return year;
	}

	public UnitVO setYear(String year) {
		this.year = year;
		return this;
	}

	public String getMake() {
		return make;
	}

	public UnitVO setMake(String make) {
		this.make = make;
		return this;
	}

	public String getModel() {
		return model;
	}

	public UnitVO setModel(String model) {
		this.model = model;
		return this;
	}

	public String getModelTypeDesc() {
		return modelTypeDesc;
	}

	public UnitVO setModelTypeDesc(String modelTypeDesc) {
		this.modelTypeDesc = modelTypeDesc;
		return this;
	}

	public String getFuelType() {
		return fuelType;
	}

	public UnitVO setFuelType(String fuelType) {
		this.fuelType = fuelType;
		return this;
	}

	public Long getGvr() {
		return gvr;
	}

	public UnitVO setGvr(Long gvr) {
		this.gvr = gvr;
		return this;
	}

	public BigDecimal getHorsePower() {
		return horsePower;
	}

	public UnitVO setHorsePower(BigDecimal horsePower) {
		this.horsePower = horsePower;
		return this;
	}

	public BigDecimal getMsrp() {
		return msrp;
	}

	public UnitVO setMsrp(BigDecimal msrp) {
		this.msrp = msrp;
		return this;
	}

	public String getNewUsed() {
		return newUsed;
	}

	public UnitVO setNewUsed(String newUsed) {
		this.newUsed = newUsed;
		return this;
	}

	public String getDeliverAccCode() {
		return deliverAccCode;
	}

	public UnitVO setDeliverAccCode(String deliverAccCode) {
		this.deliverAccCode = deliverAccCode;
		return this;
	}

	public int getContractCount() {
		return contractCount;
	}

	public UnitVO setContractCount(int contractCount) {
		this.contractCount = contractCount;
		return this;
	}

	public String getEquipmentFlag() {
		return equipmentFlag;
	}

	public UnitVO setEquipmentFlag(String equipmentFlag) {
		this.equipmentFlag = equipmentFlag;
		return this;
	}

	public BigDecimal getCbv() {
		return cbv;
	}

	public UnitVO setCbv(BigDecimal cbv) {
		this.cbv = cbv;
		return this;
	}
	
	public String getVehicleClassification() {
		return vehicleClassification;
	}

	public UnitVO setVehicleClassification(String vehicleClassification) {
		this.vehicleClassification = vehicleClassification;
		return this;
	}
	
	public String getPlbType() {
		return plbType;
	}

	public UnitVO setPlbType(String plbType) {
		this.plbType = plbType;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fmsId == null) ? 0 : fmsId.hashCode());
		result = prime * result + ((unitNo == null) ? 0 : unitNo.hashCode());
		result = prime * result + ((vin == null) ? 0 : vin.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		result = prime * result + ((make == null) ? 0 : make.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((modelTypeDesc == null) ? 0 : modelTypeDesc.hashCode());
		result = prime * result + ((fuelType == null) ? 0 : fuelType.hashCode());
		result = prime * result + ((gvr == null) ? 0 : gvr.hashCode());
		result = prime * result + ((horsePower == null) ? 0 : horsePower.hashCode());
		result = prime * result + ((msrp == null) ? 0 : msrp.hashCode());
		result = prime * result + ((newUsed == null) ? 0 : newUsed.hashCode());
		result = prime * result + ((deliverAccCode == null) ? 0 : deliverAccCode.hashCode());
		result = prime * result + contractCount;
		result = prime * result + ((equipmentFlag == null) ? 0 : equipmentFlag.hashCode());
		result = prime * result + ((cbv == null) ? 0 : cbv.hashCode());
		result = prime * result + ((vehicleClassification == null) ? 0 : vehicleClassification.hashCode());
		result = prime * result + ((plbType == null) ? 0 : plbType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitVO other = (UnitVO) obj;
		if (fmsId == null) {
			if (other.fmsId != null)
				return false;
		} else if (!fmsId.equals(other.fmsId))
			return false;
		if (unitNo == null) {
			if (other.unitNo != null)
				return false;
		} else if (!unitNo.equals(other.unitNo))
			return false;
		if (vin == null) {
			if (other.vin != null)
				return false;
		} else if (!vin.equals(other.vin))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		if (make == null) {
			if (other.make != null)
				return false;
		} else if (!make.equals(other.make))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (modelTypeDesc == null) {
			if (other.modelTypeDesc != null)
				return false;
		} else if (!modelTypeDesc.equals(other.modelTypeDesc))
			return false;
		if (fuelType == null) {
			if (other.fuelType != null)
				return false;
		} else if (!fuelType.equals(other.fuelType))
			return false;
		if (gvr == null) {
			if (other.gvr != null)
				return false;
		} else if (!gvr.equals(other.gvr))
			return false;
		if (horsePower == null) {
			if (other.horsePower != null)
				return false;
		} else if (!horsePower.equals(other.horsePower))
			return false;
		if (msrp == null) {
			if (other.msrp != null)
				return false;
		} else if (!msrp.equals(other.msrp))
			return false;
		if (newUsed == null) {
			if (other.newUsed != null)
				return false;
		} else if (!newUsed.equals(other.newUsed))
			return false;
		if (deliverAccCode == null) {
			if (other.deliverAccCode != null)
				return false;
		} else if (!deliverAccCode.equals(other.deliverAccCode))
			return false;
		if (contractCount != other.contractCount)
			return false;
		if (equipmentFlag == null) {
			if (other.equipmentFlag != null)
				return false;
		} else if (!equipmentFlag.equals(other.equipmentFlag))
			return false;
		if (cbv == null) {
			if (other.cbv != null)
				return false;
		} else if (!cbv.equals(other.cbv))
			return false;
		if (vehicleClassification == null) {
			if (other.vehicleClassification != null)
				return false;
		} else if (!vehicleClassification.equals(other.vehicleClassification))
			return false;
		if (plbType == null) {
			if (other.plbType != null)
				return false;
		} else if (!plbType.equals(other.plbType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UnitVO [fmsId=" + fmsId + ", unitNo=" + unitNo + ", vin=" + vin + ", year=" + year + ", make=" + make
				+ ", model=" + model + ", modelTypeDesc=" + modelTypeDesc + ", fuelType=" + fuelType + ", gvr=" + gvr
				+ ", horsePower=" + horsePower + ", msrp=" + msrp + ", newUsed=" + newUsed + ", deliverAccCode="
				+ deliverAccCode + ", contractCount=" + contractCount + ", equipmentFlag=" + equipmentFlag + ", cbv="
				+ cbv + ", vehicleClassification=" + vehicleClassification + ", plbType=" + plbType + "]";
	}
}
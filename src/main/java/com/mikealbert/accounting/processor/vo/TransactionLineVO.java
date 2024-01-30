package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

@SuppressWarnings("unchecked")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME, 
		include = JsonTypeInfo.As.PROPERTY, 
		property = "type")
@JsonSubTypes({ 
	@Type(value = CreditLineVO.class, name = "creditLine"),
	@Type(value = InvoiceLineVO.class, name = "invoiceLine"), 
	@Type(value = PurchaseOrderLineVO.class, name = "purchaseOrderLine"),
	@Type(value = ClientInvoiceLineVO.class, name = "clientInvoiceLine"),
	@Type(value = ClientPaymentLineVO.class, name = "clientPaymentLine"),
	@Type(value = ClientCreditMemoLineVO.class, name = "clientCreditMemoLine"),
	@Type(value = ClientDepositApplicationLineVO.class, name = "clientDepositApplicationLine")	
})
public abstract class TransactionLineVO<T> implements Serializable {
	private static final long serialVersionUID = -883971449254482342L;
	
	private String glCode;
	
	private String item;
	
	private BigDecimal quantity;
	
	private BigDecimal rate;

	private BigDecimal amount;	

	private String department;
	
	private BusinessUnitEnum businessUnit;
	
	private String location;
	
	private String description;
	
	private String unit;

	public String getGlCode() {
		return glCode;
	}

	public T setGlCode(String glCode) {
		this.glCode = glCode;
		return (T)this;
	}
	
	public String getItem() {
		return item;
	}

	public T setItem(String item) {
		this.item = item;
		return (T)this;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public T setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
		return (T)this;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public T setRate(BigDecimal rate) {
		this.rate = rate;
		return (T)this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public T setAmount(BigDecimal amount) {
		this.amount = amount;
		return (T)this;
	}

	public String getDepartment() {
		return department;
	}

	public T setDepartment(String department) {
		this.department = department;
		return (T)this;
	}

	public BusinessUnitEnum getBusinessUnit() {
		return businessUnit;
	}

	public T setBusinessUnit(BusinessUnitEnum businessUnit) {
		this.businessUnit = businessUnit;
		return (T)this;
	}

	public String getLocation() {
		return location;
	}

	public T setLocation(String location) {
		this.location = location;
		return (T)this;
	}

	public String getDescription() {
		return description;
	}

	public T setDescription(String description) {
		this.description = description;
		return (T)this;
	}

	public String getUnit() {
		return unit;
	}

	public T setUnit(String unit) {
		this.unit = unit;
		return (T)this;
	}
		
	public abstract ProductEnum getProductCode();
	
	public abstract TransactionVO<?,?> getHeader();

	@Override
	final public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((businessUnit == null) ? 0 : businessUnit.hashCode());
		result = prime * result + ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((glCode == null) ? 0 : glCode.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	@Override
	final public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionLineVO<T> other = (TransactionLineVO<T>) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (businessUnit != other.businessUnit)
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (glCode == null) {
			if (other.glCode != null)
				return false;
		} else if (!glCode.equals(other.glCode))
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (!rate.equals(other.rate))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TransactionLineVO [amount=" + amount + ", businessUnit=" + businessUnit + ", department=" + department
				+ ", description=" + description + ", glCode=" + glCode + ", item=" + item + ", location=" + location
				+ ", quantity=" + quantity + ", rate=" + rate + ", unit=" + unit + "]";
	}
		
}

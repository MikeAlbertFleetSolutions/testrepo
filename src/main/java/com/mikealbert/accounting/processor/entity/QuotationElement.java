package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the DOCL database table.
 * 
 */
@Entity
@Table(name="QUOTATION_ELEMENTS")
public class QuotationElement implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QEL_SEQ")
	@SequenceGenerator(name="QEL_SEQ", sequenceName="QEL_SEQ", allocationSize=1)
	
	@Column(name="QEL_ID")
	private BigDecimal qelId;	
	
    @Column(name="RENTAL")
    private BigDecimal rental;
	
    @Column(name="RESIDUAL_VALUE")
    private BigDecimal residualValue;
	
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)	
	private QuotationModel quotationModel;
    
    @JoinColumn(name = "QDA_QDA_ID", referencedColumnName = "QDA_ID", insertable=false, updatable=false)
    @OneToOne(fetch = FetchType.LAZY)	
    private QuotationDealerAccessory quotationDealerAccessory;
    
    @JoinColumn(name = "LEL_LEL_ID", referencedColumnName = "LEL_ID", insertable=false, updatable=false)
    @ManyToOne(fetch = FetchType.LAZY)
    private LeaseElement leaseElement;
    
	public BigDecimal getQelId() {
		return qelId;
	}

	public void setQelId(BigDecimal qelId) {
		this.qelId = qelId;
	}

	public BigDecimal getRental() {
		return rental;
	}

	public void setRental(BigDecimal rental) {
		this.rental = rental;
	}

	public BigDecimal getResidualValue() {
		return residualValue;
	}

	public void setResidualValue(BigDecimal residualValue) {
		this.residualValue = residualValue;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public QuotationDealerAccessory getQuotationDealerAccessory() {
		return quotationDealerAccessory;
	}

	public void setQuotationDealerAccessory(QuotationDealerAccessory quotationDealerAccessory) {
		this.quotationDealerAccessory = quotationDealerAccessory;
	}

	public LeaseElement getLeaseElement() {
		return leaseElement;
	}

	public void setLeaseElement(LeaseElement leaseElement) {
		this.leaseElement = leaseElement;
	}
	
}
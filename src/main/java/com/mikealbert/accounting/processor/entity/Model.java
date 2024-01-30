package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Mapped to MODELS table
 */
@Entity
@Table(name = "MODELS")
public class Model extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 3047256619348034151L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MDL_SEQ")
    @SequenceGenerator(name="MDL_SEQ", sequenceName="MDL_SEQ", allocationSize=1)
    @Column(name = "MDL_ID")
    private Long mdlId;
    
    @Column(name="MODEL_DESC", nullable=false, insertable=false, updatable=false, length=240)
    private String modelDescription;
         
    @JoinColumn(name = "MAK_MAK_ID", referencedColumnName = "MAK_ID")
    @ManyToOne
    private Make make;     

    @JoinColumn(name = "MMY_MMY_ID", referencedColumnName = "MMY_ID")
    @ManyToOne
    private ModelMarkYear modelMarkYear;    

    @JoinColumn(name = "MRG_MRG_ID", referencedColumnName = "MRG_ID")
    @ManyToOne
    private MakeModelRange makeModelRange;

    public Model() {}
    
	public Long getMdlId() {
		return mdlId;
	}

	public void setMdlId(Long mdlId) {
		this.mdlId = mdlId;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}

	public ModelMarkYear getModelMarkYear() {
		return modelMarkYear;
	}

	public void setModelMarkYear(ModelMarkYear modelMarkYear) {
		this.modelMarkYear = modelMarkYear;
	}

	public MakeModelRange getMakeModelRange() {
		return makeModelRange;
	}

	public void setMakeModelRange(MakeModelRange makeModelRange) {
		this.makeModelRange = makeModelRange;
	}
    
    
    
}

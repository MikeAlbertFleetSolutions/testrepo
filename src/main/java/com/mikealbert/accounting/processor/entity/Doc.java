package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.mikealbert.util.data.DateUtil;


/**
 * Mapped to DOC table
 */
@Entity
@Table(name="DOC")
public class Doc extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DOC_SEQ")    
    @SequenceGenerator(name="DOC_SEQ", sequenceName="DOC_SEQ", allocationSize=1)	
	@Column(name="DOC_ID")
	private Long docId;

	@Column(name="DOC_NO")
	private String docNo;
	
	@Column(name="DOC_DATE")
	private Date docDate;
	
	@Column(name="ACCOUNT_CODE")
	private String accountCode;
	
	@Column(name="DUE_DATE")
	private Date dueDate;
	
	@Column(name="TOTAL_DOC_PRICE")
	private BigDecimal totalDocPrice;
	
	@Column(name="DOC_TYPE")
	private String docType;

	@Column(name="SOURCE_CODE")
	private String sourceCode;
	
	@Column(name="GENERIC_EXT_ID")
	private Long genericExtId;
	
	@Column(name="GL_ACC")
	private Long glAcc;
	
	@Column(name="UPDATE_CONTROL_CODE")
	private String updateControlCode;
	
	@Column(name="POSTED_DATE")
	private Date postedDate;
	
	@Column(name="DOC_STATUS")
	private String docStatus;	
	
	@Column(name="OP_CODE")
	private String opCode;	
	
	@OneToMany(mappedBy="doc")
	private List<Docl> docls;
	
	@OneToMany(mappedBy="doc")
	private List<Dist> dists;	

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public Date getDocDate() {
		return DateUtil.clone(docDate);
	}

	public void setDocDate(Date docDate) {
		this.docDate = DateUtil.clone(docDate);
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Date getDueDate() {
		return DateUtil.clone(dueDate);
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = DateUtil.clone(dueDate);
	}

	public BigDecimal getTotalDocPrice() {
		return totalDocPrice;
	}

	public void setTotalDocPrice(BigDecimal totalDocPrice) {
		this.totalDocPrice = totalDocPrice;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public Long getGenericExtId() {
		return genericExtId;
	}

	public void setGenericExtId(Long genericExtId) {
		this.genericExtId = genericExtId;
	}

	public Long getGlAcc() {
		return glAcc;
	}

	public void setGlAcc(Long glAcc) {
		this.glAcc = glAcc;
	}
	
	public String getUpdateControlCode() {
		return updateControlCode;
	}

	public void setUpdateControlCode(String updateControlCode) {
		this.updateControlCode = updateControlCode;
	}

	public Date getPostedDate() {
		return DateUtil.clone(postedDate);
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = DateUtil.clone(postedDate);
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public List<Docl> getDocls() {
		return docls;
	}

	public void setDocls(List<Docl> docls) {
		this.docls = docls;
	}

	public List<Dist> getDists() {
		return dists;
	}

	public void setDists(List<Dist> dists) {
		this.dists = dists;
	}
		
}